package net.satisfy.herbalbrews.core.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.satisfy.herbalbrews.core.registry.RecipeTypeRegistry;

public class TeaKettleRecipeInput implements RecipeInput {

    private int waterLevel;
    private int heatLevel;

    public RecipeType<?> getType() {
        return RecipeTypeRegistry.TEA_KETTLE_RECIPE_TYPE.get();
    }

    public int getHeatLevel() {
        return this.heatLevel;
    }

    public int getWaterLevel() {
        return this.waterLevel;
    }

    @Override
    public ItemStack getItem(int i) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
