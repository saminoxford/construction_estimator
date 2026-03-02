package com.mintz.estimator.dto;

import java.util.List;

/**
 * Request body for creating a new project via the REST API.
 */
public class ProjectRequest {

    private String name;
    private List<RoomRequest> rooms;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<RoomRequest> getRooms() { return rooms; }
    public void setRooms(List<RoomRequest> rooms) { this.rooms = rooms; }
}
