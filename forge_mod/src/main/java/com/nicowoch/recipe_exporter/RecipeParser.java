package com.nicowoch.recipe_exporter;

import com.nicowoch.recipe_exporter.jei.MyJeiIngredients;
import com.nicowoch.recipe_exporter.logging.Logger;
import com.nicowoch.recipe_exporter.objects.Ingredient;
import com.nicowoch.recipe_exporter.objects.Recipe;
import com.nicowoch.recipe_exporter.registers.IconsRegistry;
import com.nicowoch.recipe_exporter.registers.SlotsRegistry;
import com.nicowoch.recipe_exporter.registers.StacksRegistry;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
class RecipeParser {
    private final StacksRegistry stacksRegister;
    private final SlotsRegistry slotsRegister;
    private final IconsRegistry iconsRegister;

    public RecipeParser(StacksRegistry stacksRegister, SlotsRegistry slotsRegister, IconsRegistry iconsRegister) {
        this.stacksRegister = stacksRegister;
        this.slotsRegister = slotsRegister;
        this.iconsRegister = iconsRegister;
    }

    public Recipe parseRecipe(IRecipeCategory<?> category, IRecipeWrapper recipeWrapper) {
        Logger.debug("Parsing recipe with class " + recipeWrapper.getClass() + " started!");

        Recipe recipe = new Recipe();

        recipe.category_uid = category.getUid();
        recipe.type = recipeWrapper.getClass().toString();

        MyJeiIngredients ingredients = new MyJeiIngredients();

        Logger.debug("Expanding ingredients started");
        recipeWrapper.getIngredients(ingredients);
        Logger.debug("Expanding ingredients ended");

        Logger.debug("Parsing inputs started");
        recipe.inputSlots = registerIngredients(parseIngredients(ingredients.inputsByType));

        Logger.debug("Parsing outputs started");
        recipe.outputSlots = registerIngredients(parseIngredients(ingredients.outputsByType));

        Logger.debug("Finished parsing recipe");
        Logger.debug("Memory raport:");
        Logger.debug("Stack register size: " + stacksRegister.getRegister().size());
        Logger.debug("-> Memory: ~" + Math.round(stacksRegister.getRegister().size() * 0.3) + " KB");
        Logger.debug("Slots register size: " + slotsRegister.getRegister().size());
        Logger.debug("-> Memory: ~" + Math.round(slotsRegister.getRegister().size() * 0.1) + " KB");
        Logger.debug("Icons register size: " + iconsRegister.getRegister().size());
        Logger.debug("-> Memory: ~" + Math.round(iconsRegister.getRegister().size() * 0.5) + " KB");

        return recipe;
    }

    private List<Integer> registerIngredients(List<List<Ingredient>> ingredients) {
        return ingredients.stream().map(slotsRegister::registerSlot).collect(Collectors.toList());
    }

    private List<List<Ingredient>> parseIngredients(Map<IIngredientType<?>, List<List<?>>> ingredientsByType) {
        List<List<Ingredient>> ingredients = new ArrayList<>();

        for (List<List<?>> ings : ingredientsByType.values()) {
            ingredients.addAll(parseIngredients(ings));
        }

        return ingredients;
    }

    private List<List<Ingredient>> parseIngredients(List<List<?>> ingredients) {
        return ingredients.stream().map(
                slot -> slot.stream().map(this::parseIngredient).collect(Collectors.toList())
        ).collect(Collectors.toList());
    }

    private Ingredient parseIngredient(Object ingredient) {
        return stacksRegister.registerStack(ingredient);
    }
}
