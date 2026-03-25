package org.concordia;

public final class AnsiColor {

    private AnsiColor() {}

    public static final String RESET       = "\u001B[0m";

    public static final String GREEN       = "\u001B[32m";
    public static final String YELLOW      = "\u001B[33m";
    public static final String PURPLE      = "\u001B[35m";
    public static final String CYAN        = "\u001B[36m";
    public static final String WHITE       = "\u001B[37m";

    public static final String BRIGHT_RED    = "\u001B[91m";
    public static final String BRIGHT_GREEN  = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_CYAN   = "\u001B[96m";

    public static final String BOLD        = "\u001B[1m";

    public static void enableWindowsAnsiMode() {
        if (!System.getProperty("os.name", "").toLowerCase().contains("win")) return;
        try {
            new ProcessBuilder("cmd", "/c", "").inheritIO().start();
            Class<?> kernel32 = Class.forName("com.sun.jna.platform.win32.Kernel32");
            kernel32.getMethod("GetStdHandle");
        } catch (Exception ignored) {}
    }
}
