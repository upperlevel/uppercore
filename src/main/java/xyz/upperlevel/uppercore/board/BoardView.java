package xyz.upperlevel.uppercore.board;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class manages the View of a {@link Board} for a single player.
 * It displays the lines while computing which part goes in the prefix,
 * entry or suffix and it resolves any entry duplication problem and,
 * more in general, any problem related with the line-rendering work.
 */
// TODO Optimized empty line initialization
public abstract class BoardView {
    public static final int MAX_LINES = 15;
    public static final int MAX_TITLE_CHARS = 32;
    public static final int MAX_PREFIX_CHARS = 16;
    public static final int MAX_ENTRY_CHARS = 40; // >= 1.8
    public static final int MAX_SUFFIX_CHARS = 16;

    @Getter
    private final Player holder;

    private Scoreboard scoreboard;
    private Objective objective;
    private final Line[] lines = new Line[MAX_LINES];
    private Set<String> entries = new HashSet<>();

    private void createScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("scoreboard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int position = 0; position < MAX_LINES; position++) {
            lines[position] = new Line(position);
        }
    }

    public BoardView(Player holder) {
        this.holder = holder;

        createScoreboard();
        update();

        // Firstly open the board
        ensureOpened();
    }

    public abstract String getTitle();

    public abstract List<String> getLines();

    public void ensureOpened() {
        holder.setScoreboard(scoreboard);
    }

    /**
     * Update just the board title.
     */
    public void updateTitle() {
        objective.setDisplayName(getTitle());
    }

    /**
     * Update just the board lines.
     */
    public void updateLines() {
        List<String> lines = getLines();
        int pos = 0;
        int lineCount = lines.size();
        for (; pos < lineCount && pos < MAX_LINES; pos++) {
            this.lines[pos].update(lines.get(pos), lineCount - pos);
        }
        for (; pos < MAX_LINES; pos++) {
            this.lines[pos].clear();
        }
    }

    /**
     * Updates both title and lines.
     */
    public void update() {
        updateTitle();
        updateLines();
    }

    private class Line {
        private Team team;
        private String prefix, entry, suffix;

        public Line(int position) {
            team = scoreboard.registerNewTeam("line#" + position);
        }

        /**
         * Makes sure that the entry is not used already and, if it's the case, returns a changed string that is unique
         * in the board.
         * @param entry the entry to be checked
         * @param position the position of the entry to fill
         * @return an string that is unique in the board
         */
        private String differEntry(String entry, int position) {
            // The entry cannot be equal to other entries
            while (entries.contains(entry)) {
                entry += ChatColor.RESET;
            }
            if (entry.length() > MAX_ENTRY_CHARS) {
                throw new IllegalArgumentException("Too much chars for registrable \"" + entry + "\" at: \"" + position + "\"");
            }
            return entry;
        }

        /**
         * Splits the input line into prefix, entry and suffix, trying to fill every level.
         * @param line the line to be split
         * @return an array with tre elements with the prefix, entry, suffix order
         */
        private String[] split(String line) {
            String[] res = {"", "", ""};
            if (line.isEmpty()) return res;
            // Prefix
            int pre = Math.min(line.length(), MAX_PREFIX_CHARS);
            res[0] = line.substring(0, pre);
            line = line.substring(pre, line.length());

            if (line.isEmpty()) return res;
            // Entry
            int mid = Math.min(line.length(), MAX_ENTRY_CHARS);
            res[1] = line.substring(0, mid);
            line = line.substring(mid, line.length());

            if (line.isEmpty()) return res;
            // Suffix
            int suf = Math.min(line.length(), MAX_SUFFIX_CHARS);
            res[2] = line.substring(0, suf);

            return res;
        }

        public void update(String line, int position) {
            if (line != null) {
                String[] lineSplit = split(line);
                // Entry
                if (entry != null) {
                    clear(); // Remove previous entry
                }
                this.entry = differEntry(lineSplit[1], position);
                entries.add(entry);
                team.addEntry(entry);
                objective.getScore(entry).setScore(position);

                // Prefix
                prefix = lineSplit[0];
                team.setPrefix(prefix);

                // Suffix
                suffix = lineSplit[2];
                team.setSuffix(suffix);
            } else {
                clear();
            }
        }

        public void clear() {
            if (entry != null) {
                entries.remove(entry);
                scoreboard.resetScores(entry);
                entry = null;
            }
            prefix = null;
            suffix = null;
        }
    }
}
