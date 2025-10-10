package com.snapshot.chonect.api.graphql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.snapshot.chonect.domain.models.CanvasEntity;
import com.snapshot.chonect.domain.models.ConnectionEntity;
import com.snapshot.chonect.domain.models.ElementEntity;
import com.snapshot.chonect.domain.models.PageEntity;
import com.snapshot.chonect.domain.models.ProjectEntity;
import com.snapshot.chonect.infrastructure.services.ProjectService;
import com.snapshot.chonect.utils.objects.ElementLayer;
import com.snapshot.chonect.utils.objects.PageConfig;
import com.snapshot.chonect.utils.requirements.ProjectInput;

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
    
    @Autowired
    private final ProjectService projectService;

    // esta etiqueta @QueryMapping es usada para mapear las consultas a la API GraphQL
    @QueryMapping 
    public ProjectEntity findProjectById(@Argument Long id) { // El @Argument sirve pa mapear argumentos individuales
        return projectService.getById(id);
    }
    
    @QueryMapping
    public List<ProjectEntity> findAllProjects() {
        return projectService.getAll();
    }


    // esta es una querry demasiado grande. no se si es la mejor practica, voy casi a ciegas
    // pero le pregunte a la IA y me dijo que le diera duro jajaja
    @MutationMapping // esta es muy simple jajaj, es tan solo para mapear mutaciones.
    public ProjectEntity createProject(@Argument ProjectInput projectInput) {
        ProjectEntity project = new ProjectEntity();
        project.setProjectName(projectInput.getProjectName());

        if (projectInput.getPages() != null) {
            project.getPages().forEach(pageInput -> {
                PageEntity page = new PageEntity();
                page.setPageName(pageInput.getPageName());
                page.setProject(project);

                if (pageInput.getConfig() != null) {
                    PageConfig config = new PageConfig();
                    config.setGrid(pageInput.getConfig().getGrid());
                    config.setBackgroundColor(pageInput.getConfig().getBackgroundColor());
                    page.setConfig(config);
                }

                if (pageInput.getCanvas() != null) {
                    CanvasEntity canvas = new CanvasEntity();

                    if (pageInput.getCanvas().getElements() != null) {
                        pageInput.getCanvas().getElements().forEach(elementInput -> {
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

                            canvas.getElements().add(element);
                        });
                    }

                    if (pageInput.getCanvas().getConnections() != null) {
                        pageInput.getCanvas().getConnections().forEach(connectionInput -> {
                            ConnectionEntity connection = new ConnectionEntity();
                            connection.setFromElementId(connectionInput.getFromElementId());
                            connection.setToElementId(connectionInput.getToElementId());
                            connection.setActionType(connectionInput.getActionType());
                            connection.setOrderNum(connectionInput.getOrderNum());
                            connection.setDelay(connectionInput.getDelay());
                            connection.setParallel(connectionInput.getParallel());
                            
                            canvas.getConnections().add(connection);
                        });
                    }

                    page.setCanvas(canvas);
                }

                project.getPages().add(page);
            });
        }

        System.out.println(project);
        System.out.println("El projecto esta creado.");

        return projectService.create(project);
    } // oe si se puede hacer mejor me explican que estoy francamente un poco idiota en este momento jajaja, me duele el celebelo
}
