package net.satisfy.herbalbrews.core.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.satisfy.herbalbrews.core.registry.RecipeTypeRegistry;
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
        StackedContents recipeMatcher = new StackedContents();
        int matchingStacks = 0;

        for(int i = 1; i < 5; ++i) {
            ItemStack itemStack = recipeInput.getItem(i);
            if (!itemStack.isEmpty()) {
                ++matchingStacks;
                recipeMatcher.accountStack(itemStack, 1);
            }
        }

        return matchingStacks == this.inputs.size() && recipeMatcher.canCraft(this, null);
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return this.output.copy();
    }

    public ItemStack assemble() {
        return assemble(null, null);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.output.copy();
    }

    public @NotNull ResourceLocation getId() {
        return RecipeTypeRegistry.CAULDRON_RECIPE_TYPE.getId();
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

        public static final StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        public static final MapCodec<CauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                    Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                    if (ingredients.length == 0) {
                        return DataResult.error(() -> "No ingredients for Cauldron recipe");
                    } else {
                        return ingredients.length > 3 ? DataResult.error(() -> {
                            return "Too many ingredients for Cauldron recipe";
                        }) : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                    }
                }, DataResult::success).forGetter(CauldronRecipe::getIngredients),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(cauldronRecipe -> {
                    return cauldronRecipe.output;
                })
                ).apply(instance, CauldronRecipe::new)
        );

        public static @NotNull CauldronRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            int i = registryFriendlyByteBuf.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            nonNullList.replaceAll((ingredient) -> Ingredient.CONTENTS_STREAM_CODEC.decode(registryFriendlyByteBuf));
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
            return new CauldronRecipe(nonNullList, itemStack);
        }

        public static void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, CauldronRecipe recipe) {
            registryFriendlyByteBuf.writeVarInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(registryFriendlyByteBuf, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.output);
        }

        @Override
        public MapCodec<CauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
