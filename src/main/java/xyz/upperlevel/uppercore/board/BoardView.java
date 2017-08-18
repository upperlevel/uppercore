package xyz.upperlevel.uppercore.board;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xyz.upperlevel.uppercore.task.UpdaterTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class BoardView {
    public static final int MAX_LINES = 15;
    public static final int MAX_TITLE_CHARS = 32;
    public static final int MAX_PREFIX_CHARS = 16;
    public static final int MAX_ENTRY_CHARS = 40; // >= 1.8
    public static final int MAX_SUFFIX_CHARS = 16;

    private final Player player;
    private final Scoreboard handle;
    private final Objective objective;

    // BOARD
    private Board board;
    private final Line[] lines = new Line[MAX_LINES];
    private final Set<String> entries = new HashSet<>();
    private final UpdaterTask updater = new UpdaterTask(this::render);

    public BoardView(Player player) {
        this.player = player;
        this.handle = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = handle.registerNewObjective("scoreboard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (int pos = 0; pos < lines.length; pos++)
            lines[pos] = new Line(pos);
    }

    private void open() {
        if (board != null) {
            player.setScoreboard(handle);
        } else
            close();
    }

    private void close() {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void setBoard(Board board) {
        if (updater.isStarted())
            updater.stop();
        this.board = board;
        render();
        if (board != null && board.getUpdateInterval() > 0) {
            updater.setInterval(board.getUpdateInterval());
            if (!updater.isStarted())
                updater.start(false);
        }
    }

    public void render() {
        if (board != null) {
            // scoreboard
            objective.setDisplayName(board.getTitle().resolve(player));
            List<String> lines = board.render(player);
            int pos = 0;
            for (; pos < lines.size() && pos < MAX_LINES; pos++)
                this.lines[pos].render(lines.get(pos), lines.size() - pos);
            for (; pos < this.lines.length; pos++)
                this.lines[pos].clear();
            open();
        } else
            close();
    }

    public void clear() {
        setBoard(null);
    }

    // LINE
    private class Line {
        private Team team;
        private String prefix, entry, suffix;

        public Line(int position) {
            this.team = handle.registerNewTeam("line_" + position);
        }

        private String format(String entry, int position) {
            while (entries.contains(entry))
                entry += ChatColor.RESET;
            if (entry.length() > MAX_ENTRY_CHARS)
                throw new IllegalArgumentException("Too much chars for registrable \"" + entry + "\" at: \"" + position + "\"");
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

        public void render(String line, int position) {
            if (line != null) {
                StringBuffer
                        pBfr = new StringBuffer(),
                        eBfr = new StringBuffer(),
                        sBfr = new StringBuffer();
                split(line, pBfr, eBfr, sBfr);
                // ENTRY
                if (entry != null) {
                    clear(); // remove entry line
                }
                this.entry = format(eBfr.toString(), position);
                entries.add(entry);
                team.addEntry(entry);
                objective.getScore(entry).setScore(position);
                // PREFIX
                prefix = pBfr.toString();
                team.setPrefix(prefix);
                // SUFFIX
                suffix = sBfr.toString();
                team.setSuffix(suffix);
                this.prefix = pBfr.toString();
                this.suffix = sBfr.toString();
            } else {
                clear();
            }
        }

        public void clear() {
            if (entry != null) {
                entries.remove(entry);
                handle.resetScores(entry);
                entry = null;
            }
            prefix = null;
            suffix = null;
        }
    }
}
