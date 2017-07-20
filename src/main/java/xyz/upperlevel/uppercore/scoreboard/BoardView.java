package xyz.upperlevel.uppercore.scoreboard;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.RESET;
import static xyz.upperlevel.uppercore.scoreboard.BoardUtil.*;
import static xyz.upperlevel.uppercore.scoreboard.BoardUtil.divideLine;

@Data
public class BoardView {

    public static int teamId = 0;

    private final Player player;
    private final Line[] lines = new Line[MAX_LINES];

    private PlaceholderValue<String> title;

    private final org.bukkit.scoreboard.Scoreboard handle;
    private final Objective objective;
    private final Set<String> entries = new HashSet<>();

    public BoardView(Player player) {
        this.player = player;

        handle = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = handle.registerNewObjective("scoreboard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int position = 0; position < lines.length; position++)
            lines[position] = new Line(position);
    }

    @Data
    public class Line {

        private final int position;
        private final Team team;

        private PlaceholderValue<String> text;
        private String prefix, entry, suffix;

        public Line(int position) {
            this.team = handle.registerNewTeam("" + teamId++);
            this.position = position;
        }

        public Line(int position, PlaceholderValue<String> text) {
            this(position);
            this.setText(text);
        }

        public boolean isSet() {
            return text != null;
        }

        public void setText(PlaceholderValue<String> text) {
            this.text = text;
            BoardView.this.updateLines();
        }

        public void setText(String text) {
            setText(PlaceholderValue.stringValue(text));
        }

        public String getFormatEntry(String entry) {
            while (entries.contains(entry))
                entry += RESET;
            if (entry.length() > MAX_ENTRY_CHARS)
                throw new IllegalArgumentException("Too much chars for entry \"" + entry + "\" at line: \"" + position + "\"");
            return entry;
        }

        public void remove() {
            text = null;
            BoardView.this.updateLines();
        }

        public void forcePrint() {
            player.setScoreboard(handle);
            // if the text is changed split it
            String lastEntry = entry;
            if (text != null) {
                String real = text.resolve(player);
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
                entry = getFormatEntry(entryBfr.toString());
                suffix = suffixBfr.toString();
            } else {
                prefix = "";
                entry = getFormatEntry("");
                suffix = "";
            }
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
                objective.getScore(entry).setScore(lines.length - position);
            }
            if (suffix != null) {
                team.setSuffix(suffix);
            }
        }

        public void print() {
            BoardView.this.update();
        }
    }

    public void setTitle(String title) {
        setTitle(PlaceholderValue.stringValue(title));
    }

    public void setTitle(PlaceholderValue<String> title) {
        this.title = title;
        updateTitle();
    }

    public int getNextFree() {
        for (int i = 0; i < lines.length; i++)
            if (lines[i] == null)
                return i;
        return -1;
    }

    public Line getLine(int index) {
        return lines[index];
    }

    public boolean addLine(String text) {
        return addLine(PlaceholderValue.stringValue(text));
    }

    public boolean addLine(PlaceholderValue<String> text) {
        int i = getNextFree();
        if (i > 0) {
            lines[i] = new Line(i, text);
            updateLines();
            return true;
        }
        return false;
    }

    public void setLine(int index, String text) {
        setLine(index, PlaceholderValue.stringValue(text));
    }

    public void setLine(int index, PlaceholderValue<String> text) {
        getLine(index).setText(text);
    }

    public void removeLine(int index) {
        lines[index].remove();
    }

    public void clear() {
        for (int position = 0; position < lines.length; position++)
            removeLine(position);
    }

    public void setBoard(Board board) {
        setTitle(board.getTitle());
        clear();
        for (int position = 0; position < board.getLines().length; position++)
            setLine(position, board.getLine(position));
        updateLines();
    }

    // RENDER

    private String getRenderTitle(String title) {
        return title.substring(0, Math.min(title.length(), MAX_TITLE_CHARS));
    }

    public void update() {
        updateTitle();
        updateLines();
    }

    public void updateTitle() {
        player.setScoreboard(handle);
        objective.setDisplayName(getRenderTitle(title.resolve(player)));
    }

    public void updateLines() {
        int last = 0;
        for (int current = 0; current < lines.length; current++) {
            if (lines[current].isSet())
                for (; last <= current; last++)
                    lines[last].forcePrint();
        }
    }
}
