package net.satisfy.herbalbrews.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffect;
import net.satisfy.herbalbrews.core.blocks.entity.TeaKettleBlockEntity;
import net.satisfy.herbalbrews.core.registry.RecipeTypeRegistry;
import net.satisfy.herbalbrews.core.util.HerbalBrewsUtil;
import org.jetbrains.annotations.NotNull;

public class TeaKettleRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final Holder<MobEffect> effect;
    private final int effectDuration;
    private final int requiredWater;
    private final int requiredHeat;
    private final int requiredDuration;

    public TeaKettleRecipe(NonNullList<Ingredient> inputs, ItemStack output, Holder<MobEffect> effect, int effectDuration, int requiredWater, int requiredHeat, int requiredDuration) {
        this.inputs = inputs;
        this.output = output;
        this.effect = effect;
        this.effectDuration = effectDuration;
        this.requiredWater = requiredWater;
        this.requiredHeat = requiredHeat;
        this.requiredDuration = requiredDuration;
    }

    private boolean waterLevelSufficient(Container inventory) {
        if (inventory instanceof TeaKettleBlockEntity teaKettle) {
            return teaKettle.getWaterLevel() >= requiredWater;
        }
        return false;
    }

    private boolean heatLevelSufficient(Container inventory) {
        if (inventory instanceof TeaKettleBlockEntity teaKettle) {
            return teaKettle.getHeatLevel() >= requiredHeat;
        }
        return false;
    }

    public ItemStack assemble() {
        return assemble(null, null);
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return HerbalBrewsUtil.matchesRecipe(recipeInput, inputs, 0, 5) /*&& waterLevelSufficient(recipeInput) && heatLevelSufficient(recipeInput) TODO fixme*/;
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.output;
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

    public Holder<MobEffect> getEffect() {
        return this.effect;
    }

    public int getEffectDuration() {
        return this.effectDuration;
    }

    public int getRequiredWater() {
        return this.requiredWater;
    }

    public int getRequiredHeat() {
        return this.requiredHeat;
    }

    public int getRequiredDuration() {
        return this.requiredDuration;
    }

    public @NotNull ResourceLocation getId() {
        return RecipeTypeRegistry.TEA_KETTLE_RECIPE_TYPE.getId();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegistry.TEA_KETTLE_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.TEA_KETTLE_RECIPE_TYPE.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<TeaKettleRecipe> {

        public static final StreamCodec<RegistryFriendlyByteBuf, TeaKettleRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        public static final MapCodec<TeaKettleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                            Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                            if (ingredients.length == 0) {
                                return DataResult.error(() -> "No ingredients for Tea Kettle recipe");
                            } else {
                                return ingredients.length > 6 ? DataResult.error(() -> {
                                    return "Too many ingredients for Tea Kettle recipe";
                                }) : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                            }
                        }, DataResult::success).forGetter(TeaKettleRecipe::getIngredients),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(teaKettleRecipe -> {
                            return teaKettleRecipe.output;
                        }), MobEffect.CODEC.fieldOf("effect").forGetter(TeaKettleRecipe::getEffect),
                Codec.INT.fieldOf("result").fieldOf("effectduration").forGetter(TeaKettleRecipe::getEffectDuration),
                Codec.INT.fieldOf("fluid").fieldOf("amount").forGetter(TeaKettleRecipe::getRequiredWater),
                Codec.INT.fieldOf("heat_needed").fieldOf("amount").forGetter(TeaKettleRecipe::getRequiredHeat),
                Codec.INT.fieldOf("crafting_duration").forGetter(TeaKettleRecipe::getRequiredDuration)
                ).apply(instance, TeaKettleRecipe::new)
        );

        public static @NotNull TeaKettleRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            int i = registryFriendlyByteBuf.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            nonNullList.replaceAll((ingredient) -> Ingredient.CONTENTS_STREAM_CODEC.decode(registryFriendlyByteBuf));
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);

            MobEffect effect = null;
            int effectDuration = 0;
            boolean hasEffect = registryFriendlyByteBuf.readBoolean();
            if (hasEffect) {
                ResourceLocation effectId = registryFriendlyByteBuf.readResourceLocation();
                effect = BuiltInRegistries.MOB_EFFECT.get(effectId);
                effectDuration = registryFriendlyByteBuf.readInt();
            }
            int requiredWater = registryFriendlyByteBuf.readInt();
            int requiredHeat = registryFriendlyByteBuf.readInt();
            int requiredDuration = registryFriendlyByteBuf.readInt();
            return new TeaKettleRecipe(nonNullList, itemStack, BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), effectDuration, requiredWater, requiredHeat, requiredDuration);
        }

        public static void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, TeaKettleRecipe recipe) {
            registryFriendlyByteBuf.writeVarInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(registryFriendlyByteBuf, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.output);

            if (recipe.effect != null) {
                registryFriendlyByteBuf.writeBoolean(true);
                registryFriendlyByteBuf.writeResourceLocation(recipe.effect.value().getDescriptionId().contains(":") ? ResourceLocation.parse(recipe.effect.value().getDescriptionId().split(":")[1]) : ResourceLocation.withDefaultNamespace("unknown"));
                registryFriendlyByteBuf.writeInt(recipe.effectDuration);
            } else {
                registryFriendlyByteBuf.writeBoolean(false);
            }
            registryFriendlyByteBuf.writeInt(recipe.requiredWater);
            registryFriendlyByteBuf.writeInt(recipe.requiredHeat);
            registryFriendlyByteBuf.writeInt(recipe.requiredDuration);
        }

        @Override
        public MapCodec<TeaKettleRecipe> codec() {
            return null;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TeaKettleRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static class Type implements RecipeType<TeaKettleRecipe> {
        private Type() {
        }
    }
}
