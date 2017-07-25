package xyz.upperlevel.uppercore.util;

import org.bukkit.Sound;
import xyz.upperlevel.uppercore.Uppercore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.bukkit.Sound.valueOf;

public class CSound {
    public static final boolean OLD;
    private static Map<String, Sound> translator;

    public static Sound getRaw(String str) {
        return translator.get(str);
    }

    public static Sound get(String str) {
        return translator.get(str.replace(' ', '_').toUpperCase(Locale.ENGLISH));
    }

    //SETUP METHODS

    static {
        OLD = NmsVersion.MAJOR < 9;
        translator = new HashMap<>();
        if(OLD)
            setupOld();
        else
            setupNew();
    }

    /**
     * Any method call would trigger the static block<br>
     * This method is only a optional call for runtime optimization
     */
    public static void setup(){}

    private static void setupNew() {
        for(Sound s : Sound.values())
            translator.put(s.name(), s);
    }

    private static void r(String name, String sound) {
        translator.put(name, valueOf(sound));
    }

    //http://minecraft.gamepedia.com/Sounds.json
    private static void setupOld() {
        Uppercore.logger().warning("[Sound] Using translator (not every sound is supported!)");
        r("AMBIENT_CAVE", "AMBIENCE_CAVE");
        r("BLOCK_ANVIL_BREAK", "DIG_STONE");
        r("BLOCK_ANVIL_DESTROY", "ANVIL_BREAK");
        r("BLOCK_ANVIL_FALL", "STEP_STONE");
        r("BLOCK_ANVIL_HIT", "STEP_STONE");
        r("BLOCK_ANVIL_LAND", "ANVIL_LAND");
        r("BLOCK_ANVIL_PLACE", "ANVIL_LAND");
        r("BLOCK_ANVIL_STEP", "STEP_STONE");
        r("BLOCK_ANVIL_USE", "ANVIL_USE");
        //BLOCK_BREWING_STAND_BREW
        r("BLOCK_CHEST_CLOSE", "CHEST_CLOSE");
        r("BLOCK_CHEST_LOCKED", "DOOR_CLOSE");
        r("BLOCK_CHEST_OPEN", "CHEST_OPEN");
        //r("BLOCK_CHORUS_FLOWER_DEATH", "");
        //r("BLOCK_CHORUS_FLOWER_GROW", "");
        //r("BLOCK_CLOTH_BREAK", "");
        //r("BLOCK_CLOTH_FALL", "");
        //r("BLOCK_CLOTH_HIT", "");
        //r("BLOCK_CLOTH_PLACE", "");
        //r("BLOCK_CLOTH_STEP", "");
        r("BLOCK_COMPARATOR_CLICK", "CLICK");
        r("BLOCK_DISPENSER_DISPENSE", "CLICK");
        r("BLOCK_DISPENSER_FAIL", "CLICK");
        r("BLOCK_DISPENSER_LAUNCH", "SHOOT_ARROW");
        //r("BLOCK_ENCHANTMENT_TABLE_USE", "");
        //r("BLOCK_ENDERCHEST_CLOSE", "");
        //r("BLOCK_ENDERCHEST_OPEN", "");
        r("BLOCK_END_GATEWAY_SPAWN", "EXPLODE");
        //r("BLOCK_FENCE_GATE_CLOSE", "");
        //r("BLOCK_FENCE_GATE_OPEN", "");
        r("BLOCK_FIRE_AMBIENT", "FIRE");
        r("BLOCK_FIRE_EXTINGUISH", "FIZZ");
        //r("BLOCK_FURNACE_FIRE_CRACKLE", "");
        r("BLOCK_GLASS_BREAK", "GLASS");
        r("BLOCK_GLASS_FALL", "STEP_STONE");
        r("BLOCK_GLASS_HIT", "STEP_STONE");
        r("BLOCK_GLASS_PLACE", "DIG_STONE");
        r("BLOCK_GLASS_STEP", "STEP_STONE");
        r("BLOCK_GRASS_BREAK", "DIG_GRASS");
        r("BLOCK_GRASS_FALL", "STEP_GRASS");
        r("BLOCK_GRASS_HIT", "STEP_GRASS");
        r("BLOCK_GRASS_PLACE", "DIG_GRASS");
        r("BLOCK_GRASS_STEP", "STEP_GRASS");
        r("BLOCK_GRAVEL_BREAK", "DIG_GRAVEL");
        r("BLOCK_GRAVEL_FALL", "STEP_GRAVEL");
        r("BLOCK_GRAVEL_HIT", "STEP_GRAVEL");
        r("BLOCK_GRAVEL_PLACE", "DIG_GRAVEL");
        r("BLOCK_GRAVEL_STEP", "STEP_GRAVEL");
        r("BLOCK_IRON_DOOR_CLOSE", "DOOR_CLOSE");
        r("BLOCK_IRON_DOOR_OPEN", "DOOR_OPEN");
        //r("BLOCK_IRON_TRAPDOOR_CLOSE", "");
        //r("BLOCK_IRON_TRAPDOOR_OPEN", "");
        r("BLOCK_LADDER_BREAK", "DIG_WOOD");
        r("BLOCK_LADDER_FALL", "STEP_LADDER");
        r("BLOCK_LADDER_HIT", "STEP_LADDER");
        r("BLOCK_LADDER_PLACE", "DIG_WOOD");
        r("BLOCK_LADDER_STEP", "STEP_LADDER");
        r("BLOCK_LAVA_AMBIENT", "LAVA");
        r("BLOCK_LAVA_EXTINGUISH", "FIZZ");
        r("BLOCK_LAVA_POP", "LAVA_POP");
        r("BLOCK_LEVER_CLICK", "CLICK");
        r("BLOCK_METAL_BREAK", "DIG_STONE");
        r("BLOCK_METAL_FALL", "STEP_STONE");
        r("BLOCK_METAL_HIT", "STEP_STONE");
        r("BLOCK_METAL_PLACE", "DIG_STONE");
        r("BLOCK_METAL_PRESSUREPLATE_CLICK_OFF", "CLICK");
        r("BLOCK_METAL_PRESSUREPLATE_CLICK_ON", "CLICK");
        r("BLOCK_METAL_STEP", "STEP_STONE");
        r("BLOCK_NOTE_BASEDRUM", "NOTE_BASS_DRUM");
        r("BLOCK_NOTE_BASS", "NOTE_BASS");
        //t.put("BLOCK_NOTE_HARP", valueOf());
        //t.put("BLOCK_NOTE_HAT", valueOf());
        r("BLOCK_NOTE_PLING", "NOTE_PLING");
        r("BLOCK_NOTE_SNARE", "NOTE_SNARE_DRUM");
        r("BLOCK_PISTON_CONTRACT", "PISTON_RETRACT");
        r("BLOCK_PISTON_EXTEND", "PISTON_EXTEND");
        r("BLOCK_PORTAL_AMBIENT", "PORTAL");
        r("BLOCK_PORTAL_TRAVEL", "PORTAL_TRAVEL");
        r("BLOCK_PORTAL_TRIGGER", "PORTAL_TRIGGER");
        r("BLOCK_REDSTONE_TORCH_BURNOUT", "FIZZ");
        r("BLOCK_SAND_BREAK", "DIG_SAND");
        r("BLOCK_SAND_FALL", "STEP_SAND");
        r("BLOCK_SAND_HIT", "STEP_SAND");
        r("BLOCK_SAND_PLACE", "DIG_SAND");
        r("BLOCK_SAND_STEP", "STEP_SAND");
        //r("BLOCK_SHULKER_BOX_CLOSE", "");
        //r("BLOCK_SHULKER_BOX_OPEN", "");
        //r("BLOCK_SLIME_BREAK", "");
        //r("BLOCK_SLIME_FALL", "");
        //r("BLOCK_SLIME_HIT", "");
        //r("BLOCK_SLIME_PLACE", "");
        //r("BLOCK_SLIME_STEP", "");
        r("BLOCK_SNOW_BREAK", "DIG_SNOW");
        r("BLOCK_SNOW_FALL", "STEP_SNOW");
        r("BLOCK_SNOW_HIT", "STEP_SNOW");
        r("BLOCK_SNOW_PLACE", "DIG_SNOW");
        r("BLOCK_SNOW_STEP", "STEP_SNOW");
        r("BLOCK_STONE_BREAK", "DIG_STONE");
        r("BLOCK_STONE_BUTTON_CLICK_OFF", "CLICK");
        r("BLOCK_STONE_BUTTON_CLICK_ON", "CLICK");
        r("BLOCK_STONE_FALL", "STEP_STONE");
        r("BLOCK_STONE_HIT", "STEP_STONE");
        r("BLOCK_STONE_PLACE", "DIG_STONE");
        r("BLOCK_STONE_PRESSUREPLATE_CLICK_OFF", "CLICK");
        r("BLOCK_STONE_BUTTON_CLICK_ON", "CLICK");
        r("BLOCK_STONE_STEP", "STEP_STONE");
        r("BLOCK_TRIPWIRE_ATTACH", "CLICK");
        r("BLOCK_TRIPWIRE_CLICK_OFF", "CLICK");
        r("BLOCK_STONE_PRESSUREPLATE_CLICK_ON", "CLICK");
        r("BLOCK_STONE_STEP", "STEP_STONE");
        r("BLOCK_TRIPWIRE_ATTACH", "CLICK");
        r("BLOCK_TRIPWIRE_CLICK_OFF", "CLICK");
        r("BLOCK_TRIPWIRE_CLICK_ON", "CLICK");
        r("BLOCK_TRIPWIRE_CLICK_DETACH", "ARROW_HIT");
        //r("BLOCK_WATERLILY_PLACE", "");
        r("BLOCK_WATER_AMBIENT", "WATER");
        r("BLOCK_WOODEN_DOOR_CLOSE", "DOOR_CLOSE");
        r("BLOCK_WOODEN_DOOR_OPEN", "DOOR_OPEN");
        //r("BLOCK_WOODEN_TRAPDOOR_CLOSE", "");
        //r("BLOCK_WOODEN_TRAPDOOR_OPEN", "");
        r("BLOCK_WOOD_BREAK", "DIG_WOOD");
        r("BLOCK_WOOD_BUTTON_CLICK_OFF", "WOOD_CLICK");
        r("BLOCK_WOOD_BUTTON_CLICK_ON", "WOOD_CLICK");
        r("BLOCK_WOOD_FALL", "STEP_WOOD");
        r("BLOCK_WOOD_HIT", "STEP_WOOD");
        r("BLOCK_WOOD_PLACE", "DIG_WOOD");
        r("BLOCK_WOOD_PRESSUREPLATE_CLICK_OFF", "CLICK");
        r("BLOCK_WOOD_PRESSUREPLATE_CLICK_ON", "CLICK");
        r("BLOCK_WOOD_STEP", "STEP_WOOD");
        //r("ENCHANT_THORNS_HIT")
        //r("ENTITY_ARMORSTAND_BREAK")
        r("ENTITY_ARMORSTAND_FALL", "DIG_WOOD");
        //r("ENTITY_ARMORSTAND_HIT");
        r("ENTITY_ARMORSTAND_PLACE", "DIG_STONE");
        r("ENTITY_ARROW_HIT", "ARROW_HIT");
        r("ENTITY_ARROW_HIT_PLAYER", "SUCCESSFUL_HIT");
        r("ENTITY_ARROW_SHOOT", "SHOOT_ARROW");
        r("ENTITY_BAT_AMBIENT", "BAT_IDLE");
        r("ENTITY_BAT_DEATH", "BAT_DEATH");
        r("ENTITY_BAT_HURT", "BAT_HURT");
        r("ENTITY_BAT_LOOP", "BAT_LOOP");
        r("ENTITY_BAT_TAKEOFF", "BAT_TAKEOFF");
        r("ENTITY_BLAZE_AMBIENT", "");
        r("ENTITY_BLAZE_BURN", "BLAZE_BREATH");
        r("ENTITY_BLAZE_DEATH", "BLAZE_DEATH");
        r("ENTITY_BLAZE_HURT", "BLAZE_HIT");
        r("ENTITY_BLAZE_SHOOT", "GHAST_FIREBALL");
        r("ENTITY_BOBBER_SPLASH", "SPLASH2");
        //r("ENTITY_BOBBER_THROW", )
        r("ENTITY_CAT_AMBIENT", "CAT_MEOW");
        r("ENTITY_CAT_DEATH", "CAT_HIT");
        r("ENTITY_CAT_HISS", "CAT_HISS");
        r("ENTITY_CAT_HURT", "CAT_HIT");
        r("ENTITY_CAT_PURR", "CAT_PURR");
        r("ENTITY_CAT_PURREOW", "CAT_PURREOW");
        r("ENTITY_CHICKEN_AMBIENT", "CHICKEN_IDLE");
        r("ENTITY_CHICKEN_DEATH", "CHICKEN_HURT");
        r("ENTITY_CHICKEN_EGG", "CHICKEN_EGG_POP");
        r("ENTITY_CHICKEN_HURT", "CHICKEN_HURT");
        r("ENTITY_CHICKEN_STEP", "CHICKEN_WALK");
        r("ENTITY_COW_AMBIENT", "COW_IDLE");
        r("ENTITY_COW_DEATH", "COW_HURT");
        r("ENTITY_COW_HURT", "COW_HURT");
        //r("ENTITY_COW_MILK", "");
        r("ENTITY_COW_STEP", "COW_WALK");
        r("ENTITY_CREEPER_DEATH", "CREEPER_DEATH");
        r("ENTITY_CREEPER_HURT", "CREEPER_HISS");
        r("ENTITY_CREEPER_PRIMED", "FUSE");
        r("ENTITY_DONKEY_AMBIENT", "DONKEY_IDLE");
        r("ENTITY_DONKEY_ANGRY", "DONKEY_ANGRY");
        r("ENTITY_DONKEY_CHEST", "CHICKEN_EGG_POP");
        r("ENTITY_DONKEY_DEATH", "DONKEY_DEATH");
        r("ENTITY_DONKEY_HURT", "DONKEY_HIT");
        r("ENTITY_EGG_THROW", "SHOOT_BOW");
        //r("ENTITY_ELDER_GUARDIAN_AMBIENT", "");
        //r("ENTITY_ELDER_GUARDIAN_AMBIENT_LAND", "");
        //r("ENTITY_ELDER_GUARDIAN_CURSE", "");
        //r("ENTITY_ELDER_GUARDIAN_DEATH", "");
        //r("ENTITY_ELDER_GUARDIAN_DEATH_LAND", "");
        //r("ENTITY_ELDER_GUARDIAN_FLOP", "");
        //r("ENTITY_ELDER_GUARDIAN_HURT", "");
        //r("ENTITY_ELDER_GUARDIAN_HURT_LAND", "");
        r("ENTITY_ENDERDRAGON_AMBIENT", "ENDERDRAGON_GROWL");
        r("ENTITY_ENDERDRAGON_DEATH", "ENDERDRAGON_DEATH");
        r("ENTITY_ENDERDRAGON_FIREBALL_EXPLODE", "EXPLODE");
        r("ENTITY_ENDERDRAGON_FLAP", "ENDERDRAGON_WINGS");
        r("ENTITY_ENDERDRAGON_GROWL", "ENDERDRAGON_GROWL");
        r("ENTITY_ENDERDRAGON_HURT", "ENDERDRAGON_HIT");
        r("ENTITY_ENDERDRAGON_SHOOT", "GHAST_FIREBALL");
        //r("ENTITY_ENDEREYE_LAUNCH", "");
        r("ENTITY_ENDERMEN_AMBIENT", "ENDERMAN_IDLE");
        r("ENTITY_ENDERMEN_DEATH", "ENDERMAN_DEATH");
        r("ENTITY_ENDERMEN_HURT", "ENDERMAN_HIT");
        r("ENTITY_ENDERMEN_SCREAM", "ENDERMAN_SCREAM");
        r("ENTITY_ENDERMEN_STARE", "ENDERMAN_STARE");
        r("ENTITY_ENDERMEN_TELEPORT", "ENDERMAN_TELEPORT");
        r("ENTITY_ENDERMITE_AMBIENT", "SILVERFISH_IDLE");
        r("ENTITY_ENDERMITE_DEATH", "SILVERFISH_IDLE");
        r("ENTITY_ENDERMITE_HURT", "SILVERFISH_IDLE");
        r("ENTITY_ENDERMITE_STEP", "SILVERFISH_IDLE");
        r("ENTITY_ENDERPEARL_THROW", "SHOOT_ARROW");
        //r("ENTITY_EVOCATION_FANGS_ATTACK", "");
        //r("ENTITY_EVOCATION_ILLAGER_AMBIENT", "");
        //r("ENTITY_EVOCATION_ILLAGER_CAST_SPELL", "");
        //r("ENTITY_EVOCATION_ILLAGER_DEATH", "");
        //r("ENTITY_EVOCATION_ILLAGER_HURT", "");
        //r("ENTITY_EVOCATION_ILLAGER_PREPARE_ATTACK", "");
        //r("ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON", "");
        //r("ENTITY_EVOCATION_ILLAGER_PREPARE_WOLOLO", "");
        r("ENTITY_EXPERIENCE_BOTTLE_THROW", "SHOOT_ARROW");
        r("ENTITY_EXPERIENCE_ORB_PICKUP", "ORB_PICKUP");
        r("ENTITY_FIREWORK_BLAST", "FIREWORK_BLAST");
        r("ENTITY_FIREWORK_BLAST_FAR", "FIREWORK_BLAST2");
        r("ENTITY_FIREWORK_LARGE_BLAST", "FIREWORK_LARGE_BLAST");
        r("ENTITY_FIREWORK_LARGE_BLAST_FAR", "FIREWORK_LARGE_BLAST2");
        r("ENTITY_FIREWORK_LAUNCH", "FIREWORK_LAUNCH");
        r("ENTITY_FIREWORK_SHOOT", "SHOOT_BOW");
        r("ENTITY_FIREWORK_TWINKLE", "FIREWORK_TWINKLE");
        r("ENTITY_FIREWORK_TWINKLE_FAR", "FIREWORK_TWINKLE2");
        r("ENTITY_GENERIC_BIG_FALL", "FALL_BIG");
        r("ENTITY_GENERIC_BURN", "FIZZ");
        r("ENTITY_GENERIC_DEATH", "HURT_FLESH");
        r("ENTITY_GENERIC_DRINK", "DRINK");
        r("ENTITY_GENERIC_EAT", "EAT");
        r("ENTITY_GENERIC_EXPLODE", "EXPLODE");
        r("ENTITY_GENERIC_EXTINGUISH_FIRE", "FIZZ");
        r("ENTITY_GENERIC_HURT", "HURT_FLESH");
        r("ENTITY_GENERIC_SMALL_FALL", "FALL_SMALL");
        r("ENTITY_GENERIC_SPLASH", "SPLASH");
        r("ENTITY_GENERIC_SWIM", "SWIM");
        r("ENTITY_GHAST_AMBIENT", "GHAST_MOAN");
        r("ENTITY_GHAST_DEATH", "GHAST_DEATH");
        r("ENTITY_GHAST_HURT", "GHAST_SCREAM2");
        r("ENTITY_GHAST_SCREAM", "GHAST_SCREAM");
        r("ENTITY_GHAST_SHOOT", "GHAST_FIREBALL");
        r("ENTITY_GHAST_WARN", "GHAST_CHARGE");
        //r("ENTITY_GUARDIAN_AMBIENT", "");
        //r("ENTITY_GUARDIAN_AMBIENT_LAND", "");
        //r("ENTITY_GUARDIAN_ATTACK", "");
        //r("ENTITY_GUARDIAN_DEATH", "");
        //r("ENTITY_GUARDIAN_DEATH_LAND", "");
        //r("ENTITY_GUARDIAN_FLOP", "");
        //r("ENTITY_GUARDIAN_HURT", "");
        //r("ENTITY_GUARDIAN_HURT_LAND", "");
        r("ENTITY_HORSE_AMBIENT", "HORSE_IDLE");
        r("ENTITY_HORSE_ANGRY", "HORSE_ANGRY");
        r("ENTITY_HORSE_ARMOR", "HORSE_ARMOR");
        r("ENTITY_HORSE_BREATHE", "HORSE_BREATHE");
        r("ENTITY_HORSE_DEATH", "HORSE_DEATH");
        //r("ENTITY_HORSE_EAT", "");
        r("ENTITY_HORSE_GALLOP", "HORSE_GALLOP");
        r("ENTITY_HORSE_HURT", "HORSE_HIT");
        r("ENTITY_HORSE_JUMP", "HORSE_JUMP");
        r("ENTITY_HORSE_LAND", "HORSE_LAND");
        r("ENTITY_HORSE_SADDLE", "HORSE_SADDLE");
        r("ENTITY_HORSE_STEP", "HORSE_SOFT");
        r("ENTITY_HORSE_STEP_WOOD", "HORSE_WOOD");
        r("ENTITY_HOSTILE_BIG_FALL", "FALL_BIG");
        r("ENTITY_HOSTILE_DEATH", "HURT_FLESH");
        r("ENTITY_HOSTILE_HURT", "HURT_FLESH");
        r("ENTITY_HOSTILE_SMALL_FALL", "FALL_SMALL");
        r("ENTITY_HOSTILE_SPLASH", "SPLASH");
        r("ENTITY_HOSTILE_SWIM", "SWIM");
        //r("ENTITY_HUSK_AMBIENT", "");
        //r("ENTITY_HUSK_DEATH", "");
        //r("ENTITY_HUSK_HURT", "");
        //r("ENTITY_HUSK_STEP", "");
        r("ENTITY_IRONGOLEM_ATTACK", "IRONGOLEM_THROW");
        r("ENTITY_IRONGOLEM_DEATH", "IRONGOLEM_DEATH");
        r("ENTITY_IRONGOLEM_HURT", "IRONGOLEM_HIT");
        r("ENTITY_IRONGOLEM_STEP", "IRONGOLEM_WALK");
        //r("ENTITY_ITEMFRAME_ADD_ITEM", "");
        //r("ENTITY_ITEMFRAME_BREAK", "");
        //r("ENTITY_ITEMFRAME_PLACE", "");
        //r("ENTITY_ITEMFRAME_REMOVE_ITEM", "");
        //r("ENTITY_ITEMFRAME_ROTATE_ITEM", "");
        r("ENTITY_ITEM_BREAK", "ITEM_BREAK");
        r("ENTITY_ITEM_PICKUP", "ITEM_PICKUP");
        //r("ENTITY_LEASHKNOT_BREAK", "");
        //r("ENTITY_LEASHKNOT_PLACE", "");
        r("ENTITY_LIGHTNING_IMPACT", "EXPLODE");
        r("ENTITY_LIGHTNING_THUNDER", "AMBIENCE_THUNDER");
        r("ENTITY_LINGERINGPOTION_THROW", "SHOOT_ARROW");
        //r("ENTITY_LLAMA_AMBIENT", "");
        //r("ENTITY_LLAMA_ANGRY", "");
        r("ENTITY_LLAMA_CHEST", "CHICKEN_EGG_POP");
        //r("ENTITY_LLAMA_DEATH", "");
        //r("ENTITY_LLAMA_EAT", "");
        //r("ENTITY_LLAMA_HURT", "");
        //r("ENTITY_LLAMA_SPIT", "");
        //r("ENTITY_LLAMA_STEP", "");
        //r("ENTITY_LLAMA_SWAG", "");
        r("ENTITY_MAGMACUBE_DEATH", "SLIME_WALK2");
        r("ENTITY_MAGMACUBE_HURT", "SLIME_WALK2");
        r("ENTITY_MAGMACUBE_JUMP", "MAGMACUBE_JUMP");
        r("ENTITY_MAGMACUBE_SQUISH", "MAGMACUBE_WALK");
        r("ENTITY_MINECART_INSIDE", "MINECART_INSIDE");
        r("ENTITY_MINECART_RIDING", "MINECART_BASE");
        r("ENTITY_MOOSHROOM_SHEAR", "");
        r("ENTITY_MULE_AMBIENT", "DONKEY_IDLE");
        r("ENTITY_MULE_CHEST", "CHICKEN_EGG_POP");
        r("ENTITY_MULE_DEATH", "DONKEY_DEATH");
        r("ENTITY_MULE_HURT", "DONKEY_HIT");
        //r("ENTITY_PAINTING_BREAK", "");
        //r("ENTITY_PAINTING_PLACE", "");
        r("ENTITY_PIG_AMBIENT", "PIG_IDLE");
        r("ENTITY_PIG_DEATH", "PIG_DEATH");
        r("ENTITY_PIG_HURT", "PIG_IDLE");
        r("ENTITY_PIG_SADDLE", "HORSE_SADDLE");
        r("ENTITY_PIG_STEP", "PIG_WALK");
        //r("ENTITY_PLAYER_ATTACK_CRIT", "");
        //r("ENTITY_PLAYER_ATTACK_KNOCKBACK", "");
        //r("ENTITY_PLAYER_ATTACK_NODAMAGE", "");
        r("ENTITY_PLAYER_ATTACK_STRONG", "SUCCESSFUL_HIT");
        //r("ENTITY_PLAYER_ATTACK_SWEEP", "");
        //r("ENTITY_PLAYER_ATTACK_WEAK", "");
        r("ENTITY_PLAYER_BIG_FALL", "FALL_BIG");
        //r("ENTITY_PLAYER_BREATH", "");
        r("ENTITY_PLAYER_BURP", "BURP");
        r("ENTITY_PLAYER_DEATH", "HURT_FLESH");
        r("ENTITY_PLAYER_HURT", "HURT_FLESH");
        r("ENTITY_PLAYER_LEVELUP", "LEVEL_UP");
        r("ENTITY_PLAYER_SMALL_FALL", "FALL_SMALL");
        r("ENTITY_PLAYER_SPLASH", "SPLASH");
        r("ENTITY_PLAYER_SWIM", "SWIM");
        //r("ENTITY_POLAR_BEAR_AMBIENT", "");
        //r("ENTITY_POLAR_BEAR_BABY_AMBIENT", "");
        //r("ENTITY_POLAR_BEAR_DEATH", "");
        //r("ENTITY_POLAR_BEAR_HURT", "");
        //r("ENTITY_POLAR_BEAR_STEP", "");
        //r("ENTITY_POLAR_BEAR_WARNING", "");
        //r("ENTITY_RABBIT_AMBIENT", "");
        //r("ENTITY_RABBIT_ATTACK", "");
        //r("ENTITY_RABBIT_DEATH", "");
        //r("ENTITY_RABBIT_HURT", "");
        //r("ENTITY_RABBIT_JUMP", "");
        r("ENTITY_SHEEP_AMBIENT", "SHEEP_IDLE");
        r("ENTITY_SHEEP_DEATH", "SHEEP_IDLE");
        r("ENTITY_SHEEP_HURT", "SHEEP_IDLE");
        r("ENTITY_SHEEP_SHEAR", "SHEEP_SHEAR");
        r("ENTITY_SHEEP_STEP", "SHEEP_WALK");
        //r("ENTITY_SHULKER_AMBIENT", "");
        //r("ENTITY_SHULKER_BULLET_HIT", "");
        //r("ENTITY_SHULKER_BULLET_HURT", "");
        //r("ENTITY_SHULKER_CLOSE", "");
        //r("ENTITY_SHULKER_DEATH", "");
        //r("ENTITY_SHULKER_HURT", "");
        //r("ENTITY_SHULKER_HURT_CLOSED", "");
        //r("ENTITY_SHULKER_OPEN", "");
        //r("ENTITY_SHULKER_SHOOT", "");
        //r("ENTITY_SHULKER_TELEPORT", "");
        r("ENTITY_SILVERFISH_AMBIENT", "SILVERFISH_IDLE");
        r("ENTITY_SILVERFISH_DEATH", "SILVERFISH_KILL");
        r("ENTITY_SILVERFISH_HURT", "SILVERFISH_HIT");
        r("ENTITY_SILVERFISH_STEP", "SILVERFISH_WALK");
        r("ENTITY_SKELETON_AMBIENT", "SKELETON_IDLE");
        r("ENTITY_SKELETON_DEATH", "SKELETON_DEATH");
        r("ENTITY_SKELETON_HORSE_AMBIENT", "");
        r("ENTITY_SKELETON_HORSE_DEATH", "");
        r("ENTITY_SKELETON_HORSE_HURT", "");
        r("ENTITY_SKELETON_HURT", "SKELETON_HURT");
        r("ENTITY_SKELETON_SHOOT", "SHOOT_ARROW");
        r("ENTITY_SKELETON_STEP", "SKELETON_WALK");
        r("ENTITY_SLIME_ATTACK", "SLIME_ATTACK");
        r("ENTITY_SLIME_DEATH", "SLIME_WALK2");
        r("ENTITY_SLIME_HURT", "SLIME_WALK2");
        r("ENTITY_SLIME_JUMP", "SLIME_WALK2");
        r("ENTITY_SLIME_SQUISH", "");
        r("ENTITY_SMALL_MAGMACUBE_DEATH", "SLIME_WALK");
        r("ENTITY_SMALL_MAGMACUBE_HURT", "SLIME_WALK");
        r("ENTITY_SMALL_MAGMACUBE_SQUISH", "SLIME_WALK");
        r("ENTITY_SMALL_SLIME_DEATH", "SLIME_WALK");
        r("ENTITY_SMALL_SLIME_HURT", "SLIME_WALK");
        r("ENTITY_SMALL_SLIME_JUMP", "SLIME_WALK");
        r("ENTITY_SMALL_SLIME_SQUISH", "SLIME_WALK");
        r("ENTITY_SNOWBALL_THROW", "SHOOT_ARROW");
        r("ENTITY_SNOWMAN_AMBIENT", "");
        //r("ENTITY_SNOWMAN_DEATH", "");
        //r("ENTITY_SNOWMAN_HURT", "");
        r("ENTITY_SNOWMAN_SHOOT", "SHOOT_ARROW");
        r("ENTITY_SPIDER_AMBIENT", "SPIDER_IDLE");
        r("ENTITY_SPIDER_DEATH", "SPIDER_DEATH");
        r("ENTITY_SPIDER_HURT", "SPIDER_IDLE");
        r("ENTITY_SPIDER_STEP", "SPIDER_WALK");
        r("ENTITY_SPLASH_POTION_BREAK", "GLASS");
        r("ENTITY_SPLASH_POTION_THROW", "SHOOT_ARROW");
        //r("ENTITY_SQUID_AMBIENT", "");
        //r("ENTITY_SQUID_DEATH", "");
        //r("ENTITY_SQUID_HURT", "");
        //r("ENTITY_STRAY_AMBIENT", "");
        //r("ENTITY_STRAY_DEATH", "");
        //r("ENTITY_STRAY_HURT", "");
        //r("ENTITY_STRAY_STEP", "");
        r("ENTITY_TNT_PRIMED", "FUSE");
        //r("ENTITY_VEX_AMBIENT", "");
        //r("ENTITY_VEX_CHARGE", "");
        //r("ENTITY_VEX_DEATH", "");
        //r("ENTITY_VEX_HURT", "");
        r("ENTITY_VILLAGER_AMBIENT", "VILLAGER_IDLE");
        r("ENTITY_VILLAGER_DEATH", "VILLAGER_DEATH");
        r("ENTITY_VILLAGER_HURT", "VILLAGER_HIT");
        r("ENTITY_VILLAGER_NO", "VILLAGER_NO");
        r("ENTITY_VILLAGER_TRADING", "VILLAGER_HAGGLE");
        r("ENTITY_VILLAGER_YES", "VILLAGER_YES");
        //r("ENTITY_VINDICATION_ILLAGER_AMBIENT", "");
        //r("ENTITY_VINDICATION_ILLAGER_DEATH", "");
        //r("ENTITY_VINDICATION_ILLAGER_HURT", "");
        //r("ENTITY_WITCH_AMBIENT", "");
        //r("ENTITY_WITCH_DEATH", "");
        //r("ENTITY_WITCH_DRINK", "");
        //r("ENTITY_WITCH_HURT", "");
        //r("ENTITY_WITCH_THROW", "");
        r("ENTITY_WITHER_AMBIENT", "WITHER_IDLE");
        r("ENTITY_WITHER_BREAK_BLOCK", "ZOMBIE_WOODBREAK");
        r("ENTITY_WITHER_DEATH", "WITHER_DEATH");
        r("ENTITY_WITHER_HURT", "WITHER_HURT");
        r("ENTITY_WITHER_SHOOT", "WITHER_SHOOT");
        //r("ENTITY_WITHER_SKELETON_AMBIENT", "");
        //r("ENTITY_WITHER_SKELETON_DEATH", "");
        //r("ENTITY_WITHER_SKELETON_HURT", "");
        //r("ENTITY_WITHER_SKELETON_STEP", "");
        //r("ENTITY_WITHER_SPAWN", "");
        r("ENTITY_WOLF_AMBIENT", "WOLF_BARK");
        r("ENTITY_WOLF_DEATH", "WOLF_DEATH");
        r("ENTITY_WOLF_GROWL", "WOLF_GROWL");
        r("ENTITY_WOLF_HOWL", "WOLF_HOWL");
        r("ENTITY_WOLF_HURT", "WOLF_HURT");
        r("ENTITY_WOLF_PANT", "WOLF_PANT");
        r("ENTITY_WOLF_SHAKE", "WOLF_SHAKE");
        r("ENTITY_WOLF_STEP", "WOLF_WALK");
        r("ENTITY_WOLF_WHINE", "WOLF_WHINE");
        r("ENTITY_ZOMBIE_AMBIENT", "ZOMBIE_IDLE");
        r("ENTITY_ZOMBIE_ATTACK_DOOR_WOOD", "ZOMBIE_WOOD");
        r("ENTITY_ZOMBIE_ATTACK_IRON_DOOR", "ZOMBIE_METAL");
        r("ENTITY_ZOMBIE_BREAK_DOOR_WOOD", "ZOMBIE_WOODBREAK");
        r("ENTITY_ZOMBIE_DEATH", "ZOMBIE_DEATH");
        r("ENTITY_ZOMBIE_HORSE_AMBIENT", "HORSE_ZOMBIE_IDLE");
        r("ENTITY_ZOMBIE_HORSE_DEATH", "HORSE_ZOMBIE_DEATH");
        r("ENTITY_ZOMBIE_HORSE_HURT", "HORSE_ZOMBIE_HIT");
        r("ENTITY_ZOMBIE_HURT", "ZOMBIE_HURT");
        r("ENTITY_ZOMBIE_INFECT", "ZOMBIE_INFECT");
        r("ENTITY_ZOMBIE_PIG_AMBIENT", "ZOMBIE_PIG_IDLE");
        r("ENTITY_ZOMBIE_PIG_ANGRY", "ZOMBIE_PIG_ANGRY");
        r("ENTITY_ZOMBIE_PIG_DEATH", "ZOMBIE_PIG_DEATH");
        r("ENTITY_ZOMBIE_PIG_HURT", "ZOMBIE_PIG_HURT");
        r("ENTITY_ZOMBIE_STEP", "ZOMBIE_WALK");
        r("ENTITY_ZOMBIE_VILLAGER_AMBIENT", "");
        r("ENTITY_ZOMBIE_VILLAGER_CONVERTED", "ZOMBIE_UNFECT");
        r("ENTITY_ZOMBIE_VILLAGER_CURE", "ZOMBIE_REMEDY");
        r("ENTITY_ZOMBIE_VILLAGER_DEATH", "");
        r("ENTITY_ZOMBIE_VILLAGER_HURT", "");
        r("ENTITY_ZOMBIE_VILLAGER_STEP", "");
        //r("ITEM_ARMOR_EQUIP_CHAIN", "");
        //r("ITEM_ARMOR_EQUIP_DIAMOND", "");
        //r("ITEM_ARMOR_EQUIP_ELYTRA", "");
        //r("ITEM_ARMOR_EQUIP_GENERIC", "");
        //r("ITEM_ARMOR_EQUIP_GOLD", "");
        //r("ITEM_ARMOR_EQUIP_IRON", "");
        //r("ITEM_ARMOR_EQUIP_LEATHER", "");
        //r("ITEM_BOTTLE_EMPTY", "");
        //r("ITEM_BOTTLE_FILL", "");
        //r("ITEM_BOTTLE_FILL_DRAGONBREATH", "");
        //r("ITEM_BUCKET_EMPTY", "");
        //r("ITEM_BUCKET_EMPTY_LAVA", "");
        //r("ITEM_BUCKET_FILL", "");
        //r("ITEM_BUCKET_FILL_LAVA", "");
        //r("ITEM_CHORUS_FRUIT_TELEPORT", "");
        //r("ITEM_ELYTRA_FLYING", "");
        r("ITEM_FIRECHARGE_USE", "GHAST_FIREBALL");
        r("ITEM_FLINTANDSTEEL_USE", "FIRE_IGNITE");
        //r("ITEM_HOE_TILL", "");
        //r("ITEM_SHIELD_BLOCK", "");
        r("ITEM_SHIELD_BREAK", "ITEM_BREAK");
        //r("ITEM_SHOVEL_FLATTEN", "");
        //r("ITEM_TOTEM_USE", "");
        //r("MUSIC_CREATIVE", "");
        //r("MUSIC_CREDITS", "");
        //r("MUSIC_DRAGON", "");
        //r("MUSIC_END", "");
        //r("MUSIC_GAME", "");
        //r("MUSIC_MENU", "");
        //r("MUSIC_NETHER", "");
        //r("RECORD_11", "");
        //r("RECORD_13", "");
        //r("RECORD_BLOCKS", "");
        //r("RECORD_CAT", "");
        //r("RECORD_CHIRP", "");
        //r("RECORD_FAR", "");
        //r("RECORD_MALL", "");
        //r("RECORD_MELLOHI", "");
        //r("RECORD_STAL", "");
        //r("RECORD_STRAD", "");
        //r("RECORD_WAIT", "");
        //r("RECORD_WARD", "");
        r("UI_BUTTON_CLICK", "CLICK");
        r("WEATHER_RAIN", "AMBIENCE_RAIN");
        //r("WEATHER_RAIN_ABOVE", "");
    }


}
