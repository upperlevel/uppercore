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
        for (; pos < lines.size() && pos < MAX_LINES; pos++) {
            this.lines[pos].update(lines.get(pos), lines.size() - pos);
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

        // The entry cannot be equal to other entries
        private String differEntry(String entry, int position) {
            while (entries.contains(entry)) {
                entry += ChatColor.RESET;
            }
            if (entry.length() > MAX_ENTRY_CHARS) {
                throw new IllegalArgumentException("Too much chars for registrable \"" + entry + "\" at: \"" + position + "\"");
            }
            return entry;
        }

        private void split(String line, StringBuffer prefix, StringBuffer entry, StringBuffer suffix) {
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

        public void update(String line, int position) {
            if (line != null) {
                StringBuffer
                        prefixBfr = new StringBuffer(),
                        entryBfr = new StringBuffer(),
                        suffixBfr = new StringBuffer();
                split(line, prefixBfr, entryBfr, suffixBfr);
                // Entry
                if (entry != null) {
                    clear(); // Remove previous entry
                }
                this.entry = differEntry(entryBfr.toString(), position);
                entries.add(entry);
                team.addEntry(entry);
                objective.getScore(entry).setScore(position);

                // Prefix
                prefix = prefixBfr.toString();
                team.setPrefix(prefix);

                // Suffix
                suffix = suffixBfr.toString();
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
