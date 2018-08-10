package xyz.upperlevel.uppercore.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Turns out that sometimes the plugin that is setting up the configuration is useful.
 * This, when applied to a config property (of the {@link org.bukkit.plugin.Plugin} type) will
 * fill the parameter with the curren plugin
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentPlugin {
}
