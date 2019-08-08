package xyz.upperlevel.uppercore.board;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

public class Board {
    public static final int MAX_SCOREBOARD_LINES = 15;
    public static final int MAX_SCOREBOARD_TITLE_CHARACTERS = 32;

    public static final int MAX_TEAM_PREFIX_CHARACTERS = 64;
    public static final int MAX_TEAM_ENTRY_CHARACTERS = 40;
    public static final int MAX_TEAM_SUFFIX_CHARACTERS = 64;

    @Getter
    private PlaceholderValue<String> title;
    private List<PlaceholderValue<String>> lines = new ArrayList<>();

    @Getter
    private final Scoreboard scoreboard;
    private Objective objective;

    private final Set<String> entries = new HashSet<>();

    public Board() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public void setTitle(String title) {
        if (title.length() > MAX_SCOREBOARD_TITLE_CHARACTERS) {
            throw new IllegalArgumentException("Title too long, maximum characters accepted are: " + MAX_SCOREBOARD_TITLE_CHARACTERS);
        }
        setTitle(PlaceholderValue.fake(title));
    }

    public void setTitle(PlaceholderValue<String> title) {
        this.title = title;
    }

    public List<PlaceholderValue<String>> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public void addLine(String line) {
        addLine(PlaceholderValue.fake(line));
    }

    public void addLine(PlaceholderValue<String> line) {
        if (lines.size() >= MAX_SCOREBOARD_LINES) {
            throw new IllegalStateException("Scoreboard has already reached its maximum lines: " + MAX_SCOREBOARD_LINES);
        }
        lines.add(line);
    }

    public void setLines(Collection<String> lines) {
        setLines(lines.stream().map(PlaceholderValue::fake).collect(Collectors.toList()));
    }

    public void setLines(List<PlaceholderValue<String>> lines) {
        if (lines.size() > MAX_SCOREBOARD_LINES) {
            throw new IllegalArgumentException("Too many lines, the maximum accepted is: " + MAX_SCOREBOARD_LINES);
        }
        this.lines = lines;
    }

    private void clearEntries() {
        for (String entry : entries) {
            scoreboard.resetScores(entry);
        }
        entries.clear();
    }

    public void clearLines() {
        lines.clear();
        clearEntries();
    }

    public void set(BoardModel model, Player reference) {
        model.apply(this, reference);
    }

    private Team getTeam(int position) {
        String id = "line." + position;
        Team team = scoreboard.getTeam(id);
        if (team == null) {
            team = scoreboard.registerNewTeam(id);
        }
        return team;
    }

    private void updateTitle(Player player, PlaceholderRegistry placeholders) {
        if (objective == null) {
            objective = scoreboard.registerNewObjective("scoreboard", "dummy", title.resolve(player, placeholders));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            objective.setDisplayName(title.resolve(player, placeholders));
        }
    }

    public static int generateEntry(Set<String> entries, StringBuilder entry, String padding) {
        int size = entry.length();

        // Forward
        while (entries.contains(entry.toString())) {
            entry.append(padding);
            if (entry.length() >= MAX_TEAM_ENTRY_CHARACTERS) {
                if (entry.length() > MAX_TEAM_ENTRY_CHARACTERS) {
                    entry.delete(MAX_TEAM_ENTRY_CHARACTERS + 1, entry.length());
                }
                break;
            }
        }

        // Backward
        while (entries.contains(entry.toString())) {
            int from = size - padding.length();
            if (from < 0) {
                throw new IllegalStateException("Invalid board line, can't avoid repetition.");
            }
            entry.replace(from, size, padding);
            size -= padding.length();
        }

        return size;
    }

    private void updateLine(int position, Player player, PlaceholderRegistry placeholders) {
        String line = lines.get(position).resolve(player, placeholders);
        Team team = getTeam(position);

        int startAt, endAt;
        String lastColor;

        // ================================================================================
        // Prefix
        // ================================================================================

        startAt = 0;
        endAt = Math.min(line.length(), startAt + MAX_TEAM_PREFIX_CHARACTERS);

        String prefix = line.substring(startAt, endAt);
        team.setPrefix(prefix);
        lastColor = ChatColor.getLastColors(prefix);

        // ================================================================================
        // Entry
        // ================================================================================

        startAt = endAt;

        // The entry must take care of the prefix color.
        endAt = Math.min(line.length(), startAt + MAX_TEAM_ENTRY_CHARACTERS - lastColor.length());

        // Generates the entry, with the lastColor + entry.
        StringBuilder digest = new StringBuilder(lastColor + line.substring(startAt, endAt));
        int size = generateEntry(entries, digest, ChatColor.RESET.toString());
        String entry = digest.toString();

        // The starting index remains the same, the ending index is minus the lastColor length.
        // startAt = startAt;
        endAt = startAt + Math.max(size - lastColor.length(), 0);

        // The entry lastColor is influenced by previous lastColor.
        lastColor = ChatColor.getLastColors(entry.substring(0, size));

        entries.add(entry);
        team.addEntry(entry);
        objective.getScore(entry).setScore(MAX_SCOREBOARD_LINES - position);

        // ================================================================================
        // Suffix
        // ================================================================================

        startAt = endAt;
        endAt = Math.min(line.length(), startAt + MAX_TEAM_SUFFIX_CHARACTERS - lastColor.length());
        String suffix = lastColor + line.substring(startAt, endAt);

        // The suffix was cut since the line was too long!
        if (endAt < line.length()) {
            String cross = ChatColor.RED + "X";
            suffix = new StringBuilder(suffix)
                    .replace(suffix.length() - cross.length(), suffix.length(), cross)
                    .toString();
        }

        team.setSuffix(suffix);
    }

    private void updateLines(Player player, PlaceholderRegistry placeholders) {
        clearEntries();
        for (int position = 0; position < lines.size(); position++) {
            updateLine(position, player, placeholders);
        }
    }

    public void update(Player player, PlaceholderRegistry placeholders) {
        updateTitle(player, placeholders);
        updateLines(player, placeholders);
    }

    public void update(Player player) {
        update(player, PlaceholderRegistry.def());
    }

    public void open(Player player, PlaceholderRegistry placeholders) {
        update(player, placeholders);
        player.setScoreboard(scoreboard);
    }

    public void open(Player player) {
        open(player, PlaceholderRegistry.def());
    }

    public boolean isOpened(Player player) {
        return player.getScoreboard() == scoreboard;
    }
}
