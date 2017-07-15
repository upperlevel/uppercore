package xyz.upperlevel.uppercore.gui.config.action;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.uppercore.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.uppercore.gui.config.action.actions.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class ActionType<T extends Action> {
    private static Map<String, ActionType> types = new HashMap<>();

    static {
        registerDefaults();
    }

    @Getter
    private final String type;

    public abstract T load(Object config);

    public abstract Object save(T action);


    public static void addActionType(ActionType<?> type) {
        types.put(type.getType().toLowerCase(), type);
    }

    public static void removeActionType(ActionType<?> type) {
        types.remove(type.getType());
    }

    private static void registerDefaults() {
        //Guis
        addActionType(GuiOpenAction.TYPE);
        addActionType(GuiBackAction.TYPE);
        addActionType(GuiCloseAction.TYPE);
        addActionType(GuiChangeAction.TYPE);
        addActionType(GuiReloadAction.TYPE);
        //Hotbars
        addActionType(HotbarGiveAction.TYPE);
        addActionType(HotbarRemoveAction.TYPE);
        //Vault
        addActionType(VaultGiveAction.TYPE);
        addActionType(VaultTakeAction.TYPE);
        //Player interaction
        addActionType(BroadcastAction.TYPE);
        addActionType(MessageAction.TYPE);
        addActionType(PlaySoundAction.TYPE);
        addActionType(GiveItemAction.TYPE);
        //Misc
        addActionType(RequireAction.TYPE);
        addActionType(ScriptAction.TYPE);
        addActionType(CommandAction.TYPE);
    }

    public static List<Action> deserialize(Collection<Map<String, Object>> config) {
        return config.stream().map(ActionType::deserialize).collect(Collectors.toList());
    }

    public static Action deserialize(Object config) {
        if(config instanceof Map) {
            Map<String, Object> c = (Map<String, Object>) config;
            if (c.size() > 1)
                throw new InvalidGuiConfigurationException("cannot have more than one action for now");
            Map.Entry<String, Object> action = c.entrySet().iterator().next();
            String type = action.getKey();
            if (type == null)
                throw new IllegalArgumentException("Field \"type\" needed");
            ActionType t = types.get(type.toLowerCase());
            if (t == null)
                throw new IllegalArgumentException("Cannot find action \"" + type + "\" in " + types.keySet());
            return t.load(action.getValue());
        } else if(config instanceof String) {
            String type = (String) config;
            ActionType t = types.get(type.toLowerCase());
            if (t == null)
                throw new IllegalArgumentException("Cannot find action \"" + type + "\" in " + types.keySet());
            return t.load(null);//No argument
        } else
            throw new InvalidGuiConfigurationException("Invalid value type");
    }

    public static <T extends Action<T>> Map<String, Object> serialize(T action) {
        ActionType<T> type = action.getType();
        Object obj = type.save(action);
        return ImmutableMap.of(type.getType(), obj);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> serialize(List<Action> s) {
        return s.stream()
                .map((Function<Action, Map<String, Object>>) ActionType::serialize)
                .collect(Collectors.toList());
    }
}
