package org.concordia.runner;

import org.concordia.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * TournamentRunner — lives in the teacher's private repo.
 *
 * Runs all 6 match series (student P1 vs each boss, student P2 vs each boss),
 * up to 20 attempts per series stopping at the first win.
 * Prints a structured summary to stdout (captured by Actions log)
 * and writes a detailed match_log.txt for the artifact upload.
 *
 * Win conditions:
 *   Easy:   half win = 25 pts,  full win = 25 pts AND beat the boss
 *   Medium: half win = 75 pts,  full win = 75 pts AND beat the boss
 *   Hard:   half win = 150 pts, full win = 150 pts AND beat the boss
 */
public class TournamentRunner {

    // Points threshold for a half-win at each difficulty
    private static final int[] THRESHOLDS = { 25, 75, 150 };
    private static final String[] DIFFICULTY = { "EASY", "MEDIUM", "HARD" };
    private static final int MAX_ATTEMPTS = 20;

    public static void main(String[] args) throws IOException {
        String repoName   = args.length > 0 ? args[0] : "unknown/repo";
        String studentId  = args.length > 1 ? args[1] : "unknown";

        PrintWriter log = new PrintWriter(new FileWriter("match_log.txt"));

        printHeader(repoName, studentId, log);

        int totalHalfWins = 0;
        int totalFullWins = 0;

        // Six series: for each difficulty, P1 vs boss then P2 vs boss
        for (int d = 0; d < 3; d++) {
            totalHalfWins += runSeries("Player1", d, true,  log);
            totalHalfWins += runSeries("Player2", d, false, log);
        }

        printSummary(totalHalfWins, totalFullWins, log);

        log.flush();
        log.close();
    }

    /**
     * Runs up to MAX_ATTEMPTS games for one (player, difficulty) pairing.
     * Stops at the first win.
     *
     * @param playerLabel "Player1" or "Player2" — for display only
     * @param difficultyIdx  0=easy, 1=medium, 2=hard
     * @param studentIsP1    true if the student's class plays as P1 this series
     * @return 1 if a half-win was recorded, 0 otherwise
     */
    private static int runSeries(String playerLabel, int difficultyIdx,
                                  boolean studentIsP1, PrintWriter log) {
        int threshold = THRESHOLDS[difficultyIdx];
        String diff   = DIFFICULTY[difficultyIdx];

        section(String.format("%-7s vs %s boss", playerLabel, diff), log);

        boolean halfWin = false;
        boolean fullWin = false;
        boolean cheated = false;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            MatchResult result = runOneGame(difficultyIdx, studentIsP1);

            int studentScore = studentIsP1 ? result.p1Score : result.p2Score;
            int bossScore    = studentIsP1 ? result.p2Score : result.p1Score;

            // Teleport / cheating check
            int teleportFlag = studentIsP1 ? result.p1Teleport : result.p2Teleport;
            if (teleportFlag == 1 && !cheated) {
                cheated = true;
                warn(String.format("  ⚠  CHEAT DETECTED — %s teleported in attempt %d. " +
                                   "Result counted but flagged.", playerLabel, attempt), log);
            }

            String attemptLine = String.format("  Attempt %2d/%2d | Student: %4d | Boss: %4d",
                    attempt, MAX_ATTEMPTS, studentScore, bossScore);

            halfWin = studentScore >= threshold;
            fullWin = halfWin && studentScore > bossScore;

            if (halfWin) {
                String winType = fullWin ? "FULL WIN" : "HALF WIN";
                pass(attemptLine + " | " + winType, log);
                break;
            } else {
                info(attemptLine + " | no win", log);
            }
        }

        if (!halfWin) {
            fail(String.format("  %s could not defeat the %s boss in %d attempts.",
                    playerLabel, diff, MAX_ATTEMPTS), log);
        }

        return halfWin ? 1 : 0;
    }

    /**
     * Runs a single game and returns scores + cheat flags.
     * Swaps which physical class plays P1/P2 based on studentIsP1.
     */
    private static MatchResult runOneGame(int difficultyIdx, boolean studentIsP1) {
        try {
            MapLoader loader = new MapLoader();
            Tile[][] tiles   = loader.load("Grotto.txt");

            // Instantiate the correct boss for this difficulty
            Player1 p1;
            Player2 p2;

            if (studentIsP1) {
                p1 = new org.concordia.Player1(40, 4);       // student
                p2 = bossPlayer2(difficultyIdx, 40, 26);     // teacher boss
            } else {
                p1 = bossPlayer1(difficultyIdx, 40, 4);      // teacher boss
                p2 = new org.concordia.Player2(40, 26);      // student
            }

            GameConfig config = new GameConfig();
            config.gameTick = 0;   // max speed for autograder

            GameSetup setup = new GameSetup();
            setup.spawnEntities(p1, p2, tiles);

            GameEngine engine = new GameEngine(tiles, p1, p2, config.rounds, config.gameTick);
            engine.playGame();

            return new MatchResult(
                p1.score, p2.score,
                engine.getP1TeleportFlag(), engine.getP2TeleportFlag()
            );

        } catch (Exception e) {
            // If the student's code throws, count it as a 0-score attempt
            System.err.println("Game threw an exception: " + e.getMessage());
            return new MatchResult(0, 0, 0, 0);
        }
    }

    // -----------------------------------------------------------------------
    // Boss factory methods — swap in the appropriate difficulty class.
    // These classes live in your private repo and are never exposed.
    // -----------------------------------------------------------------------

    private static Player1 bossPlayer1(int difficultyIdx, int x, int y) {
        return switch (difficultyIdx) {
            case 0 -> new org.concordia.bosses.easy.BossPlayer1Easy(x, y);
            case 1 -> new org.concordia.bosses.medium.BossPlayer1Medium(x, y);
            case 2 -> new org.concordia.bosses.hard.BossPlayer1Hard(x, y);
            default -> throw new IllegalArgumentException("Unknown difficulty: " + difficultyIdx);
        };
    }

    private static Player2 bossPlayer2(int difficultyIdx, int x, int y) {
        return switch (difficultyIdx) {
            case 0 -> new org.concordia.bosses.easy.BossPlayer2Easy(x, y);
            case 1 -> new org.concordia.bosses.medium.BossPlayer2Medium(x, y);
            case 2 -> new org.concordia.bosses.hard.BossPlayer2Hard(x, y);
            default -> throw new IllegalArgumentException("Unknown difficulty: " + difficultyIdx);
        };
    }

    // -----------------------------------------------------------------------
    // Output helpers — formatted for GitHub Actions log readability
    // -----------------------------------------------------------------------

    private static void printHeader(String repo, String student, PrintWriter log) {
        String line = "=".repeat(65);
        out(line, log);
        out("  CONCORDIA GAME TOURNAMENT — AUTOGRADER", log);
        out("  Repo:    " + repo, log);
        out("  Student: " + student, log);
        out(line, log);
        out("", log);
    }

    private static void printSummary(int halfWins, int fullWins, PrintWriter log) {
        out("", log);
        out("=".repeat(65), log);
        out("  FINAL RESULTS", log);
        out(String.format("  Half wins: %d / 6", halfWins), log);
        out(String.format("  Full wins: %d / 6", fullWins), log);
        if (halfWins == 6) pass("  All bosses defeated!", log);
        else if (halfWins == 0) fail("  No bosses defeated.", log);
        else info(String.format("  %d/6 boss series won.", halfWins), log);
        out("=".repeat(65), log);
    }

    private static void section(String title, PrintWriter log) {
        out("", log);
        out("--- " + title + " " + "-".repeat(Math.max(0, 60 - title.length())), log);
    }

    // GitHub Actions log commands for coloured output in the Actions UI
    private static void pass(String msg, PrintWriter log) {
        System.out.println("::notice ::" + msg);
        log.println("[PASS] " + msg);
    }
    private static void fail(String msg, PrintWriter log) {
        System.out.println("::warning ::" + msg);
        log.println("[FAIL] " + msg);
    }
    private static void warn(String msg, PrintWriter log) {
        System.out.println("::error ::" + msg);
        log.println("[CHEAT] " + msg);
    }
    private static void info(String msg, PrintWriter log) {
        System.out.println(msg);
        log.println(msg);
    }
    private static void out(String msg, PrintWriter log) {
        System.out.println(msg);
        log.println(msg);
    }

    // -----------------------------------------------------------------------
    // Simple result container
    // -----------------------------------------------------------------------
    private record MatchResult(int p1Score, int p2Score, int p1Teleport, int p2Teleport) {}
}
