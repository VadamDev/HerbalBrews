package net.satisfy.herbalbrews.core.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.satisfy.herbalbrews.core.blocks.entity.CauldronBlockEntity;
import net.satisfy.herbalbrews.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class CauldronRecipe implements Recipe<CauldronBlockEntity> {

    private final NonNullList<net.minecraft.world.item.crafting.Ingredient> inputs;
    private final ItemStack output;

    public CauldronRecipe(ItemStack output, NonNullList<net.minecraft.world.item.crafting.Ingredient> inputs) {
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public @NotNull NonNullList<net.minecraft.world.item.crafting.Ingredient> getIngredients() {
        return this.inputs;
    }


    @Override
    public boolean matches(CauldronBlockEntity recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(CauldronBlockEntity recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
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

        private final MapCodec<CauldronRecipe> codec = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ItemStack.STRICT_CODEC.fieldOf("result").forGetter((cauldronRecipe) -> {
                return cauldronRecipe.output;
            }), Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap((list) -> {
                Ingredient[] ingredients = (Ingredient[])list.stream().filter((ingredient) -> {
                    return !ingredient.isEmpty();
                }).toArray(Ingredient[]::new);
                if (ingredients.length == 0) {
                    return DataResult.error(() -> {
                        return "No ingredients for cauldron recipe";
                    });
                } else {
                    return ingredients.length > 3 ? DataResult.error(() -> {
                        return "Too many ingredients for cauldron recipe";
                    }) : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                }
            }, DataResult::success).forGetter((cauldronRecipe) -> {
                return cauldronRecipe.inputs;
            })).apply(instance, CauldronRecipe::new);
        });

        public final StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> STREAM_CODEC = StreamCodec.of(this::toNetwork, this::fromNetwork);

        @Override
        public MapCodec<CauldronRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public @NotNull CauldronRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            int i = registryFriendlyByteBuf.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
            nonNullList.replaceAll((ingredient) -> {
                return Ingredient.CONTENTS_STREAM_CODEC.decode(registryFriendlyByteBuf);
            });
            return new CauldronRecipe(itemStack, nonNullList);
        }

        public void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, CauldronRecipe recipe) {
            registryFriendlyByteBuf.writeVarInt(recipe.inputs.size());

            for (Ingredient ingredient : recipe.inputs) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(registryFriendlyByteBuf, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.output);
        }
    }
}
