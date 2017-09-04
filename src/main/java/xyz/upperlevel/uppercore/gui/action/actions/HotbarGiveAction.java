package xyz.upperlevel.uppercore.gui.action.actions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.hotbar.HotbarId;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static xyz.upperlevel.uppercore.Uppercore.hotbars;

@Getter
public class HotbarGiveAction extends Action<HotbarGiveAction> {

	public static final HotbarGiveActionType TYPE = new HotbarGiveActionType();

	private final PlaceholderValue<String> id;
	private final List<Action> actions;
	private final List<Action> fail;

	public HotbarGiveAction(Plugin plugin, PlaceholderValue<String> id, List<Action> actions, List<Action> fail) {
		super(plugin, TYPE);
		this.id = id;
		this.actions = actions;
		this.fail = fail;
	}

	@Override
	public void run(Player player) {
		String hotbarId = id.resolve(player);
		HotbarId hotbar = hotbars().get(getPlugin(), hotbarId);
		if (hotbar == null) {
			Uppercore.logger().severe("Cannot find hotbar \"" + hotbarId + "\"");
			return;
		}
		hotbar.get().give(player);
	}

	public static class HotbarGiveActionType extends BaseActionType<HotbarGiveAction> {

		public HotbarGiveActionType() {
			super("hotbar-give");
			setParameters(Parameter.of("id", Parser.strValue(), true),
					Parameter.of("actions", Parser.actionsValue(), Collections.emptyList(), false),
					Parameter.of("fail", Parser.actionsValue(), Collections.emptyList(), false));
		}

		@Override
		@SuppressWarnings("unchecked")
		public HotbarGiveAction create(Plugin plugin, Map<String, Object> pars) {
			return new HotbarGiveAction(plugin, PlaceholderValue.stringValue((String) pars.get("id")),
					(List<Action>) pars.get("actions"), (List<Action>) pars.get("fail"));
		}

		@Override
		public Map<String, Object> read(HotbarGiveAction action) {
			return ImmutableMap.of("id", action.id.toString(), "action", action.actions, "fail", action.fail);
		}
	}
}
