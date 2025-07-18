package net.satisfy.herbalbrews.platform.neoforge;

import net.satisfy.herbalbrews.forge.config.HerbalBrewsNeoForgeConfig;
import net.satisfy.herbalbrews.platform.PlatformHelper;

public class PlatformHelperImpl extends PlatformHelper {
    public static boolean shouldGiveEffect() {
        return HerbalBrewsNeoForgeConfig.ITEMS_BANNER_GIVE_EFFECT.get();
    }

    public static boolean shouldShowTooltip() {
        return HerbalBrewsNeoForgeConfig.ITEMS_BANNER_SHOW_TOOLTIP.get();
    }

    public static int getDryingDuration() {
        return HerbalBrewsNeoForgeConfig.BLOCKS_DRYING_DURATION.get();
    }

    public static int getBrewingDuration() {
        return HerbalBrewsNeoForgeConfig.BLOCKS_BREWING_DURATION.get();
    }

    public static boolean isHatDamageReductionEnabled() {
        return HerbalBrewsNeoForgeConfig.ITEMS_HAT_DAMAGE_REDUCTION_ENABLED.get();
    }

    public static int getHatDamageReductionAmount() {
        return HerbalBrewsNeoForgeConfig.ITEMS_HAT_DAMAGE_REDUCTION_AMOUNT.get();
    }

    public static int getJugEffectDuration() {
        return HerbalBrewsNeoForgeConfig.BLOCKS_JUG_EFFECT_DURATION.get();
    }
}
