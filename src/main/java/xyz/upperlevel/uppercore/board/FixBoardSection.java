package xyz.upperlevel.uppercore.board;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FixBoardSection implements BoardSection {
    private List<PlaceholderValue<String>> lines = new ArrayList<>();

    public FixBoardSection() {}

    /**
     * Inits the board section with default lines.
     */
    public FixBoardSection(List<PlaceholderValue<String>> lines) {
        this.lines = lines;
    }

    /**
     * Adds a new line to the section.
     */
    public void add(String line) {
        add(PlaceholderValue.stringValue(line));
    }

    /**
     * Adds a new line to the section.
     */
    public void add(PlaceholderValue<String> line) {
        lines.add(line);
    }

    @Override
    public List<String> render(Player player, PlaceholderRegistry placeholders) {
        return lines.stream()
                .map(line -> line.resolve(player, placeholders))
                .collect(Collectors.toList());
    }

    public static FixBoardSection.Builder builder() {
        return new Builder(new FixBoardSection());
    }

    @RequiredArgsConstructor
    public static class Builder {
        private final FixBoardSection handle;

        public Builder add(String line) {
            handle.add(line);
            return this;
        }

        public Builder add(PlaceholderValue<String> line) {
            handle.add(line);
            return this;
        }

        public FixBoardSection build() {
            return handle;
        }
    }
}
