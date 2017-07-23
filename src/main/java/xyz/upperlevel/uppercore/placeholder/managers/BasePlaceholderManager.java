package xyz.upperlevel.uppercore.placeholder.managers;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderManager;
import xyz.upperlevel.uppercore.placeholder.PlaceholderSession;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BasePlaceholderManager implements PlaceholderManager {
    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

    public boolean hasPlaceholders(String string) {
        return PLACEHOLDER_PATTERN.matcher(string).find();
    }

    @Override
    public String apply(Player player, String text) {
        return apply(player, text, this::find);
    }

    @Override
    public String apply(Player player, String text, Map<String, Placeholder> local) {
        return apply(player, text, finder(local));
    }

    @Override
    public String apply(Player player, String text, PlaceholderSession local) {
        return apply(player, text, local.getPlaceholders());
    }

    @Override
    public String single(Player player, String string) {
        return exec(player, string, this::find);
    }

    @Override
    public String single(Player player, String string, Map<String, Placeholder> local) {
        return exec(player, string, finder(local));
    }

    @Override
    public String single(Player player, String text, PlaceholderSession local) {
        return single(player, text, local.getPlaceholders());
    }


    public String apply(Player player, String text, Function<String, Placeholder> finder) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer res = new StringBuffer();

        while(matcher.find()) {
            String str = matcher.group(1);
            String replacement = exec(player, str, finder);
            if(replacement != null)
                matcher.appendReplacement(res, replacement);
        }
        matcher.appendTail(res);

        return res.toString();
    }

    protected abstract Placeholder find(String id);

    protected Function<String, Placeholder> finder(Map<String, Placeholder> local) {
        return id -> {
            Placeholder p = local.get(id);
            return p == null ? find(id) : p;
        };
    }


    public static String exec(Player player, String text, Function<String, Placeholder> finder) {
        int index = text.indexOf('_');
        Placeholder found;

        while (index >= 0) {
            String id = text.substring(0, index);
            String arg = text.substring(index + 1);
            found = finder.apply(id);
            if(found != null)
                return found.resolve(player, arg);
            index = text.indexOf('_', index);
        }
        found = finder.apply(text);
        return found.resolve(player, "");
    }
}
