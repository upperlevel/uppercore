package xyz.upperlevel.uppercore.board;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO Optimized empty line initialization
public class BoardViewer {
    public static final int MAX_LINES = 15;
    public static final int MAX_TITLE_CHARS = 32;
    public static final int MAX_PREFIX_CHARS = 16;
    public static final int MAX_ENTRY_CHARS = 40; // >= 1.8
    public static final int MAX_SUFFIX_CHARS = 16;

    @Getter
    private final Player player;

    @Getter
    private Board board; // can be null

    @Getter
    @Setter
    private PlaceholderRegistry placeholderRegistry;

    private Scoreboard scoreboard;
    private Objective objective;
    private final Line[] lines = new Line[MAX_LINES];
    private Set<String> entries = new HashSet<>();

    private BukkitRunnable updater;

    private BoardViewer(Player player) {
        this.player = player;

        createScoreboard();
    }

    private void createScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("scoreboard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int position = 0; position < MAX_LINES; position++) {
            lines[position] = new Line(position);
        }
    }

    public void setBoard(Board board, PlaceholderRegistry placeholderRegistry) {
        this.placeholderRegistry = placeholderRegistry;
        if (updater != null) {
            updater.cancel();
            updater = null;
        }
        if (board == null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        } else {
            update(placeholderRegistry);
            player.setScoreboard(scoreboard);
            updater = new BukkitRunnable() {
                @Override
                public void run() {
                    update(BoardViewer.this.placeholderRegistry);
                }
            };
            updater.runTaskTimer(Uppercore.plugin(), 0, board.getAutoUpdateInterval());
        }
        this.board = board;
    }

    public void update(PlaceholderRegistry placeholderRegistry) {
        if (board != null) {
            objective.setDisplayName(board.getTitle(player, placeholderRegistry));
        }
        if (board != null) {
            List<String> lines = board.getLines(player, placeholderRegistry);
            int pos = 0;
            for (; pos < lines.size() && pos < MAX_LINES; pos++) {
                this.lines[pos].update(lines.get(pos), lines.size() - pos);
            }
            for (; pos < MAX_LINES; pos++) {
                this.lines[pos].clear();
            }
        }
    }

    public void close() {
        setBoard(null, null);
    }

    static BoardViewer create(Player player) {
        return new BoardViewer(player);
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
                prefix.append(line, 0, pre);
                line = line.substring(pre);

                if (!line.isEmpty()) {
                    int mid = Math.min(line.length(), MAX_ENTRY_CHARS);
                    entry.append(line, 0, mid);
                    line = line.substring(mid);

                    if (!line.isEmpty()) {
                        int suf = Math.min(line.length(), MAX_SUFFIX_CHARS);
                        suffix.append(line, 0, suf);
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
