package db;

import model.Room;
import model.Wall;
import calculator.FloorSystemCalculator;
import calculator.WallFramingCalculator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Data Access Object for project CRUD operations.
 * Separates all database logic from the rest of the application.
 */
public class ProjectDao {

    private static final int DEFAULT_CORNERS = 4;
    private static final int DEFAULT_T_INTERSECTIONS = 0;

    // -----------------------------------------------------------------
    // CREATE — Save a new project with rooms and calculated estimates
    // -----------------------------------------------------------------

    /**
     * Saves a project with all its rooms and estimates to the database.
     * Returns the generated project ID.
     */
    public static int saveProject(Connection conn, String projectName, ArrayList<Room> rooms)
            throws SQLException {

        // Insert project
        PreparedStatement projectStmt = conn.prepareStatement(
                "INSERT INTO projects (name) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS
        );
        projectStmt.setString(1, projectName);
        projectStmt.executeUpdate();

        ResultSet projectKeys = projectStmt.getGeneratedKeys();
        projectKeys.next();
        int projectId = projectKeys.getInt(1);
        projectKeys.close();
        projectStmt.close();

        // Insert each room and its estimate
        for (Room room : rooms) {
            int roomId = insertRoom(conn, projectId, room);
            insertEstimate(conn, roomId, room);
        }

        return projectId;
    }

    private static int insertRoom(Connection conn, int projectId, Room room) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO rooms (project_id, name, length, width, height) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        stmt.setInt(1, projectId);
        stmt.setString(2, room.getName());
        stmt.setDouble(3, room.getLength());
        stmt.setDouble(4, room.getWidth());
        stmt.setDouble(5, room.getHeight());
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        int roomId = keys.getInt(1);
        keys.close();
        stmt.close();

        return roomId;
    }

    private static void insertEstimate(Connection conn, int roomId, Room room) throws SQLException {
        Wall wall = new Wall(room.getPerimeter(), room.getHeight(),
                DEFAULT_CORNERS, DEFAULT_T_INTERSECTIONS);

        int joists = FloorSystemCalculator.calculateJoists(room);
        int subfloor = FloorSystemCalculator.calculateSubfloorSheets(room);
        int studs = WallFramingCalculator.calculateStuds(wall);
        double plates = WallFramingCalculator.calculatePlateLinearFeet(wall);
        double capPlate = WallFramingCalculator.calculateCapPlate(wall);
        int sheathing = WallFramingCalculator.calculateSheathingSheets(wall);

        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO estimates (room_id, floor_joists, subfloor_sheets, "
                + "wall_studs, plate_lumber_lf, cap_plate_lf, sheathing_sheets) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)"
        );
        stmt.setInt(1, roomId);
        stmt.setInt(2, joists);
        stmt.setInt(3, subfloor);
        stmt.setInt(4, studs);
        stmt.setDouble(5, plates);
        stmt.setDouble(6, capPlate);
        stmt.setInt(7, sheathing);
        stmt.executeUpdate();
        stmt.close();
    }

    // -----------------------------------------------------------------
    // READ — Load a project by name and display its data
    // -----------------------------------------------------------------

    /**
     * Loads a project by name and prints the full estimate.
     * Returns true if the project was found, false otherwise.
     */
    public static boolean loadProject(Connection conn, String projectName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT p.id, p.name, p.created_at FROM projects p WHERE p.name = ?"
        );
        stmt.setString(1, projectName);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            rs.close();
            stmt.close();
            return false;
        }

        int projectId = rs.getInt("id");
        String name = rs.getString("name");
        String createdAt = rs.getString("created_at");
        rs.close();
        stmt.close();

        System.out.println("\n==============================================");
        System.out.println("  PROJECT: " + name);
        System.out.println("  Saved: " + createdAt);
        System.out.println("==============================================");

        // Load rooms and estimates
        PreparedStatement roomStmt = conn.prepareStatement(
                "SELECT r.name, r.length, r.width, r.height, "
                + "e.floor_joists, e.subfloor_sheets, e.wall_studs, "
                + "e.plate_lumber_lf, e.cap_plate_lf, e.sheathing_sheets "
                + "FROM rooms r "
                + "JOIN estimates e ON e.room_id = r.id "
                + "WHERE r.project_id = ? ORDER BY r.id"
        );
        roomStmt.setInt(1, projectId);
        ResultSet roomRs = roomStmt.executeQuery();

        int totalJoists = 0, totalSubfloor = 0, totalStuds = 0, totalSheathing = 0;
        double totalPlates = 0, totalCapPlate = 0, totalArea = 0;
        int roomCount = 0;

        while (roomRs.next()) {
            roomCount++;
            String roomName = roomRs.getString("name");
            double length = roomRs.getDouble("length");
            double width = roomRs.getDouble("width");
            double height = roomRs.getDouble("height");
            int joists = roomRs.getInt("floor_joists");
            int subfloor = roomRs.getInt("subfloor_sheets");
            int studs = roomRs.getInt("wall_studs");
            double plates = roomRs.getDouble("plate_lumber_lf");
            double capPlate = roomRs.getDouble("cap_plate_lf");
            int sheathing = roomRs.getInt("sheathing_sheets");

            double area = length * width;
            totalArea += area;
            totalJoists += joists;
            totalSubfloor += subfloor;
            totalStuds += studs;
            totalPlates += plates;
            totalCapPlate += capPlate;
            totalSheathing += sheathing;

            System.out.println("\n  " + roomCount + ". " + roomName
                    + " (" + length + "' x " + width + "' x " + height + "')");
            System.out.println("     Area: " + area + " sq ft");
            System.out.println("     Joists: " + joists + " | Subfloor: " + subfloor
                    + " | Studs: " + studs + " | Sheathing: " + sheathing);
            System.out.println("     Plates: " + plates + " lf | Cap plate: " + capPlate + " lf");
        }
        roomRs.close();
        roomStmt.close();

        System.out.println("\n----------------------------------------------");
        System.out.println("  TOTALS (" + roomCount + " rooms, " + totalArea + " sq ft)");
        System.out.println("----------------------------------------------");
        System.out.println("  Joists: " + totalJoists + " | Subfloor: " + totalSubfloor + " sheets");
        System.out.println("  Studs: " + totalStuds + " | Sheathing: " + totalSheathing + " sheets");
        System.out.println("  Plates: " + totalPlates + " lf | Cap plate: " + totalCapPlate + " lf");
        System.out.println("==============================================");

        return true;
    }

    // -----------------------------------------------------------------
    // LIST — Show all saved projects
    // -----------------------------------------------------------------

    /**
     * Lists all saved projects with room count.
     */
    public static void listProjects(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT p.id, p.name, p.created_at, COUNT(r.id) AS room_count "
                + "FROM projects p LEFT JOIN rooms r ON r.project_id = p.id "
                + "GROUP BY p.id, p.name, p.created_at ORDER BY p.created_at DESC"
        );
        ResultSet rs = stmt.executeQuery();

        boolean found = false;
        while (rs.next()) {
            if (!found) {
                System.out.println("\n  SAVED PROJECTS");
                System.out.println("  ----------------------------------------");
                found = true;
            }
            System.out.println("  [" + rs.getInt("id") + "] "
                    + rs.getString("name")
                    + " - " + rs.getInt("room_count") + " room(s)"
                    + " - " + rs.getString("created_at"));
        }

        if (!found) {
            System.out.println("\n  No saved projects found.");
        }

        rs.close();
        stmt.close();
    }

    // -----------------------------------------------------------------
    // DELETE — Remove a project by name
    // -----------------------------------------------------------------

    /**
     * Deletes a project and all associated rooms/estimates (via CASCADE).
     * Returns true if the project was found and deleted.
     */
    public static boolean deleteProject(Connection conn, String projectName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM projects WHERE name = ?"
        );
        stmt.setString(1, projectName);
        int rows = stmt.executeUpdate();
        stmt.close();

        return rows > 0;
    }
}
