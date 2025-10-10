package com.snapshot.chonect.infrastructure.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapshot.chonect.domain.models.ProjectEntity;
import com.snapshot.chonect.domain.repositories.ProjectRepository;
import com.snapshot.chonect.infrastructure.abstract_services.IProjectService;
import com.snapshot.chonect.infrastructure.helpers.SupportService;

import lombok.RequiredArgsConstructor;

@Service
// el atributo @RequiredArgsConstructor es una anotación de Lombok que genera automáticamente
// un constructor con parámetros para todos los campos final 
// y campos @NonNull que no hayan sido inicializados.

// ademas, el @RequiredArgsConstructor es mas seguro que el @AllArgsConstructor
@RequiredArgsConstructor
public class ProjectService implements IProjectService {

    @Autowired
    private final ProjectRepository projectRepository;

    @Autowired
    private final SupportService<ProjectEntity> supportService;

    @Override
    @Transactional // el atributo @Transactional es simplemente gestiona automáticamente las transacciones de base de datos.
    public ProjectEntity create(ProjectEntity request) {
        try {
            return projectRepository.save(request);
        } catch (Exception e) {
            throw new RuntimeException("Error creando projecto: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectEntity getById(Long id) {
        return supportService.findById(projectRepository, id, "ProjectEntity");
    }


    @Override
    @Transactional(readOnly = true)
    public List<ProjectEntity> getAll() {
        return projectRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            if (!projectRepository.existsById(id)) {
                throw new RuntimeException("Project no encontrado " + id);
            }
            projectRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando projecto: " + e.getMessage(), e);
        }
    }
}
