package xyz.upperlevel.uppercore.sound.command;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.command.function.AsCommand;
import xyz.upperlevel.uppercore.command.function.WithName;
import xyz.upperlevel.uppercore.command.function.WithOptional;
import xyz.upperlevel.uppercore.command.function.WithPermission;

@WithPermission(value = "playsound", description = "Allows you to play a sound from command", defaultUser = DefaultPermissionUser.OP)
public class PlaySoundCommand extends Command {
    public PlaySoundCommand() {
        super("playSound");
        setDescription("Plays the sound to the player (used for testing)");
    }

    @AsCommand(sender = SenderType.PLAYER)
    public void run(CommandSender sender, @WithName("sound")Sound sound, @WithOptional @WithName("volume")Float volume, @WithOptional @WithName("pitch")Float pitch) {
        Player player = (Player)sender;
        if(volume != null && pitch != null)
            player.playSound(player.getLocation(), sound, volume, pitch);
        else
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }
}
