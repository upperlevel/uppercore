package xyz.upperlevel.uppercoretest.particle;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.SenderType;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.command.functional.WithOptional;
import xyz.upperlevel.uppercore.command.functional.WithSender;
import xyz.upperlevel.uppercore.particle.impl.BlockDustParticle;
import xyz.upperlevel.uppercore.particle.impl.SimpleParticle;

public class ParticleCommand extends NodeCommand {
    public ParticleCommand() {
        super("testparticle");
        append(FunctionalCommand.load(this));
    }

    @AsCommand
    @WithSender(SenderType.PLAYER)
    public void simple(CommandSender sender, Color color) {
        Player player = (Player) sender;
        SimpleParticle particle = new SimpleParticle();
        particle.setColor(color);
        particle.display(player.getLocation().add(0, 3, 0), ImmutableList.of(player));
    }

    @AsCommand
    @WithSender(SenderType.PLAYER)
    public void blockdust(CommandSender sender, Material material, @WithOptional("0") byte data) {
        Player player = (Player) sender;

        if (!material.isBlock()) {
            player.sendMessage(ChatColor.RED + "Material not block!");
            return;
        }

        BlockDustParticle particle = new BlockDustParticle();
        particle.setBlockType(material);
        particle.setBlockData(data);
        particle.display(player.getLocation().add(0, 3, 0), ImmutableList.of(player));
    }
}
