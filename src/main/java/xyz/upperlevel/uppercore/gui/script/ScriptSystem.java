package xyz.upperlevel.uppercore.gui.script;

import com.google.common.io.Files;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.upperlevel.uppercore.Uppercore;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class ScriptSystem {
    private final File classPath;
    @Getter
    private final ClassLoader classLoader;
    private final ScriptEngineManager engineManager;
    private Map<String, String> extensionsToEngineName;
    private Map<String, Script> scripts = new HashMap<>();

    public ScriptSystem(File classPath, File scriptEngineConfig) {
        this.classPath = classPath;
        {//Create classLoader
            File[] files = classPath.listFiles();
            URL[] urls;
            if (files == null)
                urls = new URL[]{};
            else
                urls = Arrays.stream(files)
                        .map(f -> {
                            try {
                                return f.toURI().toURL();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toArray(URL[]::new);

            classLoader = new URLClassLoader(urls, getClass().getClassLoader());
        }
        engineManager = new ScriptEngineManager(classLoader);
        {//Print found engines
            List<ScriptEngineFactory> factories = engineManager.getEngineFactories();
            Uppercore.logger().info("Engines found: " + factories.size());
            if (factories.size() > 0) {
                System.out.println("-----------------------------------------------");
                for (ScriptEngineFactory f : factories) {
                    System.out.println("engine name:" + f.getEngineName());
                    System.out.println("engine version:" + f.getEngineVersion());
                    System.out.println("language name:" + f.getLanguageName());
                    System.out.println("language version:" + f.getLanguageVersion());
                    System.out.println("names:" + f.getNames());
                    System.out.println("mime:" + f.getMimeTypes());
                    System.out.println("extension:" + f.getExtensions());
                    System.out.println("-----------------------------------------------");
                }
            }
        }
        engineManager.put("Bukkit", Bukkit.getServer());
        try {
            final ClassLoader old = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            ScriptEngine engine = engineManager.getEngineByName("js");
            engine.eval("Java.type(\"xyz.upperlevel.spigot.gui.Uppercore\").logger().info(\"JS engine works!\")");
            Thread.currentThread().setContextClassLoader(old);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        reloadConfig(scriptEngineConfig);
    }

    public void reloadConfig(File configFile) {
        extensionsToEngineName = new HashMap<>();

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection("engines");
        for(Map.Entry<String, Object> obj : section.getValues(false).entrySet())
            extensionsToEngineName.put(obj.getKey(), obj.getValue().toString());
    }

    public boolean load(String id, Script script) throws ScriptException {
        return scripts.putIfAbsent(id, script) == null;
    }

    public Script load(String id, String script, String ext) throws ScriptException {
        final String engineName = extensionsToEngineName.get(ext);
        if(engineName == null)
            throw new IllegalArgumentException("Cannot find engine for \"" + ext + "\"");
        ScriptEngine engine;
        {//Load the engine
            final Thread currentThread = Thread.currentThread();
            final ClassLoader oldLoader = currentThread.getContextClassLoader();
            try {
                currentThread.setContextClassLoader(Uppercore.get().getScriptSystem().getClassLoader());
                engine = engineManager.getEngineByName(engineName);
            } finally {
                currentThread.setContextClassLoader(oldLoader);
            }
        }
        if(engine == null)
            throw new IllegalStateException("Cannot find engine \"" + engineName + "\"");
        Script s = Script.of(engine, script);
        return load(id, s) ? s : null;
    }

    public Script load(File file) throws IOException, ScriptException {
        String fileName = file.getName();
        int lastDot =  fileName.lastIndexOf('.');
        final String id = fileName.substring(0, lastDot);
        final String ext = fileName.substring(lastDot + 1);
        return load(id, Files.toString(file, StandardCharsets.UTF_8), ext);
    }

    public void loadFolder(File folder) {
        if(!folder.isDirectory()) {
            Uppercore.logger().severe("Error: " + folder + " isn't a folder");
            return;
        }
        File[] files = folder.listFiles();
        if(files == null) {
            Uppercore.logger().severe("Error reading files in " + folder);
            return;
        }
        for(File file : files) {
            Script res;
            try {
                res = load(file);
            } catch (FileNotFoundException e) {
                Uppercore.logger().severe("Cannot find file " + e);
                continue;
            } catch (ScriptException e) {
                Uppercore.logger().log(Level.SEVERE, "Script error in file " + file.getName(), e);
                continue;
            } catch (Exception e) {
                Uppercore.logger().log(Level.SEVERE, "Unknown error while reading script " + file.getName(), e);
                continue;
            }
            if(res == null)
                Uppercore.logger().severe("Cannot load file " + file.getName() + ": id already used!");
            else
                Uppercore.logger().info("Loaded script " + file.getName() + " with " + res.getEngine().getClass().getSimpleName() + (res instanceof PrecompiledScript ? " (compiled)" : ""));
        }
    }

    public void clearScripts() {
        scripts.clear();
    }

    public Script get(String id) {
        return scripts.get(id);
    }

    public Map<String, Script> get() {
        return Collections.unmodifiableMap(scripts);
    }
}
