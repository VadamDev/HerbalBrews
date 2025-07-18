package net.satisfy.herbalbrews.forge.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.satisfy.herbalbrews.HerbalBrews;

@EventBusSubscriber(modid = HerbalBrews.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class HerbalBrewsNeoForgeConfig {
    public static final ModConfigSpec COMMON_CONFIG;

    public static final ModConfigSpec.BooleanValue ITEMS_BANNER_GIVE_EFFECT;
    public static final ModConfigSpec.BooleanValue ITEMS_BANNER_SHOW_TOOLTIP;
    public static final ModConfigSpec.BooleanValue ITEMS_HAT_DAMAGE_REDUCTION_ENABLED;
    public static final ModConfigSpec.IntValue ITEMS_HAT_DAMAGE_REDUCTION_AMOUNT;
    
    public static final ModConfigSpec.IntValue BLOCKS_DRYING_DURATION;
    public static final ModConfigSpec.IntValue BLOCKS_BREWING_DURATION;
    public static final ModConfigSpec.IntValue BLOCKS_JUG_EFFECT_DURATION;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("items");

        builder.push("banner");
        ITEMS_BANNER_GIVE_EFFECT = builder
                .comment("Enable or disable the effect granted by banners.")
                .define("giveEffect", true);
        ITEMS_BANNER_SHOW_TOOLTIP = builder
                .comment("Enable or disable the tooltip display for banners.")
                .define("showTooltip", true);
        builder.pop();

        builder.push("hat");
        ITEMS_HAT_DAMAGE_REDUCTION_ENABLED = builder
                .comment("Enable or disable magic damage reduction provided by the hat.")
                .define("damageReductionEnabled", true);
        ITEMS_HAT_DAMAGE_REDUCTION_AMOUNT = builder
                .comment("Percentage of magic damage reduction provided by the hat (0-100).")
                .defineInRange("damageReductionAmount", 40, 0, 100);
        builder.pop();

        builder.pop();

        builder.push("blocks");
        BLOCKS_DRYING_DURATION = builder
                .comment("Duration of the drying process in ticks. (20 ticks = 1 second)")
                .defineInRange("dryingDuration", 900, 0, Integer.MAX_VALUE);
        BLOCKS_BREWING_DURATION = builder
                .comment("Duration of the brewing process in ticks. (20 ticks = 1 second)")
                .defineInRange("brewingDuration", 1200, 0, Integer.MAX_VALUE);
        BLOCKS_JUG_EFFECT_DURATION = builder
                .comment("Duration of the jug's effect in ticks. (20 ticks = 1 second)")
                .defineInRange("jugEffectDuration", 900, 0, Integer.MAX_VALUE);
        builder.pop();

        COMMON_CONFIG = builder.build();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
    }

    public static boolean itemsBannerGiveEffect;
    public static boolean itemsBannerShowTooltip;
    public static boolean itemsHatDamageReductionEnabled;
    public static int itemsHatDamageReductionAmount;

    public static int blocksDryingDuration;
    public static int blocksBrewingDuration;
    public static int blocksJugEffectDuration;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        itemsBannerGiveEffect = ITEMS_BANNER_GIVE_EFFECT.get();
        itemsBannerShowTooltip = ITEMS_BANNER_SHOW_TOOLTIP.get();
        itemsHatDamageReductionEnabled = ITEMS_HAT_DAMAGE_REDUCTION_ENABLED.get();
        itemsHatDamageReductionAmount = ITEMS_HAT_DAMAGE_REDUCTION_AMOUNT.get();
        blocksDryingDuration = BLOCKS_DRYING_DURATION.get();
        blocksBrewingDuration = BLOCKS_BREWING_DURATION.get();
        blocksJugEffectDuration = BLOCKS_JUG_EFFECT_DURATION.get();
    }
}
