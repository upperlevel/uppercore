package xyz.upperlevel.uppercore.sound.command;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.*;

@WithPermission(value = "playsound", desc = "Allows you to play a sound from command", def = DefaultPermission.OP)
public class PlaySoundCommand extends Command {
    public PlaySoundCommand() {
        super("playSound");
        setDescription("Plays the sound to the player (used for testing)");
    }

    @Executor(sender = Sender.PLAYER)
    public void run(CommandSender sender, @Argument("sound")Sound sound, @Optional @Argument("volume")Float volume, @Optional @Argument("pitch")Float pitch) {
        Player player = (Player)sender;
        if(volume != null && pitch != null)
            player.playSound(player.getLocation(), sound, volume, pitch);
        else
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }
}
