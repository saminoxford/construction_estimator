package calculator;

import model.Room;

/**
 * Calculates floor system materials: joists and plywood subfloor.
 * All formulas based on standard 16" on-center residential framing.
 */
public class FloorSystemCalculator {

    private static final double JOIST_SPACING_INCHES = 16.0;
    private static final double SHEET_AREA_SQFT = 32.0; // 4x8 plywood = 32 sq ft

    /**
     * Floor joists at 16" OC: (span_ft * 12 / 16) + 1, rounded up.
     * Joists run across the width; they are spaced along the length.
     * Add 2 rim joists that run along the length of the floor.
     */
    public static int calculateJoists(double spanFeet) {
        int joists = (int) Math.ceil((spanFeet * 12.0 / JOIST_SPACING_INCHES) + 1);
        int rimJoists = 2;
        return joists + rimJoists;
    }

    /**
     * Convenience method using Room dimensions.
     * Joists are spaced along the length, spanning the width.
     */
    public static int calculateJoists(Room room) {
        return calculateJoists(room.getLength());
    }

    /**
     * Plywood subfloor: area / 32, rounded up.
     * Each 4x8 sheet covers 32 sq ft.
     */
    public static int calculateSubfloorSheets(double areaSquareFeet) {
        return (int) Math.ceil(areaSquareFeet / SHEET_AREA_SQFT);
    }

    /**
     * Convenience method using Room dimensions.
     */
    public static int calculateSubfloorSheets(Room room) {
        return calculateSubfloorSheets(room.getArea());
    }
}
