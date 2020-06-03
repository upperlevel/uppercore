package xyz.upperlevel.uppercore.util;

import xyz.upperlevel.uppercore.Uppercore;

public class Dbg {
    private static boolean active = true;

    private static Class<?> getCaller() {
        try {
            return Class.forName(new Exception().getStackTrace()[2].getClassName());
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    private static void p0(String message, Class<?> caller) {
        if (active) {
            String callerName = caller != null ? caller.getSimpleName() : "?";
            Uppercore.logger().info(String.format("[%s] %s", callerName, message));
        }
    }

    /**
     * Prints a message to the console only if the debug-mode is active.
     *
     * @param message The message.
     */
    public static void p(String message) {
        p0(message, getCaller());
    }

    /**
     * Prints a formatted message to the console only if the debug-mode is active.
     *
     * @param format The String format to use.
     * @param args   The args that the format takes in.
     */
    public static void pf(String format, Object... args) {
        p0(String.format(format, args), getCaller());
    }

    public static void loadConfig() {
        // TODO load debug-mode active, currently always true
    }
}
