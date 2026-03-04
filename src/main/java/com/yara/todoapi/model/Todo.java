package com.yara.todoapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required ")
    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(nullable = true)
    private Boolean completed = false;

    public Boolean isCompleted() {
        return completed;
    }
}