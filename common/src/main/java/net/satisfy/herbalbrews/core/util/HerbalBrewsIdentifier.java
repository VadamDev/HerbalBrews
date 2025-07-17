package net.satisfy.herbalbrews.core.util;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.herbalbrews.HerbalBrews;

public class HerbalBrewsIdentifier {

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(HerbalBrews.MOD_ID, path);
    }
}
