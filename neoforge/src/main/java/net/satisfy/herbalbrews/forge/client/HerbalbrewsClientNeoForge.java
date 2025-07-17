package net.satisfy.herbalbrews.forge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.satisfy.herbalbrews.HerbalBrews;
import net.satisfy.herbalbrews.client.HerbalbrewsClient;

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
}
