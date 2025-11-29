package com.snapshot.chonect.api.graphql;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.snapshot.chonect.domain.models.CanvasEntity;
import com.snapshot.chonect.domain.models.ConnectionEntity;
import com.snapshot.chonect.domain.models.ElementEntity;
import com.snapshot.chonect.domain.models.PageEntity;
import com.snapshot.chonect.domain.models.ProjectEntity;
import com.snapshot.chonect.infrastructure.services.ProjectService;
import com.snapshot.chonect.domain.repositories.ElementRepository;
import com.snapshot.chonect.utils.objects.ElementLayer;
import com.snapshot.chonect.utils.objects.PageConfig;

import lombok.RequiredArgsConstructor;

// francamente nunca e hecho una estructura de GraphQL, 
// pero eso es lo que se me ocurrio, tan solo por amor al arte,

// Carlos yo se que vas a ver esto, si quieres documentar puedes usar
// la libreria de llamada: GraphQL DGS Code Generation, se encuenta en https://start.spring.io/
// sincerramente te compadesco jajajaj

// que viva el rey, la reina y la madre que pario el bicho siuuuuu
@Controller
@RequiredArgsConstructor
public class GraphqlController {

    private static final Logger logger = LoggerFactory.getLogger(GraphqlController.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final com.snapshot.chonect.infrastructure.services.UserServices userServices;
    private final ProjectService projectService;
    private final ElementRepository elementRepository;

    // esta etiqueta @QueryMapping es usada para mapear las consultas a la API
    // GraphQL
    @QueryMapping
    @Transactional
    public ProjectEntity findProjectById(@Argument @NonNull String id) {
        try {
            // Convertir id de String a UUID
            UUID projectId;
            try {
                projectId = UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("ID de proyecto inválido: " + id);
            }

            ProjectEntity project = projectService.getByIdWithPages(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

            // Forzar inicialización de relaciones Lazy
            if (project.getPages() != null) {
                project.getPages().forEach(page -> {
                    if (page.getCanvas() != null) {
                        if (page.getCanvas().getElements() != null)
                            page.getCanvas().getElements().size();
                        if (page.getCanvas().getConnections() != null)
                            page.getCanvas().getConnections().size();
                    }
                });
            }

            logger.info("Proyecto encontrado y procesado, retornando...");
            return project;
        } catch (Exception e) {
            logger.error("Error buscando proyecto: ", e);
            throw e;
        }
    }

    @QueryMapping
    @Transactional
    public List<ProjectEntity> findAllProjects() {
        List<ProjectEntity> projects = projectService.getAllWithPages();
        // Inicializar relaciones lazy
        projects.forEach(project -> {
            if (project.getPages() != null) {
                project.getPages().forEach(page -> {
                    if (page.getCanvas() != null) {
                        if (page.getCanvas().getElements() != null)
                            page.getCanvas().getElements().size();
                        if (page.getCanvas().getConnections() != null)
                            page.getCanvas().getConnections().size();
                    }
                });
            }
        });
        return projects;
    }

    private PageEntity createPageFromInput(PageInput pageInput, ProjectEntity project) {
        PageEntity page = new PageEntity();
        page.setPageName(pageInput.getPageName());
        page.setProject(project);

        if (pageInput.getConfig() != null) {
            setConfig(page, pageInput);
        }

        if (pageInput.getCanvas() != null) {
            setCanvas(page, pageInput);
        }

        return page;
    }

    private void setConfig(PageEntity page, PageInput pageInput) {
        PageConfig config = new PageConfig();
        config.setGrid(pageInput.getConfig().getGrid());
        config.setBackgroundColor(pageInput.getConfig().getBackgroundColor());
        page.setConfig(config);
    }

    private void setCanvas(PageEntity page, PageInput pageInput) {
        CanvasEntity canvas = page.getCanvas();
        if (canvas == null) {
            canvas = new CanvasEntity();
            page.setCanvas(canvas);
        }

        if (pageInput.getCanvas().getElements() != null) {
            addElementsToCanvas(canvas, pageInput.getCanvas().getElements());
        }

        if (pageInput.getCanvas().getConnections() != null) {
            addConnectionsToCanvas(canvas, pageInput.getCanvas().getConnections());
        }
    }

    private void addElementsToCanvas(CanvasEntity canvas, List<ElementInput> elements) {
        // Crear mapa de elementos del input por elementId
        java.util.Map<String, ElementInput> inputElementsMap = new java.util.HashMap<>();
        for (ElementInput elementInput : elements) {
            inputElementsMap.put(elementInput.getElementId(), elementInput);
        }

        // Actualizar elementos existentes o marcar para borrado
        java.util.Iterator<ElementEntity> iterator = canvas.getElements().iterator();
        while (iterator.hasNext()) {
            ElementEntity existingElement = iterator.next();
            ElementInput elementInput = inputElementsMap.get(existingElement.getElementId());

            if (elementInput != null) {
                // Actualizar elemento existente
                existingElement.setType(elementInput.getType());
                existingElement.setPositionX(elementInput.getPositionX());
                existingElement.setPositionY(elementInput.getPositionY());
                existingElement.setStyles(elementInput.getStyles());

                if (elementInput.getLayer() != null) {
                    if (existingElement.getLayer() == null) {
                        existingElement.setLayer(new ElementLayer());
                    }
                    existingElement.getLayer().setLocked(elementInput.getLayer().getLocked());
                    existingElement.getLayer().setZIndex(elementInput.getLayer().getZIndex());
                    existingElement.getLayer().setVisible(elementInput.getLayer().getVisible());
                } else {
                    existingElement.setLayer(null);
                }

                // Marcar como procesado
                inputElementsMap.remove(existingElement.getElementId());
            } else {
                // Borrar elemento que ya no está en el input
                iterator.remove();
            }
        }

        // Agregar elementos nuevos (los que quedaron en el mapa)
        for (ElementInput newElementInput : inputElementsMap.values()) {
            ElementEntity newElement = createElementFromInput(newElementInput);
            canvas.getElements().add(newElement);
        }
    }

    private void addConnectionsToCanvas(CanvasEntity canvas, List<ConnectionInput> connections) {
        // Crear un comparador único para conexiones (fromElementId + toElementId +
        // actionType)
        java.util.Map<String, ConnectionInput> inputConnectionsMap = new java.util.HashMap<>();
        for (ConnectionInput connInput : connections) {
            String key = connInput.getFromElementId() + "-" + connInput.getToElementId() + "-"
                    + connInput.getActionType();
            inputConnectionsMap.put(key, connInput);
        }

        // Actualizar conexiones existentes o marcar para borrado
        java.util.Iterator<ConnectionEntity> iterator = canvas.getConnections().iterator();
        while (iterator.hasNext()) {
            ConnectionEntity existingConn = iterator.next();
            String key = existingConn.getFromElementId() + "-" + existingConn.getToElementId() + "-"
                    + existingConn.getActionType();
            ConnectionInput connInput = inputConnectionsMap.get(key);

            if (connInput != null) {
                // Actualizar conexión existente
                existingConn.setOrderNum(connInput.getOrderNum());
                existingConn.setDelay(connInput.getDelay());
                existingConn.setIsParallel(connInput.getIsParallel());

                // Marcar como procesado
                inputConnectionsMap.remove(key);
            } else {
                // Borrar conexión que ya no está en el input
                iterator.remove();
            }
        }

        // Agregar conexiones nuevas (las que quedaron en el mapa)
        for (ConnectionInput newConnInput : inputConnectionsMap.values()) {
            ConnectionEntity newConn = createConnectionFromInput(newConnInput);
            canvas.getConnections().add(newConn);
        }
    }

    private ElementEntity createElementFromInput(ElementInput elementInput) {
        ElementEntity element = new ElementEntity();
        element.setElementId(elementInput.getElementId());
        element.setType(elementInput.getType());
        element.setPositionX(elementInput.getPositionX());
        element.setPositionY(elementInput.getPositionY());
        element.setStyles(elementInput.getStyles());

        if (elementInput.getLayer() != null) {
            ElementLayer layer = new ElementLayer();
            layer.setLocked(elementInput.getLayer().getLocked());
            layer.setZIndex(elementInput.getLayer().getZIndex());
            layer.setVisible(elementInput.getLayer().getVisible());
            element.setLayer(layer);
        }

        return element;
    }

    private ConnectionEntity createConnectionFromInput(ConnectionInput connectionInput) {
        ConnectionEntity connection = new ConnectionEntity();
        connection.setFromElementId(connectionInput.getFromElementId());
        connection.setToElementId(connectionInput.getToElementId());
        connection.setActionType(connectionInput.getActionType());
        connection.setOrderNum(connectionInput.getOrderNum());
        connection.setDelay(connectionInput.getDelay());
        connection.setIsParallel(connectionInput.getIsParallel());
        return connection;
    }

    // esta es una querry demasiado grande. no se si es la mejor practica, voy casi
    // a ciegas
    // pero le pregunte a la IA y me dijo que le diera duro jajaja
    @MutationMapping // esta es muy simple jajaj, es tan solo para mapear mutaciones.
    public ProjectEntity createProject(@Argument ProjectInput projectInput) {
        // Obtener el usuario autenticado
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        String email = authentication.getName();
        com.snapshot.chonect.domain.models.UserEntity user = userServices.getByEmail(email);

        ProjectEntity project = new ProjectEntity();
        project.setProjectName(projectInput.getProjectName());
        project.setUser(user); // ✅ Asignar el usuario al proyecto

        if (projectInput.getPages() != null) {
            for (PageInput pageInput : projectInput.getPages()) {
                PageEntity page = createPageFromInput(pageInput, project);
                project.getPages().add(page);
            }
        }

        logger.info("Proyecto creado: {}", project);
        logger.info("El proyecto está creado.");

        return projectService.create(project);
    }

    @MutationMapping
    @Transactional
    public ProjectEntity updateProject(@Argument @NonNull String projectId, @Argument ProjectInput projectInput) {
        try {
            // Obtener el usuario autenticado
            org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            String email = authentication.getName();
            com.snapshot.chonect.domain.models.UserEntity user = userServices.getByEmail(email);

            // Convertir projectId de String a UUID
            UUID id;
            try {
                id = UUID.fromString(projectId);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("ID de proyecto inválido: " + projectId);
            }

            // Buscar el proyecto existente
            ProjectEntity existingProject = projectService.getByIdWithPages(id)
                    .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con id: " + projectId));

            // Verificar que el proyecto tenga un usuario asignado
            // Si no tiene usuario (proyecto huérfano), eliminarlo automáticamente
            if (existingProject.getUser() == null) {
                logger.warn("Proyecto huérfano detectado (ID: {}). Eliminando automáticamente...", projectId);
                projectService.delete(id);
                throw new IllegalArgumentException(
                        "El proyecto no tenía un usuario asignado y fue eliminado automáticamente. Por favor, crea un nuevo proyecto.");
            }

            // Verificar que el usuario sea el dueño del proyecto
            if (!existingProject.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("No tienes permiso para editar este proyecto");
            }

            // Validar nombre
            if (projectInput.getProjectName() == null || projectInput.getProjectName().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del proyecto no puede estar vacío");
            }

            // Verificar unicidad del nombre solo si cambió
            if (!existingProject.getProjectName().equals(projectInput.getProjectName())
                    && projectService.existsByNameAndUser(projectInput.getProjectName(), user)) {
                throw new IllegalArgumentException("Ya tienes un proyecto con este nombre");
            }

            // Actualizar campos básicos
            existingProject.setProjectName(projectInput.getProjectName());
            existingProject.setDescription(projectInput.getDescription());

            // Manejo inteligente de páginas: actualizar existentes, crear nuevas, borrar
            // eliminadas
            if (projectInput.getPages() != null) {
                // 1. Crear un mapa de páginas del input por ID
                java.util.Map<UUID, PageInput> inputPagesMap = new java.util.HashMap<>();
                java.util.List<PageInput> newPages = new java.util.ArrayList<>();

                // Lógica robusta: Si hay 1 sola página existente y 1 sola página en el input
                // con ID temporal,
                // asumimos que es la misma página para evitar borrarla y recrearla.
                boolean isSinglePageUpdate = existingProject.getPages().size() == 1
                        && projectInput.getPages().size() == 1;
                UUID singleExistingPageId = isSinglePageUpdate ? existingProject.getPages().get(0).getId() : null;

                for (PageInput pageInput : projectInput.getPages()) {
                    if (pageInput.getId() != null && !pageInput.getId().trim().isEmpty()) {
                        try {
                            UUID pageId = UUID.fromString(pageInput.getId());
                            inputPagesMap.put(pageId, pageInput);
                        } catch (IllegalArgumentException e) {
                            // ID temporal del frontend (no es UUID válido)
                            if (isSinglePageUpdate) {
                                logger.info(
                                        "Detectado update de página única con ID temporal. Reutilizando ID existente: {}",
                                        singleExistingPageId);
                                inputPagesMap.put(singleExistingPageId, pageInput);
                            } else {
                                // Tratar como página nueva
                                logger.debug(
                                        "ID de página no es UUID válido (probablemente ID temporal): {}. Tratando como página nueva.",
                                        pageInput.getId());
                                newPages.add(pageInput);
                            }
                        }
                    } else {
                        if (isSinglePageUpdate) {
                            logger.info("Detectado update de página única sin ID. Reutilizando ID existente: {}",
                                    singleExistingPageId);
                            inputPagesMap.put(singleExistingPageId, pageInput);
                        } else {
                            newPages.add(pageInput);
                        }
                    }
                }

                // 2. Actualizar páginas existentes o marcar para borrado
                logger.debug("Procesando páginas existentes. Total: {}", existingProject.getPages().size());
                java.util.Iterator<PageEntity> iterator = existingProject.getPages().iterator();
                while (iterator.hasNext()) {
                    PageEntity existingPage = iterator.next();
                    logger.debug("Revisando página existente ID: {}", existingPage.getId());

                    // REMOVE del mapa para saber cuáles quedan sin procesar
                    PageInput pageInput = inputPagesMap.remove(existingPage.getId());

                    if (pageInput != null) {
                        logger.debug("Coincidencia encontrada. Actualizando página ID: {}", existingPage.getId());
                        // Actualizar página existente
                        existingPage.setPageName(pageInput.getPageName());
                        if (pageInput.getConfig() != null) {
                            setConfig(existingPage, pageInput);
                        }
                        if (pageInput.getCanvas() != null) {
                            setCanvas(existingPage, pageInput);
                        }
                    } else {
                        logger.debug("No hay coincidencia en input. Borrando página ID: {}", existingPage.getId());
                        // Borrar página que ya no está en el input
                        iterator.remove();
                    }
                }

                // 3. Agregar páginas nuevas (incluyendo las que tenían ID pero no coincidían)
                logger.debug("Páginas restantes en mapa (nuevas): {}", inputPagesMap.size());
                newPages.addAll(inputPagesMap.values());
                logger.debug("Total páginas nuevas a agregar: {}", newPages.size());

                for (PageInput newPageInput : newPages) {
                    PageEntity newPage = createPageFromInput(newPageInput, existingProject);
                    existingProject.getPages().add(newPage);
                    logger.debug("Página nueva agregada: {}", newPage.getPageName());
                }
            } else {
                // Si no hay páginas en el input, borrar todas
                existingProject.getPages().clear();
            }

            logger.info("Proyecto actualizado: {}", existingProject);
            logger.debug("Páginas antes del flush: {}", existingProject.getPages().size());

            // Flush explícito para asegurar que todos los cambios se persisten
            entityManager.flush();
            logger.debug("Flush completado");

            // CRÍTICO: Limpiar el contexto de persistencia
            entityManager.clear();
            logger.debug("EntityManager cleared");

            // Re-fetch del proyecto
            ProjectEntity refreshedProject = projectService.getByIdWithPages(id)
                    .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado después del flush"));

            // Verificación
            if (refreshedProject.getPages() != null) {
                logger.debug("=== INICIANDO VERIFICACIÓN DE PÁGINAS Y ELEMENTOS ===");
                logger.debug("Total de páginas: {}", refreshedProject.getPages().size());

                refreshedProject.getPages().forEach(page -> {
                    logger.debug("Página ID: {}, Nombre: '{}'", page.getId(), page.getPageName());

                    if (page.getCanvas() != null) {
                        logger.debug("  Canvas ID: {}", page.getCanvas().getId());

                        if (page.getCanvas().getElements() != null) {
                            int elementCount = page.getCanvas().getElements().size();
                            logger.debug("  Elementos count: {}", elementCount);

                            if (elementCount > 0) {
                                page.getCanvas().getElements().forEach(element -> {
                                    logger.debug("    - Element [ID: {}, ElementId: {}, Type: {}, Position: ({}, {})]",
                                            element.getId(),
                                            element.getElementId(),
                                            element.getType(),
                                            element.getPositionX(),
                                            element.getPositionY());
                                });
                            } else {
                                logger.warn("  ⚠️ Canvas existe pero colección de elementos está VACÍA");
                            }
                        } else {
                            logger.warn("  ⚠️ Canvas existe pero elementos es NULL");
                        }

                        if (page.getCanvas().getConnections() != null) {
                            int connectionCount = page.getCanvas().getConnections().size();
                            logger.debug("  Conexiones count: {}", connectionCount);
                        }
                    } else {
                        logger.warn("  ⚠️ Página NO tiene canvas asociado");
                    }
                });

                logger.debug("=== FIN VERIFICACIÓN ===");
            }

            return refreshedProject;

        } catch (Exception e) {
            logger.error("Error actualizando proyecto: ", e);
            throw e;
        }
    }

    @MutationMapping
    public ElementEntity updateElementPosition(@Argument String elementId, @Argument String positionX,
            @Argument String positionY) {
        ElementEntity element = elementRepository.findByElementId(elementId)
                .orElseThrow(() -> new RuntimeException("Element not found with id: " + elementId));

        element.setPositionX(positionX);
        element.setPositionY(positionY);

        return elementRepository.save(element);
    }

    @MutationMapping
    public ElementEntity updateElementStyles(@Argument String elementId, @Argument String styles) {
        ElementEntity element = elementRepository.findByElementId(elementId)
                .orElseThrow(() -> new RuntimeException("Element not found with id: " + elementId));

        element.setStyles(styles);

        return elementRepository.save(element);
    }

    @MutationMapping
    public ElementEntity updateElement(@Argument String elementId, @Argument ElementInput elementInput) {
        ElementEntity element = elementRepository.findByElementId(elementId)
                .orElseThrow(() -> new RuntimeException("Element not found with id: " + elementId));

        element.setType(elementInput.getType());
        element.setPositionX(elementInput.getPositionX());
        element.setPositionY(elementInput.getPositionY());
        element.setStyles(elementInput.getStyles());

        if (elementInput.getLayer() != null) {
            if (element.getLayer() == null) {
                element.setLayer(new ElementLayer());
            }
            element.getLayer().setLocked(elementInput.getLayer().getLocked());
            element.getLayer().setZIndex(elementInput.getLayer().getZIndex());
            element.getLayer().setVisible(elementInput.getLayer().getVisible());
        }

        return elementRepository.save(element);
    }

    @org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
    public graphql.GraphQLError handle(Exception ex) {
        logger.error("Error no manejado en GraphQL: ", ex);
        return graphql.GraphQLError.newError().errorType(graphql.ErrorType.DataFetchingException)
                .message(ex.getMessage()).build();
    }

    // oe si se puede hacer mejor me explican que estoy francamente un poco idiota
    // en este momento jajaja, me duele el celebelo
}
