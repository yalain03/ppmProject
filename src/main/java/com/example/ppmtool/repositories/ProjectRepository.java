package com.example.ppmtool.repositories;

import com.example.ppmtool.domain.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    @Override
    Iterable<Project> findAll();

    @Override
    Iterable<Project> findAllById(Iterable<Long> iterable);

    Project findByProjectIdentifier(String projectId);

    Iterable<Project> findAllByProjectLeader(String username);

    Project findByProjectIdentifierAndProjectLeader(String projectId, String username);
}
