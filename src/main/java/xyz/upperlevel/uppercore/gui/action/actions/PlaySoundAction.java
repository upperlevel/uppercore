package xyz.upperlevel.uppercore.gui.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.CurrentPlugin;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;

public class PlaySoundAction extends Action<PlaySoundAction> {
    public static final SoundActionType TYPE = new SoundActionType();

    @Getter
    private final Sound sound;
    @Getter
    private final PlaceholderValue<Float> volume, pitch;


    public PlaySoundAction(
            @CurrentPlugin Plugin plugin,
            @ConfigProperty("sound") Sound sound,
            @ConfigProperty(value = "volume", optional = true) PlaceholderValue<Float> volume,
            @ConfigProperty(value = "pitch", optional = true) PlaceholderValue<Float> pitch
    ) {
        super(plugin, TYPE);
        this.sound = sound;
        this.volume = volume != null ? volume : PlaceholderValue.fake(1.0f);
        this.pitch = pitch != null ? volume : PlaceholderValue.fake(1.0f);
    }

    @Override
    public void run(Player player) {
        player.playSound(player.getLocation(), sound, volume.resolve(player), pitch.resolve(player));
    }


    public static class SoundActionType extends BaseActionType<PlaySoundAction> {

        public SoundActionType() {
            super(PlaySoundAction.class, "play-sound");
            setParameters(
                    Parameter.of("sound", Parser.soundValue(), true),
                    Parameter.of("volume", Parser.strValue(), "1.0", false),
                    Parameter.of("pitch", Parser.strValue(), "1.0", false)
            );
        }

        @Override
        public PlaySoundAction create(Plugin plugin, Map<String, Object> pars) {
            return new PlaySoundAction(
                    plugin,
                    (Sound) pars.get("sound"),
                    PlaceholderUtil.parseFloat(pars.get("volume")),
                    PlaceholderUtil.parseFloat(pars.get("pitch"))
            );
        }

        @Override
        public Map<String, Object> read(PlaySoundAction action) {
            return ImmutableMap.of(
                    "id", action.sound,
                    "volume", action.volume.toString(),
                    "pitch", action.pitch.toString()
            );
        }
    }
}
