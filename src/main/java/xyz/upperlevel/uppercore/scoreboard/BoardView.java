package xyz.upperlevel.uppercore.scoreboard;

import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.RESET;
import static xyz.upperlevel.uppercore.scoreboard.Scoreboard.MAX_LINES;

public class ScoreboardView {

    @Getter
    private final Player player;

    private static final int MAX_TITLE_CHARS = 32;

    private org.bukkit.scoreboard.Scoreboard handle;
    private Objective objective;
    private final Set<String> entries = new HashSet<>();

    @Getter
    private PlaceholderValue<String> title;
    private final Line[] lines = new Line[MAX_LINES];

    public static final int MAX_PREFIX_CHARS = 16;
    public static final int MAX_ENTRY_CHARS = 40;
    public static final int MAX_SUFFIX_CHARS = 16;

    public static int teamId = 0;

    @Data
    public class Line {

        private final int position;
        private PlaceholderValue<String> text;

        private Team team;

        public Line(int position, PlaceholderValue<String> text) {
            this.team = handle.registerNewTeam("" + (teamId++));
            this.position = position;
            this.setText(text);
        }

        public void setText(PlaceholderValue<String> text) {
            this.text = text;
            update();
        }

        public void setText(String text) {
            setText(PlaceholderValue.stringValue(text));
        }

        private void divide(String string, StringBuffer prefix, StringBuffer entry, StringBuffer suffix) {
            if (!string.isEmpty()) {
                int pre = Math.min(string.length(), MAX_PREFIX_CHARS);
                prefix.append(string.substring(0, pre));
                string = string.substring(pre, string.length());

                if (!string.isEmpty()) {
                    int mid = Math.min(string.length(), MAX_ENTRY_CHARS);
                    entry.append(string.substring(0, mid));
                    string = string.substring(mid, string.length());

                    if (!string.isEmpty()) {
                        int suf = Math.min(string.length(), MAX_SUFFIX_CHARS);
                        suffix.append(string.substring(0, suf));
                    }
                }
            }
        }

        // just an optimization
        private boolean update;
        private String prefix, entry, suffix;

        public String getFormatEntry(String entry) {
            while (entries.contains(entry))
                entry += RESET;
            if (entry.length() > MAX_ENTRY_CHARS)
                throw new IllegalArgumentException("Too much chars for entry \"" + entry + "\" at line: \"" + position + "\"");
            return entry;
        }

        public void printPosition() {
            if (entry != null)
                objective.getScore(entry).setScore(lines.length - position);
        }

        public void update() {
            update = true;
            print();
            update = false;
        }

        public void print() {
            player.setScoreboard(handle);
            // if the text is changed split it
            if (update) {
                String lastEntry = entry;
                if (text != null) {
                    String real = text.get(player);
                    StringBuffer
                            prefixBfr = new StringBuffer(),
                            entryBfr = new StringBuffer(),
                            suffixBfr = new StringBuffer();
                    divide(
                            real,
                            prefixBfr,
                            entryBfr,
                            suffixBfr
                    );
                    prefix = prefixBfr.toString();
                    entry = getFormatEntry(entryBfr.toString());
                    suffix = suffixBfr.toString();
                } else {
                    prefix = "";
                    entry = getFormatEntry("");
                    suffix = "";
                }
                update = false;
                // updates prefix, entry and suffix just if changed
                if (prefix != null) {
                    team.setPrefix(prefix);
                }
                if (entry != null) {
                    if (lastEntry != null) {
                        entries.remove(lastEntry);
                        handle.resetScores(lastEntry);
                    }
                    entries.add(entry);
                    team.addEntry(entry);
                    printPosition();
                }
                if (suffix != null) {
                    team.setSuffix(suffix);
                }
            }
        }
    }

    public ScoreboardView(Player player) {
        this.player = player;

        handle = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = handle.registerNewObjective("scoreboard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    // SCOREBOARD

    public void setTitle(String title) {
        setTitle(PlaceholderValue.stringValue(title));
    }

    public void setTitle(PlaceholderValue<String> title) {
        this.title = title;
        printTitle();
    }

    public int getNextFree() {
        for (int i = 0; i < lines.length; i++)
            if (lines[i] == null)
                return i;
        return -1;
    }

    public boolean addLine(String text) {
        return addLine(PlaceholderValue.stringValue(text));
    }

    public boolean addLine(PlaceholderValue<String> text) {
        int i = getNextFree();
        if (i > 0) {
            lines[i] = new Line(i, text);
            printLines();
            return true;
        }
        return false;
    }

    public void setLine(int index, String text) {
        setLine(index, PlaceholderValue.stringValue(text));
    }

    public void setLine(int index, PlaceholderValue<String> text) {
        Line line = getLine(index);
        if (line == null) lines[index] = new Line(index, text);
        else line.setText(text);
    }

    public Line getLine(int index) {
        return lines[index];
    }

    public void removeLine(int index) {
        lines[index] = null;
    }

    public void clear() {
        for (int i = 0; i < lines.length; i++)
            removeLine(i);
    }

    public void setScoreboard(Scoreboard scoreboard) {
        setTitle(scoreboard.getTitle());
        clear();
        for (int i = 0; i < scoreboard.getLines().length; i++)
            setLine(i, scoreboard.getLine(i));
        printLines();
        toString();
    }

    public Line[] getLines() {
        return lines;
    }

    // RENDER

    private String getRenderTitle(String title) {
        return title.substring(0, Math.min(title.length(), MAX_TITLE_CHARS));
    }

    public void printTitle() {
        player.setScoreboard(handle);
        objective.setDisplayName(getRenderTitle(title.get(player)));
    }

    public void printLines() {
        for (Line line : lines) {
            if (line != null)
                line.print();
        }
    }

    public void print() {
        printTitle();
        printLines();
    }
}
