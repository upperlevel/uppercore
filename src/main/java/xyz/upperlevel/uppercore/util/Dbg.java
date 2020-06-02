package xyz.upperlevel.uppercore.util;

import xyz.upperlevel.uppercore.Uppercore;

public class Dbg {
    private static boolean active = true;

    /**
     * Prints the message on console only if the debug mode is active.
     * @param message The message to print.
     */
    public static void p(String message) {
        if (active) {
            String callerName = "?";
            try {
                Class<?> caller = Class.forName(new Exception().getStackTrace()[1].getClassName());
                callerName = caller.getSimpleName();
            } catch (ClassNotFoundException ignored) {
            }
            Uppercore.logger().info(String.format("[%s] %s", callerName, message));
        }
    }

    public static void loadConfig() {
    }
}
