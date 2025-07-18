package net.satisfy.herbalbrews.core.compat.rei.display;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.satisfy.herbalbrews.core.compat.rei.category.TeaKettleCategory;
import net.satisfy.herbalbrews.core.recipe.TeaKettleRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TeaKettleDisplay extends BasicDisplay {
    private static final int REQUIRED_INPUT_SLOTS = 4;

    public TeaKettleDisplay(RecipeHolder<TeaKettleRecipe> recipe) {
        super(prepareInputs(recipe), Collections.singletonList(EntryIngredients.of(recipe.value().getResultItem(null))), Optional.of(recipe.id()));
    }

    private static List<EntryIngredient> prepareInputs(RecipeHolder<TeaKettleRecipe> recipe) {
        List<EntryIngredient> inputs = new ArrayList<>(EntryIngredients.ofIngredients(new ArrayList<>(recipe.value().getIngredients())));

        if (inputs.size() > REQUIRED_INPUT_SLOTS) {
            inputs = inputs.subList(0, REQUIRED_INPUT_SLOTS);
        }

        while (inputs.size() < REQUIRED_INPUT_SLOTS) {
            inputs.add(EntryIngredient.empty());
        }

        return inputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return TeaKettleCategory.TEA_KETTLE_DISPLAY;
    }
}
