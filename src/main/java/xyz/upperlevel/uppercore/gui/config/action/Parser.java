package xyz.upperlevel.uppercore.gui.config.action;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import xyz.upperlevel.uppercore.gui.config.itemstack.CustomItem;
import xyz.upperlevel.uppercore.gui.config.util.Config;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface Parser<T> {
    T load(Object o);

    Object save(T t);

    static Parser<String> strValue() {
        return new Parser<String>() {
            @Override
            public String load(Object o) {
                return o.toString();
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
            public Short load(Object o) {
                if(o instanceof Number)
                    return ((Number) o).shortValue();
                else if(o instanceof String)
                    return Short.parseShort((String) o);
                else
                    throw new IllegalArgumentException("Cannot parse " + o + " as short");
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
            public Integer load(Object o) {
                if(o instanceof Number)
                    return ((Number) o).intValue();
                else if(o instanceof String)
                    return Integer.parseInt((String) o);
                else
                    throw new IllegalArgumentException("Cannot parse " + o + " as int");
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
            public Long load(Object o) {
                if(o instanceof Number)
                    return ((Number) o).longValue();
                else if(o instanceof String)
                    return Long.parseLong((String) o);
                else
                    throw new IllegalArgumentException("Cannot parse " + o + " as short");
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
            public Float load(Object o) {
                if(o instanceof Number)
                    return ((Number) o).floatValue();
                else if(o instanceof String)
                    return Float.parseFloat((String) o);
                else
                    throw new IllegalArgumentException("Cannot parse " + o + " as short");
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
            public Double load(Object o) {
                if(o instanceof Number)
                    return ((Number) o).doubleValue();
                else if(o instanceof String)
                    return Double.parseDouble((String) o);
                else
                    throw new IllegalArgumentException("Cannot parse " + o + " as short");
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
                if(raw instanceof Boolean) {
                    return (Boolean) raw;
                } else if(raw instanceof String){
                    switch (((String) raw).toLowerCase()) {
                        case "no":
                        case "false":
                            return false;
                        case "yes":
                        case "true":
                            return true;
                    }
                } else if(raw instanceof Number) {
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
            public List<Action> load(Object o) {
                if(o instanceof Collection)
                    return ActionType.deserialize((Collection<Map<String, Object>>)o);
                else
                    throw new IllegalArgumentException("Cannot parse " + o + " as short");
            }

            @Override
            public Object save(List<Action> s) {
                return ActionType.serialize(s);
            }
        };
    }

    static Parser<CustomItem> itemValue() {
        return new Parser<CustomItem>() {
            @Override
            @SuppressWarnings("unchecked")
            public CustomItem load(Object o) {
                if(o instanceof ItemStack)
                    return new CustomItem((ItemStack) o);
                else if(o instanceof Map)
                    return CustomItem.deserialize(Config.wrap((Map<String, Object>) o));
                else
                    throw new IllegalArgumentException("Cannot parse " + o + " as Item");
            }

            @Override
            public Object save(CustomItem customItem) {
                throw new NotImplementedException();
            }
        };
    }

    static Parser<Sound> soundValue() {
        return new Parser<Sound>() {
            @Override
            public Sound load(Object o) {
                if(o instanceof Sound)
                    return (Sound) o;
                else if(o instanceof String)
                    return Sound.valueOf(((String) o).replace(' ', '_').toUpperCase(Locale.ENGLISH));
                else
                    throw new IllegalArgumentException("Cannot parse " + o + " as sound");
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
            public T load(Object o) {
                if(clazz.isInstance(o)) {
                    return (T) o;
                } else if(o instanceof String) {
                    return Enum.valueOf(clazz, ((String) o).replace(' ', '_').toUpperCase(Locale.ENGLISH));
                } else
                    throw new IllegalArgumentException("Cannot parse " + o + " as " + clazz.getSimpleName());
            }

            @Override
            public Object save(T t) {
                return t.name().toLowerCase(Locale.ENGLISH).replace('_', ' ');
            }
        };
    }
}
