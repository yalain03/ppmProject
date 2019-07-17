package com.example.ppmtool.services;

import com.example.ppmtool.domain.Backlog;
import com.example.ppmtool.domain.Project;
import com.example.ppmtool.domain.ProjectTask;
import com.example.ppmtool.domain.User;
import com.example.ppmtool.exceptions.ProjectIdException;
import com.example.ppmtool.exceptions.ProjectNotFoundException;
import com.example.ppmtool.repositories.BacklogRepository;
import com.example.ppmtool.repositories.ProjectRepository;
import com.example.ppmtool.repositories.ProjectTaskRepository;
import com.example.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project, String username) {
        if(project.getId() != null) {
            Project existingProject = projectRepository.findByProjectIdentifierAndProjectLeader(project.getProjectIdentifier(), username);
            if(existingProject == null) throw new ProjectNotFoundException("The project does not exist in your repository");
        } else {
            throw new ProjectNotFoundException("Project with id '" + project.getId() + "' does not exist");
        }

        try {
            User user = userRepository.findByUsername(username);
            project.setUser(user);
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());

            if(project.getId() == null) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            }

            if(project.getId() != null) {
                project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
            }

            return projectRepository.save(project);
        } catch(Exception e) {
            throw new ProjectIdException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exists!");
        }
    }

    public Project findProjectByIdentifier(String projectId, String username) {
//        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        Project project = projectRepository.findByProjectIdentifierAndProjectLeader(projectId.toUpperCase(), username);

        if(project == null) throw new ProjectIdException("Project with ID '" + projectId + "' does not exist in your repository!");
//
//        if(!project.getProjectLeader().equals(username)) throw new ProjectNotFoundException("Project not found in your repository");

        return project;
    }

    public Iterable<Project> findallProjects(String username) {
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectId, String username) {
        Project project = findProjectByIdentifier(projectId, username);

        projectRepository.delete(project);
    }

    public Iterable<ProjectTask> findBacklogById(String id) {
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }
}
