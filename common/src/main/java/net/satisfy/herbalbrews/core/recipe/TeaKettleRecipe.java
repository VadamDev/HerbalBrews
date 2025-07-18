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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffect;
import net.satisfy.herbalbrews.core.blocks.entity.TeaKettleBlockEntity;
import net.satisfy.herbalbrews.core.registry.RecipeTypeRegistry;
import net.satisfy.herbalbrews.core.util.HerbalBrewsUtil;
import org.jetbrains.annotations.NotNull;

public class TeaKettleRecipe implements Recipe<TeaKettleBlockEntity> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final Holder<MobEffect> effect;
    private final int effectDuration;
    private final int requiredWater;
    private final int requiredHeat;
    private final int requiredDuration;
    private final float experience;

    public TeaKettleRecipe(float experience, int effectDuration, int requiredWater, int requiredHeat, int requiredDuration, ItemStack output, Holder<MobEffect> effect, NonNullList<net.minecraft.world.item.crafting.Ingredient> inputs) {
        this.inputs = inputs;
        this.output = output;
        this.effect = effect;
        this.effectDuration = effectDuration;
        this.requiredWater = requiredWater;
        this.requiredHeat = requiredHeat;
        this.requiredDuration = requiredDuration;
        this.experience = experience;
    }

    private boolean waterLevelSufficient(TeaKettleBlockEntity teaKettleRecipeInput) {
        return teaKettleRecipeInput.getWaterLevel() >= requiredWater;
    }

    private boolean heatLevelSufficient(TeaKettleBlockEntity teaKettleRecipeInput) {
        return teaKettleRecipeInput.getHeatLevel() >= requiredHeat;
    }

    public ItemStack assemble() {
        return assemble(null, null);
    }

    @Override
    public boolean matches(TeaKettleBlockEntity recipeInput, Level level) {
        return HerbalBrewsUtil.matchesRecipe(recipeInput, inputs, 0, 5) && waterLevelSufficient(recipeInput) && heatLevelSufficient(recipeInput);
    }

    @Override
    public ItemStack assemble(TeaKettleBlockEntity recipeInput, HolderLookup.Provider provider) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.output;
    }

    public MobEffect getEffect() {
        return this.effect.value();
    }

    public Holder<MobEffect> getEffectHolder() {
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

    public float getExperience() {
        return this.experience;
    }

    public static class Serializer implements RecipeSerializer<TeaKettleRecipe> {

        public final StreamCodec<RegistryFriendlyByteBuf, TeaKettleRecipe> STREAM_CODEC =
                StreamCodec.of(this::toNetwork, this::fromNetwork);

        private final MapCodec<TeaKettleRecipe> codec = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter((teaKettleRecipe) -> {
                        return teaKettleRecipe.experience;
                    }),
                    Codec.INT.fieldOf("effectDuration").orElse(0).forGetter((teaKettleRecipe) -> {
                        return teaKettleRecipe.effectDuration;
                    }),
                    Codec.INT.fieldOf("requiredWater").orElse(0).forGetter((teaKettleRecipe) -> {
                        return teaKettleRecipe.requiredWater;
                    }),
                    Codec.INT.fieldOf("requiredHeat").orElse(0).forGetter((teaKettleRecipe) -> {
                        return teaKettleRecipe.requiredHeat;
                    }),
                    Codec.INT.fieldOf("requiredDuration").orElse(0).forGetter((teaKettleRecipe) -> {
                        return teaKettleRecipe.requiredDuration;
                    }),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter((teaKettleRecipe) -> {
                        return teaKettleRecipe.output;
                    }),
                    MobEffect.CODEC.fieldOf("effect").forGetter(teaKettleRecipe -> {
                        return teaKettleRecipe.effect;
                    }),
                    Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap((list) -> {
                        Ingredient[] ingredients = (Ingredient[])list.stream().filter((ingredient) -> {
                            return !ingredient.isEmpty();
                        }).toArray(Ingredient[]::new);
                        if (ingredients.length == 0) {
                            return DataResult.error(() -> {
                                return "No ingredients for cauldron recipe";
                            });
                        } else {
                            return ingredients.length > 4 ? DataResult.error(() -> {
                                return "Too many ingredients for cauldron recipe";
                            }) : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                        }
                        }, DataResult::success).forGetter((teaKettleRecipe) -> {
                            return teaKettleRecipe.inputs;
                        })).apply(instance, TeaKettleRecipe::new);
        });

        @Override
        public MapCodec<TeaKettleRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TeaKettleRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public @NotNull TeaKettleRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            int i = registryFriendlyByteBuf.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
            nonNullList.replaceAll((ingredient) -> {
                return Ingredient.CONTENTS_STREAM_CODEC.decode(registryFriendlyByteBuf);
            });
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
            float experience = registryFriendlyByteBuf.readFloat();
            return new TeaKettleRecipe(experience, effectDuration, requiredWater, requiredHeat, requiredDuration, itemStack, BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), nonNullList);
        }

        public void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, TeaKettleRecipe recipe) {
            registryFriendlyByteBuf.writeVarInt(recipe.inputs.size());
            for (Ingredient ingredient : recipe.inputs) {
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
            registryFriendlyByteBuf.writeFloat(recipe.experience);
        }
    }
}
