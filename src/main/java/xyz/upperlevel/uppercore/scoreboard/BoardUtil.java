package xyz.upperlevel.uppercore.scoreboard;

public class BoardUtil {

    public static final int MAX_LINES = 15;
    public static final int MAX_TITLE_CHARS = 32;
    public static final int MAX_PREFIX_CHARS = 16;
    public static final int MAX_ENTRY_CHARS = 40; // >= 1.8
    public static final int MAX_SUFFIX_CHARS = 16;

    private BoardUtil() {
    }

    public static void divideLine(String line, StringBuffer prefix, StringBuffer entry, StringBuffer suffix) {
        if (!line.isEmpty()) {
            int pre = Math.min(line.length(), MAX_PREFIX_CHARS);
            prefix.append(line.substring(0, pre));
            line = line.substring(pre, line.length());

            if (!line.isEmpty()) {
                int mid = Math.min(line.length(), MAX_ENTRY_CHARS);
                entry.append(line.substring(0, mid));
                line = line.substring(mid, line.length());

                if (!line.isEmpty()) {
                    int suf = Math.min(line.length(), MAX_SUFFIX_CHARS);
                    suffix.append(line.substring(0, suf));
                }
            }
        }
    }
}
