package xyz.upperlevel.uppercore.gui.action;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.uppercore.itemstack.UItem;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.sound.SoundUtil;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface Parser<T> {

    T load(Object object);

    Object save(T t);

    static Parser<String> strValue() {
        return new Parser<String>() {
            @Override
            public String load(Object object) {
                return object.toString();
            }

            @Override
            public Object save(String s) {
                return s;
            }
        };
    }

    static Parser<Short> shortValue() {
        return new Parser<Short>() {
            @Override
            public Short load(Object object) {
                if (object instanceof Number)
                    return ((Number) object).shortValue();
                else if (object instanceof String)
                    return Short.parseShort((String) object);
                else
                    throw new IllegalArgumentException("Cannot parse " + object + " as short");
            }

            @Override
            public Object save(Short s) {
                return s;
            }
        };
    }

    static Parser<Integer> intValue() {
        return new Parser<Integer>() {
            @Override
            public Integer load(Object object) {
                if (object instanceof Number)
                    return ((Number) object).intValue();
                else if (object instanceof String)
                    return Integer.parseInt((String) object);
                else
                    throw new IllegalArgumentException("Cannot parse " + object + " as int");
            }

            @Override
            public Object save(Integer s) {
                return s;
            }
        };
    }

    static Parser<Long> longValue() {
        return new Parser<Long>() {
            @Override
            public Long load(Object object) {
                if (object instanceof Number)
                    return ((Number) object).longValue();
                else if (object instanceof String)
                    return Long.parseLong((String) object);
                else
                    throw new IllegalArgumentException("Cannot parse " + object + " as short");
            }

            @Override
            public Object save(Long s) {
                return s;
            }
        };
    }

    static Parser<Float> floatValue() {
        return new Parser<Float>() {
            @Override
            public Float load(Object object) {
                if (object instanceof Number)
                    return ((Number) object).floatValue();
                else if (object instanceof String)
                    return Float.parseFloat((String) object);
                else
                    throw new IllegalArgumentException("Cannot parse " + object + " as short");
            }

            @Override
            public Object save(Float s) {
                return s;
            }
        };
    }

    static Parser<Double> doubleValue() {
        return new Parser<Double>() {
            @Override
            public Double load(Object object) {
                if (object instanceof Number)
                    return ((Number) object).doubleValue();
                else if (object instanceof String)
                    return Double.parseDouble((String) object);
                else
                    throw new IllegalArgumentException("Cannot parse " + object + " as short");
            }

            @Override
            public Object save(Double s) {
                return s;
            }
        };
    }

    static Parser<Boolean> boolValue() {
        return new Parser<Boolean>() {
            @Override
            public Boolean load(Object raw) {
                if (raw instanceof Boolean) {
                    return (Boolean) raw;
                } else if (raw instanceof String) {
                    switch (((String) raw).toLowerCase()) {
                        case "no":
                        case "false":
                            return false;
                        case "yes":
                        case "true":
                            return true;
                    }
                } else if (raw instanceof Number) {
                    return ((Number) raw).intValue() == 1;
                }
                throw new IllegalArgumentException("Cannot parse " + raw + " as short");
            }

            @Override
            public Object save(Boolean s) {
                return s;
            }
        };
    }

    @SuppressWarnings("unchecked")
    static Parser<List<Action>> actionsValue() {
        return new Parser<List<Action>>() {
            @Override
            public List<Action> load(Object object) {
                if (object instanceof Collection)
                    return ActionType.deserialize((Collection<Map<String, Object>>) object);
                else
                    throw new IllegalArgumentException("Cannot parse " + object + " as short");
            }

            @Override
            public Object save(List<Action> s) {
                return ActionType.serialize(s);
            }
        };
    }

    static Parser<UItem> itemValue() {
        return new Parser<UItem>() {
            @Override
            @SuppressWarnings("unchecked")
            public UItem load(Object object) {
                if (object instanceof ItemStack)
                    return new UItem((ItemStack) object);
                else if (object instanceof Map)
                    return Config.from((Map<String, Object>) object).get(UItem.class);
                else
                    throw new IllegalArgumentException("Cannot parse " + object + " as Item");
            }

            @Override
            public Object save(UItem UItem) {
                throw new UnsupportedOperationException();
            }
        };
    }

    static Parser<Sound> soundValue() {
        return new Parser<Sound>() {
            @Override
            public Sound load(Object object) {
                if (object instanceof Sound)
                    return (Sound) object;
                else {
                    return SoundUtil.get(object.toString())
                            .orElseThrow(() -> new IllegalArgumentException("Cannot find sound \"" + object + "\", is it supported?"));
                }
            }

            @Override
            public Object save(Sound sound) {
                return sound.name().toLowerCase(Locale.ENGLISH).replace('_', ' ');
            }
        };
    }

    static <T extends Enum<T>> Parser<T> enumValue(Class<T> clazz) {
        return new Parser<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T load(Object object) {
                if (clazz.isInstance(object)) {
                    return (T) object;
                } else if (object instanceof String) {
                    return Enum.valueOf(clazz, ((String) object).replace(' ', '_').toUpperCase(Locale.ENGLISH));
                } else
                    throw new IllegalArgumentException("Cannot parse " + object + " as " + clazz.getSimpleName());
            }

            @Override
            public Object save(T t) {
                return t.name().toLowerCase(Locale.ENGLISH).replace('_', ' ');
            }
        };
    }
}
