package com.Project.TaskManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Project.TaskManager.model.User;


@Repository
public interface UserRepository extends JpaRepository<User,Long>{
    
}
