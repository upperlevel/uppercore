package xyz.upperlevel.uppercore.config.parser;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.List;

public class ArrayParser<T> extends ConfigParser<T> {
    private final ArraySetter arraySetter;
    private final Class<?> arrayClass;
    private final ConfigParser<?> handleParser;

    public ArrayParser(Class<T> handleClass, Type handleType, ConfigParserRegistry registry) {
        super(handleClass);
        this.arraySetter = selectSetter();
        this.arrayClass = getHandleClass().getComponentType();

        Type arrayType;
        if (handleType instanceof GenericArrayType) {
            // Generics on array found (ex. List<Integer>[])
            GenericArrayType type = (GenericArrayType) handleType;// Null if there are no arguments in array
            arrayType = type.getGenericComponentType();
        } else {
            arrayType = arrayClass;
        }
        this.handleParser = registry.getFor(arrayClass, arrayType);
    }

    @Override
    public T parse(Plugin plugin, Node root) {
        checkTag(root, Tag.SEQ);


        SequenceNode node = (SequenceNode) root;
        int length = node.getValue().size();
        Object array = Array.newInstance(arrayClass, length);
        List<Node> entries = node.getValue();
        for (int i = 0; i < length; i++) {
            arraySetter.set(array, i, handleParser.parse(plugin, entries.get(i)));
        }

        return (T) array;
    }

    private ArraySetter selectSetter() {
        Class<?> type = arrayClass;
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
