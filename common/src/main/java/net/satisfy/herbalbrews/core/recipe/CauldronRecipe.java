package net.satisfy.herbalbrews.core.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.satisfy.herbalbrews.core.registry.RecipeTypeRegistry;
import net.satisfy.herbalbrews.core.util.HerbalBrewsUtil;
import net.satisfy.herbalbrews.core.util.StreamCodecUtil;
import org.jetbrains.annotations.NotNull;

public class CauldronRecipe implements Recipe<RecipeInput> {

    private final NonNullList<net.minecraft.world.item.crafting.Ingredient> inputs;
    private final ItemStack output;

    public CauldronRecipe(NonNullList<net.minecraft.world.item.crafting.Ingredient> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public @NotNull NonNullList<net.minecraft.world.item.crafting.Ingredient> getIngredients() {
        return this.inputs;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        int nonEmptySlots = 0;
        for (int i = 0; i < recipeInput.size(); i++) {
            if (!recipeInput.getItem(i).isEmpty()) {
                nonEmptySlots++;
            }
        }
        return nonEmptySlots >= 1 && nonEmptySlots <= inputs.size() && HerbalBrewsUtil.matchesRecipe(recipeInput, inputs, 0, 3);
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    public @NotNull ResourceLocation getId() {
        return RecipeTypeRegistry.CAULDRON_RECIPE_TYPE.getId();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.output.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegistry.CAULDRON_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.CAULDRON_RECIPE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<CauldronRecipe> {

        private final MapCodec<CauldronRecipe> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                            Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                            if (ingredients.length == 0) {
                                return DataResult.error(() -> "No ingredients for shapeless recipe");
                            }
                            return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                        }, DataResult::success).forGetter(CauldronRecipe::getIngredients),
                        ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.output)
                ).apply(instance, CauldronRecipe::new)
        );

        public final StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> STREAM_CODEC = StreamCodec.composite(
                StreamCodecUtil.nonNullList(Ingredient.CONTENTS_STREAM_CODEC, Ingredient.EMPTY), CauldronRecipe::getIngredients,
                ItemStack.STREAM_CODEC, recipe -> recipe.output,
                CauldronRecipe::new);

        @Override
        public MapCodec<CauldronRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
