package com.mintz.estimator.controller;

import com.mintz.estimator.dto.ProjectRequest;
import com.mintz.estimator.model.Project;
import com.mintz.estimator.service.EstimatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the Mintz Construction Estimator API.
 *
 * Endpoints:
 *   POST   /api/projects       — create a new project with rooms
 *   GET    /api/projects        — list all saved projects
 *   GET    /api/projects/{id}   — get a specific project with estimates
 *   DELETE /api/projects/{id}   — delete a project
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final EstimatorService estimatorService;

    public ProjectController(EstimatorService estimatorService) {
        this.estimatorService = estimatorService;
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectRequest request) {
        Project project = estimatorService.createProject(request);
        return ResponseEntity.ok(project);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(estimatorService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        return estimatorService.getProject(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        if (estimatorService.deleteProject(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
