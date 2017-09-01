package xyz.upperlevel.uppercore.placeholder.managers;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderManager;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

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
    public String apply(Player player, String text, PlaceholderRegistry local) {
        return apply(player, text, local::get);
    }

    @Override
    public String single(Player player, String string) {
        return exec(player, string, this::find);
    }

    @Override
    public String single(Player player, String text, PlaceholderRegistry local) {
        return exec(player, text, this::find);
    }


    public String apply(Player player, String text, Function<String, Placeholder> finder) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer res = new StringBuffer();

        while(matcher.find()) {
            String str = matcher.group(1);
            String replacement = exec(player, str, finder);
            if(replacement != null)
                matcher.appendReplacement(res, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(res);

        return res.toString();
    }

    protected abstract Placeholder find(String id);


    public static String exec(Player player, String text, Function<String, Placeholder> finder) {
        Placeholder found = finder.apply(text);

        if(found != null)
            return found.resolve(player, "");

        int index = text.lastIndexOf('_');

        while (index >= 0) {
            String id = text.substring(0, index);
            String arg = text.substring(index + 1);
            found = finder.apply(id);
            if(found != null) {
                try {
                    return found.resolve(player, arg);
                } catch (Exception e) {
                    return null;
                }
            }
            index = text.lastIndexOf('_', index - 1);
        }
        return null;
    }
}
