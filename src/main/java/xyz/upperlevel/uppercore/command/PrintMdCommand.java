package xyz.upperlevel.uppercore.command;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.WithPermission;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class PrintMdCommand {
    private final Command rootCmd;

    public PrintMdCommand(Command rootCmd) {
        this.rootCmd = rootCmd;
    }

    private void printMd0(Writer writer, List<Command> commands) throws IOException {
        for (Command command : commands) {
            writer.write(String.format(
                    "%s | %s | %s | %s | %s\n",
                    command.getFullName(),
                    command.getUsage(null, false),
                    command.getPermission().getName(),
                    command.getPermission().getDefault().name(),
                    command.getSenderType().name()
            ));

            if (command instanceof NodeCommand)
                printMd0(writer, ((NodeCommand) command).getCommands());
        }
    }

    public void printMd(Writer writer, List<Command> commands) throws IOException {
        writer.write("Name | Usage | Permission | Permission default | Sender type\n");
        writer.write("---- | ----- | ---------- | ------------------ | -----------\n");

        printMd0(writer, commands);

        writer.flush();
    }

    @AsCommand(
            description = "Prints a .md file containing the registered commands tree."
    )
    @WithPermission
    public void printCommandsMd(CommandSender sender) {
        File f = new File(Uppercore.getPlugin().getDataFolder(), "commands.md");
        try {
            FileWriter writer = new FileWriter(f);
            printMd(writer, Collections.singletonList(rootCmd));
            writer.close();

            sender.sendMessage(GREEN + "Commands tree saved at: " + f.getPath());

        } catch (IOException e) {
            sender.sendMessage(RED + "Error during file writing: " + f.getPath());
        }
    }
}
