package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates the database tables if they don't already exist.
 * Called once at application startup.
 */
public class DatabaseSetup {

    /**
     * Creates the projects, rooms, and estimates tables.
     */
    public static void initialize(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute(
            "CREATE TABLE IF NOT EXISTS projects ("
            + "  id SERIAL PRIMARY KEY,"
            + "  name VARCHAR(255) NOT NULL,"
            + "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ")"
        );

        stmt.execute(
            "CREATE TABLE IF NOT EXISTS rooms ("
            + "  id SERIAL PRIMARY KEY,"
            + "  project_id INTEGER REFERENCES projects(id) ON DELETE CASCADE,"
            + "  name VARCHAR(255) NOT NULL,"
            + "  length DOUBLE PRECISION NOT NULL,"
            + "  width DOUBLE PRECISION NOT NULL,"
            + "  height DOUBLE PRECISION NOT NULL"
            + ")"
        );

        stmt.execute(
            "CREATE TABLE IF NOT EXISTS estimates ("
            + "  id SERIAL PRIMARY KEY,"
            + "  room_id INTEGER REFERENCES rooms(id) ON DELETE CASCADE,"
            + "  floor_joists INTEGER,"
            + "  subfloor_sheets INTEGER,"
            + "  wall_studs INTEGER,"
            + "  plate_lumber_lf DOUBLE PRECISION,"
            + "  cap_plate_lf DOUBLE PRECISION,"
            + "  sheathing_sheets INTEGER"
            + ")"
        );

        stmt.close();
    }
}
