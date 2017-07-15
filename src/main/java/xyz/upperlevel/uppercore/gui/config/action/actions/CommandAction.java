package xyz.upperlevel.uppercore.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.config.action.Parser;
import xyz.upperlevel.uppercore.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.uppercore.gui.config.placeholders.PlaceholderValue;

import java.util.Map;

public class CommandAction extends Action<CommandAction> {
    public static final CommandActionType TYPE = new CommandActionType();
    @Getter
    private final PlaceholderValue<String> command;
    @Getter
    private final Executor executor;

    public CommandAction(PlaceholderValue<String> command, Executor executor) {
        super(TYPE);
        this.command = command;
        this.executor = executor;
    }

    @Override
    public void run(Player player) {
        executor.execute(player, command.get(player));
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
        public CommandAction create(Map<String, Object> pars) {
            return new CommandAction(
                    PlaceHolderUtil.process((String) pars.get("command")),
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
            void execute(Player player, String command) {
                player.performCommand(command);
            }
        },
        CONSOLE {
            @Override
            void execute(Player player, String command) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        };

        abstract void execute(Player player, String command);
    }
}

