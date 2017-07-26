package xyz.upperlevel.uppercore.script;

import com.google.common.io.Files;
import lombok.Data;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Loader;
import xyz.upperlevel.uppercore.Registry;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static xyz.upperlevel.uppercore.Uppercore.scripts;

@Data
public class ScriptRegistry extends Registry<ScriptId> {
    public static final Loader<ScriptId> LOADER = ScriptRegistry::load;

    public ScriptRegistry(Plugin plugin) {
        super(plugin, "scripts");
        scripts().register(this);
    }

    @Override
    public void register(ScriptId script) {
        super.register(script);
        scripts().register(script);
    }

    @Override
    public ScriptId unregister(String id) {
        ScriptId result = super.unregister(id);
        if (result != null)
            scripts().unregister(result);
        return result;
    }

    public void load(File file) {
        load(file, LOADER);
    }

    public void loadFile(File file) {
        loadFile(file, LOADER);
    }

    public void loadFolder(File file) {
        loadFolder(file, LOADER);
    }

    @Override
    protected void postLoad(File in, ScriptId out) {
        Script script = out.get();
        getLogger().info("Successfully loaded script \"" + out.getId() + "\" with " + script.getEngine().getClass().getSimpleName() + (script instanceof PrecompiledScript ? " (compiled)" : ""));
    }

    protected static ScriptId load(Plugin plugin, String id, String script, String ext) throws ScriptException {
        ScriptManager manager = scripts();
        final String engineName = manager.getExtensionsToEngineName().get(ext);
        if (engineName == null)
            throw new IllegalArgumentException("Cannot find engine for \"" + ext + "\"");
        ScriptEngine engine;
        {//Load the engine
            final Thread currentThread = Thread.currentThread();
            final ClassLoader oldLoader = currentThread.getContextClassLoader();
            try {
                currentThread.setContextClassLoader(manager.getClassLoader());
                engine = manager.getEngineManager().getEngineByName(engineName);
            } finally {
                currentThread.setContextClassLoader(oldLoader);
            }
        }
        if (engine == null)
            throw new IllegalStateException("Cannot find engine \"" + engineName + "\"");
        return new ScriptId(plugin, id, Script.of(engine, script));
    }

    protected static ScriptId load(Plugin plugin, String id, File file) {
        String fileName = file.getName();
        int lastDot = fileName.lastIndexOf('.');
        final String ext = fileName.substring(lastDot + 1);
        try {
            return load(plugin, id, Files.toString(file, StandardCharsets.UTF_8), ext);
        } catch (IOException | ScriptException e) {
            throw new InvalidConfigurationException("while loading script \"" + id + "\"");
        }
    }
}
