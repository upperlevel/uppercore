package xyz.upperlevel.uppercore.gui.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;

@Getter
public class CommandAction extends Action<CommandAction> {

    public static final CommandActionType TYPE = new CommandActionType();

    private final PlaceholderValue<String> command;
    private final Executor executor;

    public CommandAction(Plugin plugin, PlaceholderValue<String> command, Executor executor) {
        super(plugin, TYPE);
        this.command = command;
        this.executor = executor;
    }

    @Override
    public void run(Player player) {
        executor.execute(player, command.resolve(player));
    }


    public static class CommandActionType extends BaseActionType<CommandAction> {

        public CommandActionType() {
            super("command");
            setParameters(
                    Parameter.of("command", Parser.strValue(), true),
                    Parameter.of("executor", Parser.enumValue(Executor.class), Executor.PLAYER, false)
            );
        }

        @Override
        public CommandAction create(Plugin plugin, Map<String, Object> pars) {
            return new CommandAction(
                    plugin,
                    PlaceholderUtil.process((String) pars.get("command")),
                    (Executor) pars.get("executor")
            );
        }

        @Override
        public Map<String, Object> read(CommandAction action) {
            return ImmutableMap.of(
                    "id", action.command.toString(),
                    "executor", action.executor
            );
        }
    }

    public enum Executor {
        PLAYER {
            @Override
            public void execute(Player player, String command) {
                player.performCommand(command);
            }
        },
        CONSOLE {
            @Override
            public void execute(Player player, String command) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        };

        public abstract void execute(Player player, String command);
    }
}

