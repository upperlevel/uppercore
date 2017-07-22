package xyz.upperlevel.uppercore.placeholder;

import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.config.action.actions.PlaySoundAction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Data
public class PlaceholderSession {

    private final Map<String, Placeholder> placeholders = new HashMap<>();

    private PlaceholderSession add(Placeholder placeholder) {
        this.placeholders.put(placeholder.getId(), placeholder);
        return this;
    }

    public PlaceholderSession add(String id, Object obj) {
        this.placeholders.put(id, Placeholder.constant(String.valueOf(obj)));
        return this;
    }

    public PlaceholderSession add(String id, Function<Player, String> obj) {
        this.placeholders.put(id, Placeholder.of(obj));
        return this;
    }

    public PlaceholderSession add(String id, BiFunction<Player, String, String> obj) {
        this.placeholders.put(id, Placeholder.of(obj));
        return this;
    }

    public PlaceholderSession set(String id, Object value) {
        placeholders.put(id, Placeholder.constant(String.valueOf(value)));
        return this;
    }
}
