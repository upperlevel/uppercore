package xyz.upperlevel.uppercoretest.gui;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.PermissionCompleter;
import xyz.upperlevel.uppercore.hotbar.Hotbar;
import xyz.upperlevel.uppercore.registry.Registry;
import xyz.upperlevel.uppercoretest.UppercoreTest;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static xyz.upperlevel.uppercore.Uppercore.hotbars;

public class HotbarCommand extends Command {
    private Registry<Hotbar> registry;

    public HotbarCommand(Registry<Hotbar> registry) {
        super("testbar");
        Logger logger = UppercoreTest.logger("guis");
        this.registry = registry;

        setDescription("Open a hotbar");

        setPermissionCompleter(PermissionCompleter.NONE);

        // Register guis
        registry.load("test", new InputStreamReader(getClass().getResourceAsStream("/hotbars/test.yml")), Hotbar.CONFIG_LOADER);

        logger.info("Registered " + registry.getRegistered().size() + " hotbars: " + registry.getRegistered().keySet());
    }

    @Override
    public String getUsage(CommandSender sender, boolean colored) {
        return "<name>";
    }

    public String reverseNameLookup(Hotbar hotbar) {
        return registry.getRegistered().entrySet()
                .stream()
                .filter(e -> e.getValue() == hotbar)
                .findAny()
                .map(Map.Entry::getKey)
                .orElse("<unknown>");
    }

    @Override
    protected boolean onCall(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only callable from player");
            return true;
        }
        Player player = (Player) sender;
        if (args.isEmpty()) {
            sender.sendMessage("Missing subcommand");
            return false;
        }

        String command = args.get(0);

        switch (command) {
            case "open": {
                if (args.size() < 2) {
                    sender.sendMessage("Missing gui name");
                    return false;
                }
                String arg = args.get(1);
                Hotbar hotbar = registry.find(args.get(1), Hotbar.class);
                if (hotbar == null) {
                    sender.sendMessage(ChatColor.RED + "Hotbar " + arg + " not found");
                    return true;
                }

                hotbars().view(player).addHotbar(hotbar);
                sender.sendMessage(ChatColor.GREEN + "Hotbar opened '" + arg + "'");
                break;
            }
            case "list": {
                Set<String> hotbars = registry.getRegistered().keySet();
                sender.sendMessage(ChatColor.AQUA + "Listing " + hotbars.size() + " hotbars:");
                for (String name : hotbars) {
                    sender.sendMessage("- " + name);
                }
                break;
            }
            case "check": {
                if (args.size() < 2) {
                    Set<Hotbar> hotbars = hotbars().view(player).getHotbars();
                    sender.sendMessage(ChatColor.AQUA + "Listing " + hotbars.size() + " hotbars:");
                    for (Hotbar hotbar : hotbars) {
                        sender.sendMessage(ChatColor.AQUA + "- " + reverseNameLookup(hotbar));
                    }
                } else {
                    String arg = args.get(1);
                    Hotbar hotbar = registry.find(args.get(1), Hotbar.class);
                    if (hotbar == null) {
                        sender.sendMessage(ChatColor.RED + "Hotbar " + arg + " not found");
                        return true;
                    }

                    boolean open = hotbars().view(player).isHolding(hotbar);
                    sender.sendMessage(ChatColor.GREEN + "Hotbar is " + (open ? "open" : "closed"));
                }
                break;
            }
            case "close": {
                if (args.size() < 2) {
                    sender.sendMessage("Missing gui name");
                    return false;
                }
                String arg = args.get(1);
                Hotbar hotbar = registry.find(args.get(1), Hotbar.class);
                if (hotbar == null) {
                    sender.sendMessage(ChatColor.RED + "Hotbar " + arg + " not found");
                    return true;
                }

                hotbars().view(player).removeHotbar(hotbar);
                sender.sendMessage(ChatColor.GREEN + "Hotbar closed '" + arg + "'");
                break;
            }
        }
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, List<String> args) {
        return Collections.emptyList();
    }
}
