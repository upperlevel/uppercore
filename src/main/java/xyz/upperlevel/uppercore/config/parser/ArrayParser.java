package xyz.upperlevel.uppercore.config.parser;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.List;

public class ArrayParser extends ConfigParser {
    private final ArraySetter arraySetter;
    private final Type baseType;
    private final Class<?> componentClass;
    private final ConfigParser handleParser;

    public ArrayParser(Type handleType, ConfigParserRegistry registry) {
        super(handleType);
        this.arraySetter = selectSetter();
        this.baseType = extractBaseType(handleType);
        this.componentClass = extractComponentClass(baseType);
        this.handleParser = registry.getFor(baseType);
    }

    public static Class<?> extractComponentClass(Type t) {
        if (t instanceof Class) return (Class<?>) t;
        if (t instanceof GenericArrayType) return extractComponentClass(((GenericArrayType) t).getGenericComponentType());
        throw new IllegalStateException("Uknown array type " + t);
    }

    private static Type extractBaseType(Type type) {
        if (type instanceof Class) return ((Class) type).getComponentType();
        if (type instanceof GenericArrayType) return ((GenericArrayType) type).getGenericComponentType();
        throw new IllegalStateException("Unknown array type: " + type);
    }

    @Override
    public Object parse(Node root) {
        checkTag(root, Tag.SEQ);


        SequenceNode node = (SequenceNode) root;
        int length = node.getValue().size();
        Object array = Array.newInstance(componentClass, length);
        List<Node> entries = node.getValue();
        for (int i = 0; i < length; i++) {
            arraySetter.set(array, i, handleParser.parse(entries.get(i)));
        }

        return array;
    }

    private ArraySetter selectSetter() {
        Type type = baseType;
        if (type == Byte.TYPE) {
            return (arr, i, o) -> Array.setByte(arr, i, (Byte) o);
        } else if (type == Short.TYPE) {
            return (arr, i, o) -> Array.setShort(arr, i, (Short) o);
        } else if (type == Integer.TYPE) {
            return (arr, i, o) -> Array.setInt(arr, i, (Integer) o);
        } else if (type == Long.TYPE) {
            return (arr, i, o) -> Array.setLong(arr, i, (Long) o);
        }  else if (type == Float.TYPE) {
            return (arr, i, o) -> Array.setFloat(arr, i, (Long) o);
        } else if (type == Double.TYPE) {
            return (arr, i, o) -> Array.setDouble(arr, i, (Double) o);
        } else if (type == Character.TYPE) {
            return (arr, i, o) -> Array.setChar(arr, i, (Character) o);
        } else if (type == Boolean.TYPE) {
            return (arr, i, o) -> Array.setBoolean(arr, i, (Boolean) o);
        } else {
            return Array::set;
        }
    }

    interface ArraySetter {
        void set(Object array, int index, Object val);
    }
}
