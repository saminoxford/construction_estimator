package model;

/**
 * Represents a room or structure with dimensions used for framing estimates.
 */
public class Room {

    private String name;
    private double length; // feet
    private double width;  // feet
    private double height; // feet

    public Room(String name, double length, double width, double height) {
        this.name = name;
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getArea() {
        return length * width;
    }

    public double getPerimeter() {
        return 2 * (length + width);
    }

    @Override
    public String toString() {
        return name + " (" + length + "' x " + width + "' x " + height + "' high)";
    }
}
