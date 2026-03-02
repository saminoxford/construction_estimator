package model;

/**
 * Represents a rough opening for a door or window in a framed wall.
 */
public class Opening {

    public enum Type {
        DOOR,
        WINDOW
    }

    private Type type;
    private double width;      // feet (rough opening width)
    private double height;     // feet (rough opening height)
    private double sillHeight; // feet from floor to bottom of window (windows only)

    public Opening(Type type, double width, double height, double sillHeight) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.sillHeight = sillHeight;
    }

    public Type getType() {
        return type;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getSillHeight() {
        return sillHeight;
    }

    public double getOpeningArea() {
        return width * height;
    }

    @Override
    public String toString() {
        if (type == Type.DOOR) {
            return "Door (" + width + "' x " + height + "')";
        } else {
            return "Window (" + width + "' x " + height + "', sill at " + sillHeight + "')";
        }
    }
}
