package xyz.upperlevel.uppercore.script;

import lombok.Getter;
import org.bstats.Metrics;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptManager {

    @Getter
    private File classPath;

    @Getter
    private ClassLoader classLoader;
    @Getter
    private ScriptEngineManager engineManager;
    @Getter
    private Map<String, String> extensionsToEngineName;

    public void load(File classPath, File scriptEngineConfig) {
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

            classLoader = new URLClassLoader(urls, ScriptManager.class.getClassLoader());
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
            engine.eval("Java.type(\"xyz.upperlevel.uppercore.Uppercore\").logger().info(\"JS engine works!\")");
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
        for (Map.Entry<String, Object> obj : section.getValues(false).entrySet())
            extensionsToEngineName.put(obj.getKey(), obj.getValue().toString());
    }

    public void setupMetrics(Metrics metrics) {
        metrics.addCustomChart(new Metrics.AdvancedPie("script_engines_used") {

            @Override
            public HashMap<String, Integer> getValues(HashMap<String, Integer> map) {
                /* Todo:
                Map<String, Long> counts = get()
                        .stream()
                        .collect(
                                Collectors.groupingBy((ScriptId s) -> getEngineName(s.get().getEngine()),
                                        Collectors.counting())
                        );
                for (Map.Entry<String, Long> e : counts.entrySet())
                    map.put(e.getKey(), Math.toIntExact(e.getValue()));
                    */
                return map;
            }
        });
    }

    public static String getEngineName(ScriptEngine engine) {
        return engine.getClass().getSimpleName()
                .replaceFirst("ScriptEngine", "")
                .toLowerCase(Locale.ENGLISH);
    }
}
