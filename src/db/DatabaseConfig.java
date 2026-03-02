package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the database connection using environment variables.
 * Falls back to defaults that match the Docker container setup.
 *
 * Set these environment variables to override:
 *   DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
 */
public class DatabaseConfig {

    private static final String DEFAULT_HOST = "db";
    private static final String DEFAULT_PORT = "5432";
    private static final String DEFAULT_DB = "mintz_estimator";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "mintz123";

    /**
     * Creates and returns a new database connection.
     * Reads credentials from environment variables with defaults.
     */
    public static Connection getConnection() throws SQLException {
        String host = getEnvOrDefault("DB_HOST", DEFAULT_HOST);
        String port = getEnvOrDefault("DB_PORT", DEFAULT_PORT);
        String dbName = getEnvOrDefault("DB_NAME", DEFAULT_DB);
        String user = getEnvOrDefault("DB_USER", DEFAULT_USER);
        String password = getEnvOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Reads an environment variable, returning the default if not set.
     */
    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }
}
