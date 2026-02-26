package com.Project.TaskManager.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
}