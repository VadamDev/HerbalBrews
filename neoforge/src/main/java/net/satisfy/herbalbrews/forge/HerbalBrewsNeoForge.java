package net.satisfy.herbalbrews.forge;

import dev.architectury.platform.hooks.EventBusesHooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.satisfy.herbalbrews.HerbalBrews;
import net.satisfy.herbalbrews.core.registry.CompostableRegistry;
import net.satisfy.herbalbrews.forge.config.HerbalBrewsNeoForgeConfig;

@Mod(HerbalBrews.MOD_ID)
public class HerbalBrewsNeoForge {
    public HerbalBrewsNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        EventBusesHooks.whenAvailable(HerbalBrews.MOD_ID, IEventBus::start);
        HerbalBrews.init();
        modContainer.registerConfig(ModConfig.Type.COMMON, HerbalBrewsNeoForgeConfig.COMMON_CONFIG);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CompostableRegistry::registerCompostable);
    }
}
