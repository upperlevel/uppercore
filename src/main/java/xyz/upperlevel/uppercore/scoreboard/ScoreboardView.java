package xyz.upperlevel.uppercore.scoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.RESET;

public class ScoreboardView {

    @Getter
    private final Player player;

    private static final int MAX_LINE_CHARS = 40;
    private static final int MAX_LINES = 15;
    private static final int MAX_TITLE_CHARS = 32;

    private org.bukkit.scoreboard.Scoreboard handle;
    private Objective objective;
    private final List<String> coded = new ArrayList<>();

    @Getter
    private PlaceholderValue<String> title;
    private final List<PlaceholderValue<String>> lines = new ArrayList<>();

    public ScoreboardView(Player player, Scoreboard scoreboard) {
        this.player = player;

        handle = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = handle.registerNewObjective(player.getUniqueId().toString(), "dummy");
        objective.setDisplayName(scoreboard.getTitle().get(player));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(handle);
    }

    // SCOREBOARD

    public void setTitle(String title) {
        setTitle(PlaceholderValue.stringValue(title));
    }

    public void setTitle(PlaceholderValue<String> title) {
        this.title = title;
        printTitle();
    }

    public void addLine(String line) {
        addLine(PlaceholderValue.stringValue(line));
    }

    public void addLine(PlaceholderValue<String> line) {
        lines.add(line);
        printLine(lines.size() - 1);
    }

    public void setLine(int index, String line) {
        setLine(index, PlaceholderValue.stringValue(line));
    }

    public void setLine(int index, PlaceholderValue<String> line) {
        lines.set(index, line);
        printLine(index);
    }

    public PlaceholderValue<String> getLine(int index) {
        return lines.get(index);
    }

    public void removeLine(int index) {
        lines.remove(index);
        wipeLine(index);
    }

    public void clear() {
        lines.clear();
        wipeLines();
    }

    public void setScoreboard(Scoreboard scoreboard) {
        setTitle(scoreboard.getTitle());
        clear();
        for (int i = 0; i < scoreboard.getLines().size(); i++)
            setLine(i, scoreboard.getLine(i));
    }

    // RENDER

    private String getRenderTitle(String title) {
        return title.substring(0, Math.min(title.length(), MAX_TITLE_CHARS));
    }

    public void printTitle() {
        objective.setDisplayName(getRenderTitle(title.get(player)));
    }

    private String getRenderLine(String line) {
        line.substring(0, Math.min(line.length(), MAX_LINE_CHARS));
        while (coded.contains(line))
            line += RESET;
        if (line.length() > MAX_LINE_CHARS)
            throw new IllegalStateException("Line cannot be added!");
        return line;
    }

    public void printLine(int index) {
        handle.resetScores(coded.get(index));

        String real = getRenderLine(lines.get(index).get(player));
        objective.getScore(real).setScore(index);
        this.coded.set(index, real);
    }

    public void wipeLine(int index) {
        handle.resetScores(coded.get(index));
        coded.remove(index);
    }

    public void printLines() {
        for (int i = 0; i < lines.size(); i++)
            printLine(i);
    }

    public void wipeLines() {
        for (int i = 0; i < lines.size(); i++)
            handle.resetScores(coded.get(i));
        coded.clear();
    }

    public void print() {
        printTitle();
        printLines();
    }
}
