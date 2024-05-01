package xyz.upperlevel.uppercore.command.functional.parameter;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.util.Vector;
import xyz.upperlevel.uppercore.config.ConfigUtil;
import xyz.upperlevel.uppercore.sound.SoundUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

import static xyz.upperlevel.uppercore.util.PluginUtil.parseNamespacedKey;

public final class BukkitParameterHandler {
    private BukkitParameterHandler() {
    }

    public static void register() {
        // Color
        ParameterHandler.register(
                Collections.singletonList(Color.class),
                args -> {
                    try {
                        return ConfigUtil.parseColor(args.take());
                    } catch (NumberFormatException ign) {
                        throw args.areWrong();
                    }
                });

        // Enchantment
        ParameterHandler.register(
                Collections.singletonList(Enchantment.class),
                args -> {
                    String arg = args.take().toLowerCase(Locale.ENGLISH);
                    Enchantment result = Enchantment.getByKey(parseNamespacedKey(arg));
                    if (result == null) {
                        throw args.areWrong();
                    }
                    return result;
                },
                args -> {
                    // args list can't be empty in a suggestion.
                    if (args.remaining() > 1)
                        return Collections.emptyList();
                    String arg = args.take().toLowerCase(Locale.ENGLISH);
                    return Arrays
                            .stream(Enchantment.values())
                            .map(Enchantment::getKey)
                            .filter(name -> name.getKey().startsWith(arg) || name.getNamespace().startsWith(arg))
                            .map(NamespacedKey::toString)
                            .collect(Collectors.toList());
                });

        // Material
        ParameterHandler.register(
                Collections.singletonList(Material.class),
                args -> {
                    String arg = args.take().toLowerCase(Locale.ENGLISH);
                    Material mat = Material.getMaterial(arg);
                    if (mat == null)
                        throw args.areWrong();
                    return mat;
                },
                args -> {
                    // args list can't be empty in a suggestion.
                    if (args.remaining() > 1)
                        return Collections.emptyList();
                    String arg = args.take().toLowerCase(Locale.ENGLISH);
                    return Arrays
                            .stream(Material.values())
                            .map(Material::getKey)
                            .filter(name -> name.getKey().startsWith(arg) || name.getNamespace().startsWith(arg))
                            .map(NamespacedKey::toString)
                            .collect(Collectors.toList());
                });

        // Sound
        ParameterHandler.register(
                Collections.singletonList(Sound.class),
                args -> SoundUtil.get(args.take()).orElseThrow(args::areWrong),
                args -> {
                    if (args.remaining() > 1)
                        return Collections.emptyList();
                    String arg = args.take().toUpperCase(Locale.ENGLISH);
                    return Arrays.stream(Sound.values())
                            .map(Enum::name)
                            .filter(sound -> sound.startsWith(arg))
                            .collect(Collectors.toList());
                });

        // Vector
        ParameterHandler.register(
                Collections.singletonList(Vector.class),
                args -> {
                    try {
                        return new Vector(
                                Double.parseDouble(args.take()),
                                Double.parseDouble(args.take()),
                                Double.parseDouble(args.take())
                        );
                    } catch (NumberFormatException e) {
                        throw args.areWrong();
                    }
                });
    }
}
