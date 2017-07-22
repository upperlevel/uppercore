package xyz.upperlevel.uppercore.placeholder.managers;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderManager;
import xyz.upperlevel.uppercore.placeholder.PlaceholderSession;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BasePlaceholderManager implements PlaceholderManager {
    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

    protected boolean isPlaceholder(String text, Predicate<String> hasId) {
        int index = text.indexOf('_');

        while (index >= 0) {
            String id = text.substring(0, index);
            if(hasId.test(id))
                return true;
            index = text.indexOf('_', index + 1);
        }
        return hasId.test(text);
    }

    public boolean hasPlaceholders(String string, Predicate<String> hasId) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(string);
        while (matcher.find())
            if (isPlaceholder(matcher.group(1).toLowerCase(), hasId))
                return true;
            else
                throw new InvalidConfigurationException("Cannot find placeholder '" + matcher.group(1) + "'");
        return false;
    }

    @Override
    public boolean hasPlaceholders(String string, Map<String, Placeholder> local) {
        return hasPlaceholders(string, id -> local.containsKey(id) || has(id));
    }

    @Override
    public boolean hasPlaceholders(String string, Set<String> localRaw) {
        return hasPlaceholders(string, id -> localRaw.contains(id) || has(id));
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

    protected boolean has(String id) {
        return find(id) != null;
    }

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
