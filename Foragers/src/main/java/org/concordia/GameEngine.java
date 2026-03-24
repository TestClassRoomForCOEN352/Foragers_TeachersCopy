package org.concordia;

import java.util.concurrent.TimeUnit;
import static org.concordia.MapLoader.MAP_HEIGHT;
import static org.concordia.MapLoader.MAP_WIDTH;

public class GameEngine {
    public int tickLength;
    public GameState state;
    private int teleport;

    public GameEngine(Tile[][] tiles, Player1 p1, Player2 p2, int rounds, int tickLength) {
        this.tickLength = tickLength;
        this.state = new GameState(tiles, p1, p2, rounds);
    }

    public void render(Tile[][] tiles) {
        StringBuilder sb = new StringBuilder(MAP_WIDTH * (MAP_HEIGHT + 1) * 12); // extra room for escape codes

        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                Tile tile = tiles[i][j];

                if (tile.player1Present && tile.player2Present) {
                    sb.append(AnsiColor.BOLD).append(AnsiColor.BRIGHT_YELLOW).append('3').append(AnsiColor.RESET);
                } else if (tile.player1Present) {
                    sb.append(AnsiColor.BOLD).append(AnsiColor.BRIGHT_CYAN).append('1').append(AnsiColor.RESET);
                } else if (tile.player2Present) {
                    sb.append(AnsiColor.BOLD).append(AnsiColor.BRIGHT_RED).append('2').append(AnsiColor.RESET);
                } else if (tile.treasurePresent) {
                    String colour = (tile.treasure.value == 10) ? AnsiColor.YELLOW : AnsiColor.BRIGHT_GREEN;
                    sb.append(colour).append(tile.treasure.texture).append(AnsiColor.RESET);
                } else if (tile.collision) {
                    sb.append(AnsiColor.WHITE).append(tile.texture).append(AnsiColor.RESET);
                } else {
                    sb.append(AnsiColor.BLUE).append(tile.texture).append(AnsiColor.RESET);
                }
            }
            sb.append('\n');
        }
        System.out.print(sb);
    }

    public void updateStateP1(GameState state, Tile tile) {
        state.tiles[state.p1_y][state.p1_x].player1Present = false;
        tile.player1Present = true;
        state.p1.x = tile.x;
        state.p1.y = tile.y;
        state.p1_x = tile.x;
        state.p1_y = tile.y;
        if (tile.treasurePresent) {
            state.p1.score += tile.treasure.value;
            state.p1_score += tile.treasure.value;
            tile.treasure = null;
            tile.treasurePresent = false;
        }
    }

    public void updateStateP2(GameState state, Tile tile) {
        state.tiles[state.p2_y][state.p2_x].player2Present = false;
        tile.player2Present = true;
        state.p2.x = tile.x;
        state.p2.y = tile.y;
        state.p2_x = tile.x;
        state.p2_y = tile.y;
        if (tile.treasurePresent) {
            state.p2.score += tile.treasure.value;
            state.p2_score += tile.treasure.value;
            tile.treasure = null;
            tile.treasurePresent = false;
        }
    }

    public void player1Decision(GameState state) {
        Tile next = state.p1.moveDecision(state);
        if (next == null || next.collision) {
            System.out.println("Player 1 returned an invalid tile — skipping turn.");
            return;
        }
        Tile current = state.tiles[state.p1_y][state.p1_x];
        if (!isNeighbour(current, next)) {
            this.teleport = 1;
            System.out.println("Player 1 moved too fast. Submission flagged for teleporting.");
            return;
        }
        updateStateP1(state, next);
    }

    public void player2Decision(GameState state) {
        Tile next = state.p2.moveDecision(state);
        if (next == null || next.collision) {
            System.out.println("Player 2 returned an invalid tile — skipping turn.");
            return;
        }
        Tile current = state.tiles[state.p2_y][state.p2_x];
        if (!isNeighbour(current, next)) {
            this.teleport = 1;
            System.out.println("Player 2 moved too fast. Submission flagged for teleporting.");
            return;
        }
        updateStateP2(state, next);
    }

    private boolean isNeighbour(Tile current, Tile candidate) {
        for (Tile n : current.neighbours)
            if (n == candidate) return true;
        return false;
    }

    public void gameTick() {
        try {
            TimeUnit.SECONDS.sleep(tickLength);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int playGame() {
        while (state.rounds_left-- > 0) {
            render(state.tiles);
            player1Decision(state);
            player2Decision(state);
            gameTick();
        }
        render(state.tiles);

        if (state.p1.score == state.p2.score) return 3;
        return (state.p1.score > state.p2.score) ? 1 : 2;
    }

    public int getTeleport(){
        return this.teleport;
    }
}
