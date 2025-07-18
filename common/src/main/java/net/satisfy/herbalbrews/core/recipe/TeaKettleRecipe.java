package net.satisfy.herbalbrews.core.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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
    private final MobEffect effect;
    private final int effectDuration;
    private final int requiredWater;
    private final int requiredHeat;
    private final int requiredDuration;

    public TeaKettleRecipe(NonNullList<Ingredient> inputs, ItemStack output, MobEffect effect, int effectDuration, int requiredWater, int requiredHeat, int requiredDuration) {
        this.inputs = inputs;
        this.output = output;
        this.effect = effect;
        this.effectDuration = effectDuration;
        this.requiredWater = requiredWater;
        this.requiredHeat = requiredHeat;
        this.requiredDuration = requiredDuration;
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

    public static class Serializer implements RecipeSerializer<TeaKettleRecipe> {

        // TODO fixme
        @Override
        public MapCodec<TeaKettleRecipe> codec() {
            return null;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TeaKettleRecipe> streamCodec() {
            return null;
        }

        // TODO fixme
        /*
        @Override
        public @NotNull TeaKettleRecipe fromJson(ResourceLocation id, JsonObject json) {
            final var ingredients = HerbalBrewsUtil.deserializeIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for Tea Kettle Recipe");
            } else if (ingredients.size() > 6) {
                throw new JsonParseException("Too many ingredients for Tea Kettle Recipe");
            }
            JsonObject result = GsonHelper.getAsJsonObject(json, "result");
            ItemStack output = ShapedRecipe.itemStackFromJson(result);
            MobEffect effect = null;
            int effectDuration = 0;
            if (result.has("effect")) {
                ResourceLocation effectId = new ResourceLocation(GsonHelper.getAsString(result, "effect"));
                effect = BuiltInRegistries.MOB_EFFECT.get(effectId);
                if (effect == null) {
                    throw new JsonParseException("Invalid effect ID: " + effectId);
                }
            }
            if (result.has("effectduration")) {
                effectDuration = GsonHelper.getAsInt(result, "effectduration");
            }
            int requiredWater = 0;
            if (json.has("fluid")) {
                var fluidArray = GsonHelper.getAsJsonArray(json, "fluid");
                if (!fluidArray.isEmpty()) {
                    requiredWater = GsonHelper.getAsInt(fluidArray.get(0).getAsJsonObject(), "amount");
                }
            }
            int requiredHeat = 0;
            if (json.has("heat_needed")) {
                var heatArray = GsonHelper.getAsJsonArray(json, "heat_needed");
                if (!heatArray.isEmpty()) {
                    requiredHeat = GsonHelper.getAsInt(heatArray.get(0).getAsJsonObject(), "amount");
                }
            }
            int requiredDuration = 0;
            if (json.has("crafting_duration")) {
                requiredDuration = GsonHelper.getAsInt(json, "crafting_duration") * 20;
            }
            return new TeaKettleRecipe(id, ingredients, output, effect, effectDuration, requiredWater, requiredHeat, requiredDuration);
        }

        @Override
        public @NotNull TeaKettleRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            final var ingredients = NonNullList.withSize(buf.readVarInt(), Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.fromNetwork(buf));
            ItemStack output = buf.readItem();
            MobEffect effect = null;
            int effectDuration = 0;
            boolean hasEffect = buf.readBoolean();
            if (hasEffect) {
                ResourceLocation effectId = buf.readResourceLocation();
                effect = BuiltInRegistries.MOB_EFFECT.get(effectId);
                effectDuration = buf.readInt();
            }
            int requiredWater = buf.readInt();
            int requiredHeat = buf.readInt();
            int requiredDuration = buf.readInt();
            return new TeaKettleRecipe(id, ingredients, output, effect, effectDuration, requiredWater, requiredHeat, requiredDuration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, TeaKettleRecipe recipe) {
            buf.writeVarInt(recipe.inputs.size());
            recipe.inputs.forEach(entry -> entry.toNetwork(buf));
            buf.writeItem(recipe.output);
            if (recipe.effect != null) {
                buf.writeBoolean(true);
                buf.writeResourceLocation(recipe.effect.getDescriptionId().contains(":") ? new ResourceLocation(recipe.effect.getDescriptionId().split(":")[1]) : new ResourceLocation("minecraft", "unknown"));
                buf.writeInt(recipe.effectDuration);
            } else {
                buf.writeBoolean(false);
            }
            buf.writeInt(recipe.requiredWater);
            buf.writeInt(recipe.requiredHeat);
            buf.writeInt(recipe.requiredDuration);
        }*/
    }

    public static class Type implements RecipeType<TeaKettleRecipe> {
        private Type() {
        }
    }
}
