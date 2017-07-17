package xyz.upperlevel.uppercore.script;

import com.google.common.io.Files;
import lombok.Data;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Data
public class ScriptRegistry {

    private final Plugin plugin;
    private final File folder;
    private final Map<String, Script> scripts = new HashMap<>();

    public ScriptRegistry(Plugin plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "scripts");
        ScriptSystem.instance().register(plugin, this);
    }

    public void register(String id, Script gui) {
        scripts.put(id, gui);
        ScriptSystem.instance().register(plugin, id, gui);
    }

    public Script get(String id) {
        return scripts.get(id);
    }

    public Collection<Script> getScripts() {
        return scripts.values();
    }

    public boolean load(String id, Script script) throws ScriptException {
        return scripts.putIfAbsent(id, script) == null;
    }

    public Script load(String id, String script, String ext) throws ScriptException {
        final ScriptSystem system = ScriptSystem.instance();
        final String engineName = system.getExtensionsToEngineName().get(ext);
        if (engineName == null)
            throw new IllegalArgumentException("Cannot find engine for \"" + ext + "\"");
        ScriptEngine engine;
        {//Load the engine
            final Thread currentThread = Thread.currentThread();
            final ClassLoader oldLoader = currentThread.getContextClassLoader();
            try {
                currentThread.setContextClassLoader(Uppercore.get().getScriptSystem().getClassLoader());
                engine = system.getEngineManager().getEngineByName(engineName);
            } finally {
                currentThread.setContextClassLoader(oldLoader);
            }
        }
        if (engine == null)
            throw new IllegalStateException("Cannot find engine \"" + engineName + "\"");
        Script s = Script.of(engine, script);
        return load(id, s) ? s : null;
    }

    public Script load(File file) throws IOException, ScriptException {
        String fileName = file.getName();
        int lastDot = fileName.lastIndexOf('.');
        final String id = fileName.substring(0, lastDot);
        final String ext = fileName.substring(lastDot + 1);
        return load(id, Files.toString(file, StandardCharsets.UTF_8), ext);
    }

    public void loadFolder(File folder) {
        plugin.getLogger().info("Attempting to load scripts at: \"" + folder.getPath() + "\"");
        if (!folder.isDirectory()) {
            plugin.getLogger().severe("Error: " + folder + " isn't a folder");
            return;
        }
        File[] files = folder.listFiles();
        if (files == null) {
            plugin.getLogger().severe("Error reading files in " + folder);
            return;
        }
        for (File file : files) {
            Script res;
            try {
                res = load(file);
            } catch (FileNotFoundException e) {
                plugin.getLogger().severe("Cannot find file " + e);
                continue;
            } catch (ScriptException e) {
                plugin.getLogger().log(Level.SEVERE, "Script error in file " + file.getName(), e);
                continue;
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Unknown error while reading script " + file.getName(), e);
                continue;
            }
            if (res == null)
                plugin.getLogger().severe("Cannot load file " + file.getName() + ": id already used!");
            else
                plugin.getLogger().info("Loaded script " + file.getName() + " with " + res.getEngine().getClass().getSimpleName() + (res instanceof PrecompiledScript ? " (compiled)" : ""));
        }
    }

    public void loadDefaultFolder() {
        loadFolder(folder);
    }
}
