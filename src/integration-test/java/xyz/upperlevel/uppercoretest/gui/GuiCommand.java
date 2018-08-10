package xyz.upperlevel.uppercoretest.gui;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.PermissionCompleter;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.registry.Registry;
import xyz.upperlevel.uppercoretest.UppercoreTest;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiCommand extends Command {
    private Registry<Gui> registry;

    public GuiCommand(Registry<Gui> registry) {
        super("testgui");
        Logger logger = UppercoreTest.logger("guis");
        this.registry = registry;

        setDescription("Open a gui");

        setPermissionCompleter(PermissionCompleter.NONE);

        // Register guis
        registry.register("test", new InputStreamReader(getClass().getResourceAsStream("/guis/test.yml")), Gui.CONFIG_LOADER);

        logger.info("Registered " + registry.getRegistered().size() + " guis: " + registry.getRegistered().keySet());
    }

    @Override
    public String getUsage(CommandSender sender, boolean colored) {
        return "<name>";
    }

    @Override
    protected boolean onCall(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only callable from player");
            return true;
        }
        if (args.isEmpty()) {
            return false;
        }
        String arg = String.join(" ", args);

        Gui gui = (Gui) registry.find(arg);
        if (gui == null) {
            sender.sendMessage(ChatColor.RED + "Gui " + arg + " not found");
            return true;
        }

        guis().open((Player) sender, gui);
        sender.sendMessage(ChatColor.GREEN + "Gui opened '" + arg + "'");
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            return Collections.emptyList();
        }
        String arg = String.join(" ", args);

        return registry.getRegistered().keySet().stream()
                .filter(s -> s.startsWith(arg))
                .collect(Collectors.toList());
    }
}
