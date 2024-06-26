package xyz.upperlevel.uppercore.placeholder.managers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.manager.LocalExpansionManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PlaceholderManager that hooks into PlaceholderAPI (Papi) and works with it.
 */
public class PAPIPlaceholderManager extends BasePlaceholderManager {
    @Getter
    private final PapiPlaceholderRegistry registry = new PapiPlaceholderRegistry(this);
    private Map<String, Placeholder> placeholders = new HashMap<>();

    @Getter
    private final LocalExpansionManager expansionManager = PlaceholderAPIPlugin.getInstance().getLocalExpansionManager();

    public PAPIPlaceholderManager() {
        if (!new OfficialPlaceholderAdapter().register()) {
            Uppercore.logger().severe("Could not register PAPI adapter");
        }
    }

    @Override
    public void register(Plugin plugin, Placeholder placeholder) {
        placeholders.put(placeholder.getId(), placeholder);
    }

    public Map<String, PlaceholderHook> getPlaceholders() {
        return expansionManager
                .getExpansions()
                .stream()
                .collect(Collectors.toMap(PlaceholderExpansion::getIdentifier, (ex) -> ex));
    }

    public Placeholder find(String id) {
        PlaceholderHook hook = getPlaceholders().get(id);
        return hook == null ? null : new Placeholder() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String resolve(Player player, String arg) {
                return hook.onPlaceholderRequest(player, arg);
            }
        };
    }


    private class OfficialPlaceholderAdapter extends PlaceholderExpansion {

        @Override
        public String onPlaceholderRequest(Player player, String identifier) {
            Uppercore.logger().severe("Resolving " + identifier);
            return BasePlaceholderManager.exec(player, identifier, x -> placeholders.get(x));
        }

        @Override
        public String getIdentifier() {
            return Uppercore.plugin().getName().toLowerCase();
        }

        @Override
        public String getAuthor() {
            return String.join(", ", Uppercore.plugin().getDescription().getAuthors());
        }

        @Override
        public String getVersion() {
            return Uppercore.plugin().getDescription().getVersion();
        }

        /**
         * Because this is an internal class,
         * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
         * PlaceholderAPI is reloaded
         *
         * @return true to persist through reloads
         */
        @Override
        public boolean persist() {
            return true;
        }

        /**
         * Because this is a internal class, this check is not needed
         * and we can simply return {@code true}
         *
         * @return Always true since it's an internal class.
         */
        @Override
        public boolean canRegister(){
            return true;
        }
    }

    @AllArgsConstructor
    private static class PlaceholderHookWrapper implements Placeholder {
        @Getter
        private final String id;
        private final PlaceholderHook hook;

        @Override
        public String resolve(Player player, String arg) {
            return hook.onRequest(player, arg);
        }
    }

    private class PapiPlaceholderRegistry implements PlaceholderRegistry<PapiPlaceholderRegistry> {
        private final PAPIPlaceholderManager parent;

        private PapiPlaceholderRegistry(PAPIPlaceholderManager parent) {
            this.parent = parent;
        }

        public PlaceholderRegistry<?> getParent() {
            return null;
        }

        public void setParent(PlaceholderRegistry<?> parent) {
            throw new UnsupportedOperationException();
        }

        public Placeholder getLocal(String key) {
            var hook = parent.expansionManager.getExpansion(key.toLowerCase(Locale.ENGLISH));
            return hook == null ? null : new PlaceholderHookWrapper(key, hook);
        }

        public Placeholder get(String key) {
            var hook = parent.expansionManager.getExpansion(key.toLowerCase(Locale.ENGLISH));
            return hook == null ? null : new PlaceholderHookWrapper(key, hook);
        }

        public PapiPlaceholderRegistry set(Placeholder placeholder) {
            throw new UnsupportedOperationException("Use PlaceholderUtil.register or PlaceholderManager#register instead!");
        }

        public boolean has(String id) {
            return getPlaceholders().containsKey(id);
        }


        public boolean hasLocal(String id) {
            return getPlaceholders().containsKey(id);
        }
    }
}
