package xyz.upperlevel.uppercore.placeholder.managers;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderManager;

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
            index = text.indexOf('_', index);
        }
        return hasId.test(text);
    }

    public boolean hasPlaceholders(String string, Predicate<String> hasId) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(string);
        while (matcher.find())
            if (isPlaceholder(matcher.group(1).toLowerCase(), hasId))
                return true;
            else
                System.out.println("Cannot find " + matcher.group(1));
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
        return apply(player, text, replacer());
    }

    @Override
    public String apply(Player player, String text, Map<String, Placeholder> local) {
        return apply(player, text, replacer(local));
    }

    @Override
    public String applyRaw(Player player, String text, Map<String, String> local) {
        return apply(player, text, replacerRaw(local));
    }

    @Override
    public String single(Player player, String string) {
        return exec(player, string, replacer());
    }

    @Override
    public String single(Player player, String string, Map<String, Placeholder> local) {
        return exec(player, string, replacer(local));
    }

    @Override
    public String singleRaw(Player player, String text, Map<String, String> local) {
        return exec(player, text, replacerRaw(local));
    }


    public String apply(Player player, String text, Replacer replacer) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer res = new StringBuffer();

        while(matcher.find()) {
            String str = matcher.group(1);
            String replacement = exec(player, str, replacer);
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


    public static String exec(Player player, String text, Replacer replacer) {
        int index = text.indexOf('_');
        String found;

        while (index >= 0) {
            String id = text.substring(0, index);
            String arg = text.substring(index + 1);
            found = replacer.replace(player, id, arg);
            if(found != null)
                return found;
            index = text.indexOf('_', index);
        }
        found = replacer.replace(player, text, "");
        return found;
    }

    protected Replacer replacer(Function<String, Placeholder> finder) {
        return (player, id, arg) -> {
            Placeholder p = finder.apply(id);
            return  p == null ? null : p.resolve(player, arg);
        };
    }

    /**
     * This is a replacer that uses both the raw local and the placeholders <br>
     * It first searches the Placeholder in the local values and, if it isn't found it falls back to the default Placehodler
     * @param local the local raw values
     * @return a replacer that searches in both local and the default placeholders
     */
    protected Replacer replacerRaw(Map<String, String> local) {
        return (player, id, arg) -> {
            String res = local.get(id);
            if(res != null)
                return res;
            Placeholder p = find(id);
            return p == null ? null : p.resolve(player, arg);
        };
    }

    protected Replacer replacer(Map<String, Placeholder> local) {
        return replacer(finder(local));
    }

    protected Replacer replacer() {
        return replacer(this::find);
    }

    interface Replacer {
        String replace(Player player, String id, String arg);
    }
}
