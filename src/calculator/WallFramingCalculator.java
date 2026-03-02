package calculator;

import model.Opening;
import model.Wall;
import java.util.List;

/**
 * Calculates wall framing materials: studs, plates, sheathing, and cap plate.
 * All formulas based on standard 16" on-center residential framing.
 */
public class WallFramingCalculator {

    private static final double STUD_SPACING_INCHES = 16.0;
    private static final double SHEET_AREA_SQFT = 32.0; // 4x8 plywood = 32 sq ft

    /**
     * Wall studs at 16" OC: (linear ft * 12 / 16) + 1, rounded up.
     * Add 2 studs per corner, 1 per T-wall intersection.
     */
    public static int calculateStuds(Wall wall) {
        int baseStuds = (int) Math.ceil((wall.getLength() * 12.0 / STUD_SPACING_INCHES) + 1);
        int cornerStuds = wall.getCorners() * 2;
        int tStuds = wall.getTIntersections();
        return baseStuds + cornerStuds + tStuds;
    }

    /**
     * Top and bottom plates: linear ft * 3 (double top plate + single bottom plate).
     * Returns linear feet of plate lumber needed.
     */
    public static double calculatePlateLinearFeet(Wall wall) {
        return wall.getLength() * 3.0;
    }

    /**
     * Cap plate: single plate that ties wall corners together.
     * Returns linear feet equal to wall length.
     */
    public static double calculateCapPlate(Wall wall) {
        return wall.getLength();
    }

    /**
     * Plywood wall sheathing: (wall area - opening areas) / 32, rounded up.
     * Each 4x8 sheet covers 32 sq ft.
     */
    public static int calculateSheathingSheets(Wall wall, List<Opening> openings) {
        double wallArea = wall.getWallArea();
        double openingArea = 0;
        for (Opening opening : openings) {
            openingArea += opening.getOpeningArea();
        }
        double netArea = wallArea - openingArea;
        if (netArea <= 0) {
            return 0;
        }
        return (int) Math.ceil(netArea / SHEET_AREA_SQFT);
    }

    /**
     * Plywood wall sheathing with no openings.
     */
    public static int calculateSheathingSheets(Wall wall) {
        return (int) Math.ceil(wall.getWallArea() / SHEET_AREA_SQFT);
    }
}
