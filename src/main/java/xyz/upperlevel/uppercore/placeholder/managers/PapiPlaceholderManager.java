package xyz.upperlevel.uppercore.placeholder.managers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * PlaceholderManager that hooks into PlaceholderAPI (Papi) and works with it.
 */
public class PapiPlaceholderManager extends BasePlaceholderManager {

    private final Map<String, PlaceholderHook> placeholders;
    @Getter
    private final PapiPlaceholderRegistry registry = new PapiPlaceholderRegistry();

    @SuppressWarnings("unchecked")
    public PapiPlaceholderManager() {
        try {
            Field field = PlaceholderAPI.class.getDeclaredField("placeholders");
            field.setAccessible(true);
            placeholders = (Map<String, PlaceholderHook>) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Uncompatible PlaceholderAPI exception");
        }
    }

    @Override
    public void register(Plugin plugin, Placeholder placeholder) {
        new OfficialPlaceholderAdapter(plugin, placeholder).register();
    }

    public Placeholder find(String id) {
        PlaceholderHook hook = placeholders.get(id);
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


    private static class OfficialPlaceholderAdapter extends PlaceholderExpansion {
        private final Placeholder placeholder;
        private final Plugin plugin;

        public OfficialPlaceholderAdapter(Plugin plugin, Placeholder placeholder) {
            this.plugin = plugin;
            this.placeholder = placeholder;
        }

        @Override
        public String onPlaceholderRequest(Player player, String identifier) {
            return placeholder.resolve(player, identifier);
        }

        @Override
        public String getIdentifier() {
            return placeholder.getId();
        }

        @Override
        public String getAuthor() {
            return String.join(", ", plugin.getDescription().getAuthors());
        }

        @Override
        public String getVersion() {
            return plugin.getDescription().getVersion();
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
        public PlaceholderRegistry<?> getParent() {
            return null;
        }

        public void setParent(PlaceholderRegistry<?> parent) {
            throw new UnsupportedOperationException();
        }

        public Placeholder getLocal(String key) {
            PlaceholderHook hook = placeholders.get(key);
            return hook == null ? null : new PlaceholderHookWrapper(key, hook);
        }

        public Placeholder get(String key) {
            PlaceholderHook hook = placeholders.get(key);
            return hook == null ? null : new PlaceholderHookWrapper(key, hook);
        }

        public PapiPlaceholderRegistry set(Placeholder placeholder) {
            throw new UnsupportedOperationException("Use PlaceholderUtil.register or PlaceholderManager#register instead!");
        }

        public boolean has(String id) {
            return placeholders.containsKey(id);
        }


        public boolean hasLocal(String id) {
            return placeholders.containsKey(id);
        }
    }
}
