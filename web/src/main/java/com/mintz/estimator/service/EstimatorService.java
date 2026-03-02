package com.mintz.estimator.service;

import com.mintz.estimator.dto.ProjectRequest;
import com.mintz.estimator.dto.RoomRequest;
import com.mintz.estimator.model.Estimate;
import com.mintz.estimator.model.Project;
import com.mintz.estimator.model.Room;
import com.mintz.estimator.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer that wires the framing calculation logic into the Spring Boot API.
 * Contains the same formulas from Phase 1.
 */
@Service
public class EstimatorService {

    private static final double STUD_SPACING = 16.0;
    private static final double SHEET_AREA = 32.0;
    private static final int DEFAULT_CORNERS = 4;

    private final ProjectRepository projectRepository;

    public EstimatorService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Creates a new project, calculates estimates for each room, and saves to DB.
     */
    public Project createProject(ProjectRequest request) {
        Project project = new Project(request.getName());

        for (RoomRequest rr : request.getRooms()) {
            Room room = new Room(rr.getName(), rr.getLength(), rr.getWidth(), rr.getHeight());
            Estimate estimate = calculateEstimate(room);
            estimate.setRoom(room);
            room.setEstimate(estimate);
            project.addRoom(room);
        }

        return projectRepository.save(project);
    }

    /**
     * Returns all saved projects.
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Returns a single project by ID.
     */
    public Optional<Project> getProject(Long id) {
        return projectRepository.findById(id);
    }

    /**
     * Deletes a project by ID.
     */
    public boolean deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Core calculation logic — same formulas from Phase 1 calculators.
     */
    private Estimate calculateEstimate(Room room) {
        double perimeter = room.getPerimeter();
        double area = room.getArea();
        double height = room.getHeight();

        // Wall studs: (linear_ft * 12 / 16) + 1, rounded up + corners
        int studs = (int) Math.ceil((perimeter * 12.0 / STUD_SPACING) + 1)
                + (DEFAULT_CORNERS * 2);

        // Plates: perimeter * 3 (double top + single bottom)
        double plates = perimeter * 3.0;

        // Cap plate: perimeter
        double capPlate = perimeter;

        // Sheathing: wall area / 32
        int sheathing = (int) Math.ceil((perimeter * height) / SHEET_AREA);

        // Floor joists: (length * 12 / 16) + 1, rounded up + 2 rim
        int joists = (int) Math.ceil((room.getLength() * 12.0 / STUD_SPACING) + 1) + 2;

        // Subfloor: area / 32
        int subfloor = (int) Math.ceil(area / SHEET_AREA);

        Estimate estimate = new Estimate();
        estimate.setWallStuds(studs);
        estimate.setPlateLumberLf(plates);
        estimate.setCapPlateLf(capPlate);
        estimate.setSheathingSheets(sheathing);
        estimate.setFloorJoists(joists);
        estimate.setSubfloorSheets(subfloor);

        return estimate;
    }
}
