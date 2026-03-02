package io;

import model.Opening;
import model.Room;
import model.Wall;

import calculator.FloorSystemCalculator;
import calculator.RoughOpeningCalculator;
import calculator.WallFramingCalculator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Writes a formatted material estimate report to a text file.
 * Loops through all rooms, calculates framing materials, and produces a summary.
 */
public class ReportWriter {

    private static final int DEFAULT_CORNERS = 4;
    private static final int DEFAULT_T_INTERSECTIONS = 0;

    /**
     * Generates a full estimate report for all rooms and writes it to a file.
     *
     * @param projectName name of the construction project
     * @param rooms       list of rooms to estimate
     * @param outputPath  file path to write the report to
     * @throws IOException if the file cannot be written
     */
    public static void writeReport(String projectName, ArrayList<Room> rooms, String outputPath)
            throws IOException {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(outputPath));

            // --- Header ---
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            writer.println("==============================================================");
            writer.println("  MINTZ CONSTRUCTION ESTIMATOR - MATERIAL REPORT");
            writer.println("==============================================================");
            writer.println("  Project:  " + projectName);
            writer.println("  Date:     " + date);
            writer.println("  Rooms:    " + rooms.size());
            writer.println("==============================================================");

            // --- Running totals ---
            int totalJoists = 0;
            int totalSubfloorSheets = 0;
            int totalStuds = 0;
            double totalPlateLF = 0;
            double totalCapPlateLF = 0;
            int totalSheathingSheets = 0;
            double totalFloorArea = 0;

            // --- Per-room estimates ---
            for (int i = 0; i < rooms.size(); i++) {
                Room room = rooms.get(i);
                Wall wall = new Wall(room.getPerimeter(), room.getHeight(),
                        DEFAULT_CORNERS, DEFAULT_T_INTERSECTIONS);

                int joists = FloorSystemCalculator.calculateJoists(room);
                int subfloor = FloorSystemCalculator.calculateSubfloorSheets(room);
                int studs = WallFramingCalculator.calculateStuds(wall);
                double plates = WallFramingCalculator.calculatePlateLinearFeet(wall);
                double capPlate = WallFramingCalculator.calculateCapPlate(wall);
                int sheathing = WallFramingCalculator.calculateSheathingSheets(wall);

                writer.println();
                writer.println("--------------------------------------------------------------");
                writer.println("  ROOM " + (i + 1) + ": " + room);
                writer.println("--------------------------------------------------------------");
                writer.println("  Floor area:          " + room.getArea() + " sq ft");
                writer.println("  Perimeter:           " + room.getPerimeter() + " linear ft");
                writer.println();
                writer.println("  FLOOR SYSTEM");
                writer.println("    Joists (16\" OC):   " + joists + " (includes 2 rim joists)");
                writer.println("    Subfloor plywood:  " + subfloor + " sheets (4x8)");
                writer.println();
                writer.println("  WALL FRAMING");
                writer.println("    Studs (16\" OC):    " + studs);
                writer.println("    Plate lumber:      " + plates + " linear ft");
                writer.println("    Cap plate:         " + capPlate + " linear ft");
                writer.println("    Sheathing:         " + sheathing + " sheets (4x8)");

                // Accumulate totals
                totalJoists += joists;
                totalSubfloorSheets += subfloor;
                totalStuds += studs;
                totalPlateLF += plates;
                totalCapPlateLF += capPlate;
                totalSheathingSheets += sheathing;
                totalFloorArea += room.getArea();
            }

            // --- Project summary ---
            writer.println();
            writer.println("==============================================================");
            writer.println("  PROJECT SUMMARY - " + projectName);
            writer.println("==============================================================");
            writer.println("  Total floor area:       " + totalFloorArea + " sq ft");
            writer.println("  Total floor joists:     " + totalJoists);
            writer.println("  Total subfloor sheets:  " + totalSubfloorSheets);
            writer.println("  Total wall studs:       " + totalStuds);
            writer.println("  Total plate lumber:     " + totalPlateLF + " linear ft");
            writer.println("  Total cap plate:        " + totalCapPlateLF + " linear ft");
            writer.println("  Total sheathing:        " + totalSheathingSheets + " sheets");
            writer.println("==============================================================");
            writer.println("  Report generated by Mintz Construction Estimator v1.0");
            writer.println("==============================================================");

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
