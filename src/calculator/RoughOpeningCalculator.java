package calculator;

import model.Opening;

/**
 * Calculates framing materials for rough openings (doors and windows).
 *
 * Door rough opening: 2 king studs + 2 jack studs + 1 doubled header
 * Window rough opening: same as door + cripple studs above and below
 */
public class RoughOpeningCalculator {

    private static final double STUD_SPACING_INCHES = 16.0;

    /** King studs: always 2 (full-height studs on each side of the opening). */
    public static int getKingStuds() {
        return 2;
    }

    /** Jack studs: always 2 (trimmer studs that support the header). */
    public static int getJackStuds() {
        return 2;
    }

    /** Header pieces: always 2 (doubled header beam over the opening). */
    public static int getHeaderPieces() {
        return 2;
    }

    /**
     * Cripple studs maintain the 16" OC layout through the opening.
     *
     * For windows: cripples above the header AND below the sill.
     * For doors: cripples above the header only (door goes to floor).
     *
     * Count per section = positions that fall within the opening width.
     */
    public static int getCrippleStuds(Opening opening) {
        double widthInches = opening.getWidth() * 12.0;
        int cripplesPerSection = Math.max(0, (int) ((widthInches - 1) / STUD_SPACING_INCHES));

        if (opening.getType() == Opening.Type.DOOR) {
            // Doors: cripples above header only
            return cripplesPerSection;
        } else {
            // Windows: cripples above header + below sill
            return cripplesPerSection * 2;
        }
    }

    /**
     * Sill plate pieces for a window (horizontal member at bottom of window).
     * Doors do not have a sill plate in the rough opening.
     */
    public static int getSillPlate(Opening opening) {
        if (opening.getType() == Opening.Type.WINDOW) {
            return 1;
        }
        return 0;
    }

    /**
     * Total studs/lumber pieces needed for a single rough opening.
     */
    public static int getTotalPieces(Opening opening) {
        return getKingStuds()
                + getJackStuds()
                + getHeaderPieces()
                + getCrippleStuds(opening)
                + getSillPlate(opening);
    }

    /**
     * Prints a detailed breakdown for a single opening.
     */
    public static String getBreakdown(Opening opening) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(opening).append("\n");
        sb.append("    King studs:    ").append(getKingStuds()).append("\n");
        sb.append("    Jack studs:    ").append(getJackStuds()).append("\n");
        sb.append("    Header pieces: ").append(getHeaderPieces()).append("\n");
        sb.append("    Cripple studs: ").append(getCrippleStuds(opening)).append("\n");
        if (opening.getType() == Opening.Type.WINDOW) {
            sb.append("    Sill plate:    ").append(getSillPlate(opening)).append("\n");
        }
        sb.append("    TOTAL pieces:  ").append(getTotalPieces(opening)).append("\n");
        return sb.toString();
    }
}
