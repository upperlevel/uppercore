package xyz.upperlevel.uppercore.command.functional.parameter;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;

public class TestAllArgumentParser {

    @Test
    public void primitiveParsing() {
        Map<String, Class<?>> data = new HashMap<>();
        data.put("my_string", String.class);
        data.put("123", int.class);
        data.put("20.0", double.class);
        data.put("10.9", float.class);
        data.put("true", boolean.class);

        PrimitiveParameterHandler.register();
        BukkitParameterHandler.register();

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Class<?>> entry : data.entrySet()) {
            try {
                Object parsed = ParameterHandler.parse(entry.getValue(), new ArrayDeque<>(singletonList(entry.getKey())));
                result.put(entry.getKey(), parsed);
            } catch (ParameterParseException e) {
                throw new IllegalStateException(e);
            }
        }

        assert result.get("my_string") == "my_string";
        assert ((Integer) result.get("123")) == 123;
        assert ((Double) result.get("20.0")) == 20.0;
        assert ((Float) result.get("10.9")) == 10.9f;
        assert (Boolean) result.get("true");
    }
}
