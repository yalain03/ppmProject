package com.example.ppmtool.services;

import com.example.ppmtool.domain.Backlog;
import com.example.ppmtool.domain.ProjectTask;
import com.example.ppmtool.repositories.BacklogRepository;
import com.example.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
        projectTask.setBacklog(backlog);
        Integer backlogSequence = backlog.getPTSequence();
        backlogSequence++;

        projectTask.setProjectSequence(projectIdentifier+"-"+backlogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);

//        if(projectTask.getPriority()==0 || projectTask.getPriority() == null) {
//            projectTask.setPriority(3);
//        }

        if(projectTask.getStatus() == "" || projectTask.getStatus() == null) {
            projectTask.setStatus("TODO");
        }

        return projectTaskRepository.save(projectTask);
    }
}
