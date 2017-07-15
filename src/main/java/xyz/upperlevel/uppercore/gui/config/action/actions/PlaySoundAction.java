package xyz.upperlevel.uppercore.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.config.action.Action;
import xyz.upperlevel.uppercore.gui.config.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.config.action.Parser;
import xyz.upperlevel.uppercore.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.uppercore.gui.config.placeholders.PlaceholderValue;

import java.util.Map;

public class PlaySoundAction extends Action<PlaySoundAction> {
    public static final SoundActionType TYPE = new SoundActionType();
    @Getter
    private final Sound sound;
    @Getter
    private final PlaceholderValue<Float> volume, pitch;


    public PlaySoundAction(Sound sound, PlaceholderValue<Float> volume, PlaceholderValue<Float> pitch) {
        super(TYPE);
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void run(Player player) {
        player.playSound(player.getLocation(), sound, volume.get(player), pitch.get(player));
    }


    public static class SoundActionType extends BaseActionType<PlaySoundAction> {

        public SoundActionType() {
            super("play-sound");
            setParameters(
                    Parameter.of("sound", Parser.soundValue(), true),
                    Parameter.of("volume", Parser.strValue(), "1.0", false),
                    Parameter.of("pitch", Parser.strValue(), "1.0", false)
            );
        }

        @Override
        public PlaySoundAction create(Map<String, Object> pars) {
            return new PlaySoundAction(
                    (Sound) pars.get("sound"),
                    PlaceHolderUtil.parseFloat(pars.get("volume")),
                    PlaceHolderUtil.parseFloat(pars.get("pitch"))
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
