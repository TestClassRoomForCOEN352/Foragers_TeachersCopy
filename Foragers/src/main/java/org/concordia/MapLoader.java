package org.concordia;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
 
public class MapLoader {
 
    public static final int MAP_WIDTH  = 80;
    public static final int MAP_HEIGHT = 30;
 
    private static final String MAP_INDEX = "maps.txt";
    private static final Random rng = new Random();
 
    private static final int[][] OFFSETS = {
        {-1, -1}, { 0, -1}, { 1, -1},
        {-1,  0},           { 1,  0},
        {-1,  1}, { 0,  1}, { 1,  1}
    };
 
    //Map Randomizer
    public Tile[][] load() throws IOException {
        String chosen = pickRandomMap();
        System.out.println("[MapLoader] Selected map: " + chosen);
        return load(chosen);
    }

    public Tile[][] load(String fileName) throws IOException {
        InputStream is = MapLoader.class.getResourceAsStream(fileName);
        if (is == null) throw new IOException("Cannot find map resource: " + fileName);
 
        Tile[][] grid = new Tile[MAP_HEIGHT][MAP_WIDTH];
 
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                String line = reader.readLine();
                if (line == null)
                    throw new IllegalStateException(
                        String.format("File ended at row %d — expected %d rows. " +
                                      "Check that MAP_HEIGHT matches your map file.", y, MAP_HEIGHT));
                if (line.length() < MAP_WIDTH)
                    throw new IllegalStateException(
                        String.format("Row %d has %d chars, expected %d. " +
                                      "Check that MAP_WIDTH matches your map file.", y, line.length(), MAP_WIDTH));
 
                for (int x = 0; x < MAP_WIDTH; x++)
                    grid[y][x] = new Tile(line.charAt(x), x, y);
            }
        }
 
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = grid[y][x];
                for (int dir = 0; dir < OFFSETS.length; dir++) {
                    int nx = x + OFFSETS[dir][0];
                    int ny = y + OFFSETS[dir][1];
                    if (nx >= 0 && nx < MAP_WIDTH && ny >= 0 && ny < MAP_HEIGHT)
                        tile.neighbours[dir] = grid[ny][nx];
                }
            }
        }
        return grid;
    }
 
    private String pickRandomMap() throws IOException {
        InputStream is = MapLoader.class.getResourceAsStream(MAP_INDEX);
        if (is == null) throw new IOException(
            "Cannot find map index: " + MAP_INDEX + ". " +
            "Make sure maps.txt exists in src/main/resources/org/concordia/");
 
        List<String> maps = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) maps.add(line);
            }
        }
 
        if (maps.isEmpty()) throw new IOException("maps.txt is empty — no maps to load.");
        return maps.get(rng.nextInt(maps.size()));
    }
}
