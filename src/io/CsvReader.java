package io;

import model.Room;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Reads room dimensions from a CSV file.
 * Expected format: name,length,width,height
 * First row is treated as a header and skipped.
 */
public class CsvReader {

    /**
     * Reads rooms from a CSV file and returns them as an ArrayList.
     * Skips the header row and any malformed lines (with a warning printed).
     *
     * @param filePath path to the CSV file
     * @return list of Room objects parsed from the file
     * @throws FileNotFoundException if the file does not exist
     */
    public static ArrayList<Room> readRooms(String filePath) throws FileNotFoundException {
        ArrayList<Room> rooms = new ArrayList<>();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip header row
                if (lineNumber == 1) {
                    continue;
                }

                // Skip blank lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    Room room = parseLine(line, lineNumber);
                    rooms.add(room);
                } catch (IllegalArgumentException e) {
                    System.out.println("  WARNING: Skipping line " + lineNumber + " - " + e.getMessage());
                }
            }

        } catch (FileNotFoundException e) {
            throw e; // re-throw so caller can handle it
        } catch (IOException e) {
            System.out.println("  ERROR: Problem reading file - " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore close errors
                }
            }
        }

        return rooms;
    }

    /**
     * Parses a single CSV line into a Room object.
     * Expected format: name,length,width,height
     */
    private static Room parseLine(String line, int lineNumber) {
        String[] parts = line.split(",");

        if (parts.length < 4) {
            throw new IllegalArgumentException(
                    "Expected 4 columns (name,length,width,height) but found " + parts.length);
        }

        String name = parts[0].trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Room name is empty");
        }

        double length;
        double width;
        double height;

        try {
            length = Double.parseDouble(parts[1].trim());
            width = Double.parseDouble(parts[2].trim());
            height = Double.parseDouble(parts[3].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in dimensions - " + e.getMessage());
        }

        if (length <= 0 || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive numbers");
        }

        return new Room(name, length, width, height);
    }
}
