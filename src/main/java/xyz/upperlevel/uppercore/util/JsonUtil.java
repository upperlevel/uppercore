package xyz.upperlevel.uppercore.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Map;

import static xyz.upperlevel.uppercore.util.TypeUtil.typeOf;

public final class JsonUtil {
    public static final Gson GENERAL_GSON = new Gson();
    public static final Type JSON_MAP_TYPE = typeOf(Map.class, String.class, Object.class);

    private JsonUtil() {}
}
