package net.satisfy.herbalbrews.core.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.satisfy.herbalbrews.core.blocks.entity.TeaKettleBlockEntity;

public class TeaKettleRecipeInput implements RecipeInput {

    public TeaKettleBlockEntity teaKettleBlockEntity;

    public TeaKettleRecipeInput() {
        this(new TeaKettleBlockEntity(null, null));
    }

    public TeaKettleRecipeInput(TeaKettleBlockEntity teaKettleBlockEntity) {
        this.teaKettleBlockEntity = teaKettleBlockEntity;
        if (this.teaKettleBlockEntity == null) {
            this.teaKettleBlockEntity = new TeaKettleBlockEntity(null, null);
        }
    }

    @Override
    public ItemStack getItem(int i) {
        return teaKettleBlockEntity.getItem(i);
    }

    @Override
    public int size() {
        return teaKettleBlockEntity.getItems().size();
    }

    @Override
    public boolean isEmpty() {
        return teaKettleBlockEntity.isEmpty();
    }

    public int getWaterLevel() {
        return teaKettleBlockEntity.getWaterLevel();
    }

    public int getHeatLevel() {
        return teaKettleBlockEntity.getHeatLevel();
    }
}
