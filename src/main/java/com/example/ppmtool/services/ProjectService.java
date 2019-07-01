package com.example.ppmtool.services;

import com.example.ppmtool.domain.Project;
import com.example.ppmtool.exceptions.ProjectIdException;
import com.example.ppmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project saveOrUpdateProject(Project project) {
        try {
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            return projectRepository.save(project);
        } catch(Exception e) {
            throw new ProjectIdException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exists!");
        }
    }

    public Project findProjectByIdentifier(String projectId) {
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null) throw new ProjectIdException("Project with ID '" + projectId + "' does not exist!");

        return project;
    }

    public Iterable<Project> findallProjects() {
        return projectRepository.findAll();
    }

    public void deleteProjectByIdentifier(String projectId) {
        Project project = projectRepository.findByProjectIdentifier(projectId);

        if(project == null) {
            throw new ProjectIdException("Action cannot be completed. Project with ID '" + projectId + "' does not exist!");
        }

        projectRepository.delete(project);
    }
}
