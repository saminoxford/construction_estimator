package model;

/**
 * Represents a wall section with properties needed for framing calculations.
 */
public class Wall {

    private double length;         // linear feet
    private double height;         // feet
    private int corners;           // number of corners (each gets 2 extra studs)
    private int tIntersections;    // number of T-wall intersections (each gets 1 extra stud)

    public Wall(double length, double height, int corners, int tIntersections) {
        this.length = length;
        this.height = height;
        this.corners = corners;
        this.tIntersections = tIntersections;
    }

    public double getLength() {
        return length;
    }

    public double getHeight() {
        return height;
    }

    public int getCorners() {
        return corners;
    }

    public int getTIntersections() {
        return tIntersections;
    }

    public double getWallArea() {
        return length * height;
    }

    @Override
    public String toString() {
        return length + "' long x " + height + "' high, "
                + corners + " corner(s), " + tIntersections + " T-intersection(s)";
    }
}
