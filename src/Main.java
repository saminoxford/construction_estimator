// Mintz Construction Estimator — Phase 3 Console Application
// Created by John Mintz

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import model.Opening;
import model.Room;
import model.Wall;

import calculator.FloorSystemCalculator;
import calculator.RoughOpeningCalculator;
import calculator.WallFramingCalculator;

import io.CsvReader;
import io.ReportWriter;

import db.DatabaseConfig;
import db.DatabaseSetup;
import db.ProjectDao;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    private static boolean dbAvailable = false;

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("   Mintz Construction Estimator v1.0");
        System.out.println("   Created by John Mintz");
        System.out.println("==============================================");

        // Try to initialize database (non-blocking — app works without it)
        try {
            Connection conn = DatabaseConfig.getConnection();
            DatabaseSetup.initialize(conn);
            conn.close();
            dbAvailable = true;
            System.out.println("  Database connected.");
        } catch (SQLException e) {
            System.out.println("  Database not available - DB features disabled.");
            System.out.println("  (" + e.getMessage() + ")");
        }

        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    squareFootageCalculator();
                    break;
                case 2:
                    wallStudCalculator();
                    break;
                case 3:
                    floorJoistCalculator();
                    break;
                case 4:
                    plywoodSheetCalculator();
                    break;
                case 5:
                    roughOpeningCalculator();
                    break;
                case 6:
                    fullRoomEstimate();
                    break;
                case 7:
                    loadFromCsv();
                    break;
                case 8:
                    loadCsvAndGenerateReport();
                    break;
                case 9:
                    saveProjectToDb();
                    break;
                case 10:
                    loadProjectFromDb();
                    break;
                case 11:
                    listAllProjects();
                    break;
                case 12:
                    deleteProject();
                    break;
                case 0:
                    running = false;
                    System.out.println("\nThank you for using the Mintz Construction Estimator.");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    break;
            }
        }

        scanner.close();
    }

    // ---------------------------------------------------------------
    // Menu
    // ---------------------------------------------------------------

    private static void printMenu() {
        System.out.println("\n----------------------------------------------");
        System.out.println("  MAIN MENU");
        System.out.println("----------------------------------------------");
        System.out.println("  1. Square Footage Calculator");
        System.out.println("  2. Wall Stud Calculator");
        System.out.println("  3. Floor Joist Calculator");
        System.out.println("  4. Plywood Sheet Calculator");
        System.out.println("  5. Rough Opening Calculator");
        System.out.println("  6. Full Room Estimate");
        System.out.println("  7. Load Rooms from CSV");
        System.out.println("  8. CSV -> Full Report File");
        System.out.println("  ---  DATABASE  ---");
        System.out.println("  9.  Save Project to DB (from CSV)");
        System.out.println("  10. Load Project from DB");
        System.out.println("  11. List All Projects");
        System.out.println("  12. Delete a Project");
        System.out.println("  0. Exit");
        System.out.println("----------------------------------------------");
    }

    // ---------------------------------------------------------------
    // 1. Square Footage Calculator
    // ---------------------------------------------------------------

    private static void squareFootageCalculator() {
        System.out.println("\n--- Square Footage Calculator ---");
        double length = readDouble("Enter length (ft): ");
        double width = readDouble("Enter width (ft): ");
        double area = length * width;

        System.out.println("\n  RESULT");
        System.out.println("  Length:     " + length + " ft");
        System.out.println("  Width:      " + width + " ft");
        System.out.println("  Area:       " + area + " sq ft");
    }

    // ---------------------------------------------------------------
    // 2. Wall Stud Calculator
    // ---------------------------------------------------------------

    private static void wallStudCalculator() {
        System.out.println("\n--- Wall Stud Calculator (16\" OC) ---");
        double wallLength = readDouble("Enter total linear feet of wall: ");
        double wallHeight = readDouble("Enter wall height (ft): ");
        int corners = readInt("Enter number of corners: ");
        int tIntersections = readInt("Enter number of T-wall intersections: ");

        Wall wall = new Wall(wallLength, wallHeight, corners, tIntersections);

        int studs = WallFramingCalculator.calculateStuds(wall);
        double plates = WallFramingCalculator.calculatePlateLinearFeet(wall);
        double capPlate = WallFramingCalculator.calculateCapPlate(wall);

        System.out.println("\n  WALL FRAMING ESTIMATE");
        System.out.println("  Wall:              " + wall);
        System.out.println("  Studs needed:      " + studs);
        System.out.println("  Plate lumber:      " + plates + " linear ft (double top + single bottom)");
        System.out.println("  Cap plate:         " + capPlate + " linear ft");
    }

    // ---------------------------------------------------------------
    // 3. Floor Joist Calculator
    // ---------------------------------------------------------------

    private static void floorJoistCalculator() {
        System.out.println("\n--- Floor Joist Calculator (16\" OC) ---");
        double span = readDouble("Enter floor span / length (ft): ");
        double width = readDouble("Enter floor width (ft): ");
        double area = span * width;

        int joists = FloorSystemCalculator.calculateJoists(span);
        int subfloor = FloorSystemCalculator.calculateSubfloorSheets(area);

        System.out.println("\n  FLOOR SYSTEM ESTIMATE");
        System.out.println("  Span:              " + span + " ft");
        System.out.println("  Width:             " + width + " ft");
        System.out.println("  Floor area:        " + area + " sq ft");
        System.out.println("  Joists needed:     " + joists + " (includes 2 rim joists)");
        System.out.println("  Subfloor sheets:   " + subfloor + " (4x8 plywood)");
    }

    // ---------------------------------------------------------------
    // 4. Plywood Sheet Calculator
    // ---------------------------------------------------------------

    private static void plywoodSheetCalculator() {
        System.out.println("\n--- Plywood Sheet Calculator ---");
        System.out.println("  1. Subfloor (floor area)");
        System.out.println("  2. Wall sheathing");
        int type = readInt("Choose type: ");

        if (type == 1) {
            double area = readDouble("Enter floor area (sq ft): ");
            int sheets = FloorSystemCalculator.calculateSubfloorSheets(area);
            System.out.println("\n  PLYWOOD SUBFLOOR");
            System.out.println("  Area:              " + area + " sq ft");
            System.out.println("  Sheets needed:     " + sheets + " (4x8 plywood, 32 sq ft each)");
        } else if (type == 2) {
            double wallLength = readDouble("Enter wall length (ft): ");
            double wallHeight = readDouble("Enter wall height (ft): ");
            int openingCount = readInt("How many openings (doors/windows) to deduct? ");

            Wall wall = new Wall(wallLength, wallHeight, 0, 0);
            ArrayList<Opening> openings = new ArrayList<>();

            for (int i = 0; i < openingCount; i++) {
                System.out.println("  Opening " + (i + 1) + ":");
                double oWidth = readDouble("    Width (ft): ");
                double oHeight = readDouble("    Height (ft): ");
                openings.add(new Opening(Opening.Type.DOOR, oWidth, oHeight, 0));
            }

            int sheets = WallFramingCalculator.calculateSheathingSheets(wall, openings);
            double grossArea = wall.getWallArea();
            double openingArea = 0;
            for (Opening o : openings) {
                openingArea += o.getOpeningArea();
            }

            System.out.println("\n  PLYWOOD WALL SHEATHING");
            System.out.println("  Gross wall area:   " + grossArea + " sq ft");
            System.out.println("  Opening deduction: " + openingArea + " sq ft");
            System.out.println("  Net area:          " + (grossArea - openingArea) + " sq ft");
            System.out.println("  Sheets needed:     " + sheets + " (4x8 plywood, 32 sq ft each)");
        } else {
            System.out.println("  Invalid choice.");
        }
    }

    // ---------------------------------------------------------------
    // 5. Rough Opening Calculator
    // ---------------------------------------------------------------

    private static void roughOpeningCalculator() {
        System.out.println("\n--- Rough Opening Calculator ---");
        System.out.println("  1. Door");
        System.out.println("  2. Window");
        int type = readInt("Choose opening type: ");

        Opening opening;

        if (type == 1) {
            double width = readDouble("Enter door rough opening width (ft): ");
            double height = readDouble("Enter door rough opening height (ft): ");
            opening = new Opening(Opening.Type.DOOR, width, height, 0);
        } else if (type == 2) {
            double width = readDouble("Enter window rough opening width (ft): ");
            double height = readDouble("Enter window rough opening height (ft): ");
            double sill = readDouble("Enter sill height from floor (ft): ");
            opening = new Opening(Opening.Type.WINDOW, width, height, sill);
        } else {
            System.out.println("  Invalid choice.");
            return;
        }

        System.out.println("\n  ROUGH OPENING FRAMING");
        System.out.print(RoughOpeningCalculator.getBreakdown(opening));
    }

    // ---------------------------------------------------------------
    // 6. Full Room Estimate
    // ---------------------------------------------------------------

    private static void fullRoomEstimate() {
        System.out.println("\n--- Full Room Estimate ---");
        System.out.print("Enter room name: ");
        String name = scanner.nextLine();
        double length = readDouble("Enter room length (ft): ");
        double width = readDouble("Enter room width (ft): ");
        double height = readDouble("Enter wall height (ft): ");

        Room room = new Room(name, length, width, height);

        // Wall framing — use perimeter as total wall length
        int corners = readInt("Enter number of corners: ");
        int tIntersections = readInt("Enter number of T-wall intersections: ");
        Wall wall = new Wall(room.getPerimeter(), room.getHeight(), corners, tIntersections);

        // Openings
        int doorCount = readInt("How many doors? ");
        int windowCount = readInt("How many windows? ");

        ArrayList<Opening> openings = new ArrayList<>();

        for (int i = 0; i < doorCount; i++) {
            System.out.println("  Door " + (i + 1) + ":");
            double dWidth = readDouble("    Rough opening width (ft): ");
            double dHeight = readDouble("    Rough opening height (ft): ");
            openings.add(new Opening(Opening.Type.DOOR, dWidth, dHeight, 0));
        }

        for (int i = 0; i < windowCount; i++) {
            System.out.println("  Window " + (i + 1) + ":");
            double wWidth = readDouble("    Rough opening width (ft): ");
            double wHeight = readDouble("    Rough opening height (ft): ");
            double wSill = readDouble("    Sill height from floor (ft): ");
            openings.add(new Opening(Opening.Type.WINDOW, wWidth, wHeight, wSill));
        }

        // --- Calculate everything ---
        int studs = WallFramingCalculator.calculateStuds(wall);
        double plates = WallFramingCalculator.calculatePlateLinearFeet(wall);
        double capPlate = WallFramingCalculator.calculateCapPlate(wall);
        int sheathingSheets = WallFramingCalculator.calculateSheathingSheets(wall, openings);

        int joists = FloorSystemCalculator.calculateJoists(room);
        int subfloorSheets = FloorSystemCalculator.calculateSubfloorSheets(room);

        int totalOpeningPieces = 0;
        for (Opening o : openings) {
            totalOpeningPieces += RoughOpeningCalculator.getTotalPieces(o);
        }

        // --- Print full report ---
        System.out.println("\n==============================================");
        System.out.println("  FULL MATERIAL ESTIMATE");
        System.out.println("  Room: " + room);
        System.out.println("==============================================");

        System.out.println("\n  FLOOR SYSTEM");
        System.out.println("  Floor area:           " + room.getArea() + " sq ft");
        System.out.println("  Floor joists:         " + joists + " (includes 2 rim joists)");
        System.out.println("  Subfloor plywood:     " + subfloorSheets + " sheets (4x8)");

        System.out.println("\n  WALL FRAMING");
        System.out.println("  Wall perimeter:       " + room.getPerimeter() + " linear ft");
        System.out.println("  Wall studs (16\" OC):  " + studs);
        System.out.println("  Plate lumber:         " + plates + " linear ft");
        System.out.println("  Cap plate:            " + capPlate + " linear ft");
        System.out.println("  Sheathing plywood:    " + sheathingSheets + " sheets (4x8)");

        if (!openings.isEmpty()) {
            System.out.println("\n  ROUGH OPENINGS");
            for (Opening o : openings) {
                System.out.print(RoughOpeningCalculator.getBreakdown(o));
            }
            System.out.println("  Total opening pieces: " + totalOpeningPieces);
        }

        System.out.println("\n==============================================");
        System.out.println("  MATERIAL SUMMARY - " + room.getName());
        System.out.println("==============================================");
        System.out.println("  Floor joists:         " + joists);
        System.out.println("  Subfloor sheets:      " + subfloorSheets);
        System.out.println("  Wall studs:           " + studs);
        System.out.println("  Plate lumber:         " + plates + " linear ft");
        System.out.println("  Cap plate:            " + capPlate + " linear ft");
        System.out.println("  Sheathing sheets:     " + sheathingSheets);
        System.out.println("  Opening framing:      " + totalOpeningPieces + " pieces");
        System.out.println("==============================================");
    }

    // ---------------------------------------------------------------
    // DB Helper — checks availability before any DB operation
    // ---------------------------------------------------------------

    private static boolean checkDb() {
        if (!dbAvailable) {
            System.out.println("\n  Database is not available.");
            System.out.println("  Start the PostgreSQL Docker container and restart the app.");
            return false;
        }
        return true;
    }

    // ---------------------------------------------------------------
    // 7. Load Rooms from CSV (display on screen)
    // ---------------------------------------------------------------

    private static void loadFromCsv() {
        System.out.println("\n--- Load Rooms from CSV ---");
        System.out.print("Enter CSV file path: ");
        String filePath = scanner.nextLine().trim();

        try {
            ArrayList<Room> rooms = CsvReader.readRooms(filePath);

            if (rooms.isEmpty()) {
                System.out.println("  No valid rooms found in file.");
                return;
            }

            System.out.println("\n  Loaded " + rooms.size() + " room(s):\n");

            for (int i = 0; i < rooms.size(); i++) {
                Room room = rooms.get(i);
                Wall wall = new Wall(room.getPerimeter(), room.getHeight(), 4, 0);

                System.out.println("  " + (i + 1) + ". " + room);
                System.out.println("     Area: " + room.getArea() + " sq ft"
                        + " | Studs: " + WallFramingCalculator.calculateStuds(wall)
                        + " | Joists: " + FloorSystemCalculator.calculateJoists(room)
                        + " | Subfloor: " + FloorSystemCalculator.calculateSubfloorSheets(room) + " sheets");
            }

        } catch (FileNotFoundException e) {
            System.out.println("  ERROR: File not found - " + filePath);
            System.out.println("  Make sure the path is correct and the file exists.");
        }
    }

    // ---------------------------------------------------------------
    // 8. CSV -> Full Report File
    // ---------------------------------------------------------------

    private static void loadCsvAndGenerateReport() {
        System.out.println("\n--- Generate Report from CSV ---");
        System.out.print("Enter CSV file path: ");
        String csvPath = scanner.nextLine().trim();

        ArrayList<Room> rooms;
        try {
            rooms = CsvReader.readRooms(csvPath);
        } catch (FileNotFoundException e) {
            System.out.println("  ERROR: File not found - " + csvPath);
            System.out.println("  Make sure the path is correct and the file exists.");
            return;
        }

        if (rooms.isEmpty()) {
            System.out.println("  No valid rooms found in file. Report not generated.");
            return;
        }

        System.out.println("  Loaded " + rooms.size() + " room(s).");
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine().trim();

        System.out.print("Enter output file path (e.g. estimate_report.txt): ");
        String outputPath = scanner.nextLine().trim();

        try {
            ReportWriter.writeReport(projectName, rooms, outputPath);
            System.out.println("\n  Report saved to: " + outputPath);
            System.out.println("  Open the file to see the full material estimate.");
        } catch (IOException e) {
            System.out.println("  ERROR: Could not write report - " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // 9. Save Project to DB (from CSV)
    // ---------------------------------------------------------------

    private static void saveProjectToDb() {
        if (!checkDb()) return;

        System.out.println("\n--- Save Project to Database ---");
        System.out.print("Enter CSV file path: ");
        String csvPath = scanner.nextLine().trim();

        ArrayList<Room> rooms;
        try {
            rooms = CsvReader.readRooms(csvPath);
        } catch (FileNotFoundException e) {
            System.out.println("  ERROR: File not found - " + csvPath);
            return;
        }

        if (rooms.isEmpty()) {
            System.out.println("  No valid rooms found. Nothing saved.");
            return;
        }

        System.out.println("  Loaded " + rooms.size() + " room(s).");
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine().trim();

        try {
            Connection conn = DatabaseConfig.getConnection();
            int projectId = ProjectDao.saveProject(conn, projectName, rooms);
            conn.close();
            System.out.println("\n  Project \"" + projectName + "\" saved! (ID: " + projectId + ")");
        } catch (SQLException e) {
            System.out.println("  ERROR: Could not save project - " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // 10. Load Project from DB
    // ---------------------------------------------------------------

    private static void loadProjectFromDb() {
        if (!checkDb()) return;

        System.out.println("\n--- Load Project from Database ---");
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine().trim();

        try {
            Connection conn = DatabaseConfig.getConnection();
            boolean found = ProjectDao.loadProject(conn, projectName);
            conn.close();
            if (!found) {
                System.out.println("  Project \"" + projectName + "\" not found.");
            }
        } catch (SQLException e) {
            System.out.println("  ERROR: Could not load project - " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // 11. List All Projects
    // ---------------------------------------------------------------

    private static void listAllProjects() {
        if (!checkDb()) return;

        System.out.println("\n--- All Saved Projects ---");
        try {
            Connection conn = DatabaseConfig.getConnection();
            ProjectDao.listProjects(conn);
            conn.close();
        } catch (SQLException e) {
            System.out.println("  ERROR: Could not list projects - " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // 12. Delete a Project
    // ---------------------------------------------------------------

    private static void deleteProject() {
        if (!checkDb()) return;

        System.out.println("\n--- Delete Project ---");
        System.out.print("Enter project name to delete: ");
        String projectName = scanner.nextLine().trim();

        try {
            Connection conn = DatabaseConfig.getConnection();
            boolean deleted = ProjectDao.deleteProject(conn, projectName);
            conn.close();
            if (deleted) {
                System.out.println("  Project \"" + projectName + "\" deleted.");
            } else {
                System.out.println("  Project \"" + projectName + "\" not found.");
            }
        } catch (SQLException e) {
            System.out.println("  ERROR: Could not delete project - " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Input Helpers
    // ---------------------------------------------------------------

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value < 0) {
                    System.out.println("  Value cannot be negative. Try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Try again.");
            }
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < 0) {
                    System.out.println("  Value cannot be negative. Try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Try again.");
            }
        }
    }
}
