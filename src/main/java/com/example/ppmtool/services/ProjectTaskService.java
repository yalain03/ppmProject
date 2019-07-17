package com.example.ppmtool.services;

import com.example.ppmtool.domain.Backlog;
import com.example.ppmtool.domain.Project;
import com.example.ppmtool.domain.ProjectTask;
import com.example.ppmtool.exceptions.ProjectNotFoundException;
import com.example.ppmtool.exceptions.ProjectNotFoundExceptionResponse;
import com.example.ppmtool.repositories.BacklogRepository;
import com.example.ppmtool.repositories.ProjectRepository;
import com.example.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {
        try {
            Backlog backlog = projectRepository.findByProjectIdentifierAndProjectLeader(projectIdentifier, username).getBacklog(); //backlogRepository.findByProjectIdentifier(projectIdentifier);
            projectTask.setBacklog(backlog);
            Integer backlogSequence = backlog.getPTSequence();

            backlogSequence++;
            backlog.setPTSequence(backlogSequence);

            projectTask.setProjectSequence(projectIdentifier+"-"+backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            if(projectTask.getPriority() == null || projectTask.getPriority() == 0) {
                projectTask.setPriority(3);
            }

            if(projectTask.getStatus() == "" || projectTask.getStatus() == null) {
                projectTask.setStatus("TODO");
            }

            return projectTaskRepository.save(projectTask);
        } catch(Exception e) {
            throw new ProjectNotFoundException("Project not found!");
        }
    }

    public List<ProjectTask> findBacklogById(String id, String username) {
        Project project = projectRepository.findByProjectIdentifierAndProjectLeader(id, username);

        if(project == null) {
            throw new ProjectNotFoundException("Project with ID: '" + id + "' does not exist!");
        }

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id, String username) {
        projectService.findProjectByIdentifier(backlog_id, username);

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);

        if(projectTask == null){
            throw new ProjectNotFoundException("Project Task '" + pt_id + "' not found");
        }

        //make sure that the backlog/project id in the path corresponds to the right project
        if(!projectTask.getProjectIdentifier().equals(backlog_id)){
            throw new ProjectNotFoundException("Project Task '" + pt_id + "' does not exist in project: '"+backlog_id);
        }


        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id, String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);

        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id, String pt_id, String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);

        projectTaskRepository.delete(projectTask);
    }
}
