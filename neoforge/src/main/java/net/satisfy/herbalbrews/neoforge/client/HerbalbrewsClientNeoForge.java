package net.satisfy.herbalbrews.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.satisfy.herbalbrews.HerbalBrews;
import net.satisfy.herbalbrews.client.HerbalbrewsClient;
import net.satisfy.herbalbrews.client.gui.CauldronGui;
import net.satisfy.herbalbrews.client.gui.TeaKettleGui;
import net.satisfy.herbalbrews.core.registry.MenuTypeRegistry;

@EventBusSubscriber(modid = HerbalBrews.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class HerbalbrewsClientNeoForge {
    @SubscribeEvent
    public static void onClientSetup(RegisterEvent event) {
        HerbalbrewsClient.preInitClient();
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        HerbalbrewsClient.onInitializeClient();
    }

    @SubscribeEvent
    public static void clientLoad(RegisterMenuScreensEvent event) {
        event.register(MenuTypeRegistry.TEA_KETTLE_SCREEN_HANDLER.get(), TeaKettleGui::new);
        event.register(MenuTypeRegistry.CAULDRON_SCREEN_HANDLER.get(), CauldronGui::new);
    }
}
