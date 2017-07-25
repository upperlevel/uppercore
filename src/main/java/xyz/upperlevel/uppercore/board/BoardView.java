package xyz.upperlevel.uppercore.board;

import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import xyz.upperlevel.uppercore.placeholder.PlaceholderSession;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.RESET;
import static xyz.upperlevel.uppercore.board.BoardUtil.*;
import static xyz.upperlevel.uppercore.board.BoardUtil.divideLine;

@Data
public class BoardView {

    public static int teamId = 0;

    private final Player player;

    private final Title title = new Title();
    private final Line[] lines = new Line[MAX_LINES];
    private Board board;

    private final org.bukkit.scoreboard.Scoreboard handle;
    private final Objective objective;
    private final Set<String> entries = new HashSet<>();

    public BoardView(Player player) {
        this.player = player;
        for (int i = 0; i < lines.length; i++)
            lines[i] = new Line(i);

        handle = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = handle.registerNewObjective("board", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void open() {
        player.setScoreboard(handle);
    }

    public void close() {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public Line getLine(int position) {
        return lines[position];
    }

    public void setBoard(Board board) {
        this.board = board;
        if (board != null) {
            getTitle().set(board.getTitle());
            for (int position = 0; position < board.getLines().length; position++)
                lines[position].set(board.getLine(position));
        } else {

        }
    }

    public void clear() {

    }

    public void display() {
        int last = 0;
        for (int current = 0; current < lines.length; current++) {
            if (!lines[current].isEmpty())
                for (; last <= current; last++)
                    lines[last].display();
        }
    }

    // TITLE
    public class Title {
        private Board.Title title;

        @Getter
        private final PlaceholderSession placeholders = new PlaceholderSession();

        public boolean isEmpty() {
            return title == null || title.isEmpty();
        }

        public Board.Title get() {
            return title;
        }

        public void set(Board.Title title) {
            // todo stop old update task
            this.title = title;
            // todo start update task
            display();
        }

        public void display() {
            if (isEmpty()) {
                close();
                return;
            }
            objective.setDisplayName(title.getText().resolve(player, placeholders));
            open();
        }
    }

    // LINE
    public class Line {
        @Getter
        private final int position;
        private Board.Line line;

        @Getter
        private final PlaceholderSession placeholders = new PlaceholderSession();

        private final Team team;
        private String prefix, entry, suffix;

        public Line(int position) {
            this.position = position;
            this.team = handle.registerNewTeam("" + teamId++);
        }

        public boolean isEmpty() {
            return line == null || line.isEmpty();
        }

        public Board.Line get() {
            return line;
        }

        public void set(Board.Line line) {
            // todo stop old task
            this.line = line;
            if (line == null && entry != null) {
                entries.remove(entry);
                handle.resetScores(entry);
                prefix = null;
                entry = null;
                suffix = null;
            }
            display();
            // todo start task
        }

        public String format(String entry) {
            while (entries.contains(entry))
                entry += RESET;
            if (entry.length() > MAX_ENTRY_CHARS)
                throw new IllegalArgumentException("Too much chars for entry \"" + entry + "\" at line: \"" + position + "\"");
            return entry;
        }

        public void display() {
            // if the text is changed split it
            String lastEntry = entry;
            if (line != null && !line.isEmpty()) {
                String real = line.getText().resolve(player, placeholders); // TODO mix placeholders
                StringBuffer
                        prefixBfr = new StringBuffer(),
                        entryBfr = new StringBuffer(),
                        suffixBfr = new StringBuffer();
                divideLine(
                        real,
                        prefixBfr,
                        entryBfr,
                        suffixBfr
                );
                prefix = prefixBfr.toString();
                entry = format(entryBfr.toString());
                suffix = suffixBfr.toString();
            } else {
                prefix = "";
                entry = format("");
                suffix = "";
            }
            // updates prefix, entry and suffix just if changed
            if (prefix != null)
                team.setPrefix(prefix);
            if (entry != null) {
                if (lastEntry != null) {
                    entries.remove(lastEntry);
                    handle.resetScores(lastEntry);
                }
                entries.add(entry);
                team.addEntry(entry);
                objective.getScore(entry).setScore(lines.length - position);
            }
            if (suffix != null)
                team.setSuffix(suffix);
        }
    }
}
