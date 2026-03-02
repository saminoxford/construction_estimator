package com.mintz.estimator.dto;

/**
 * Room data within a ProjectRequest.
 */
public class RoomRequest {

    private String name;
    private double length;
    private double width;
    private double height;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }

    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
}
