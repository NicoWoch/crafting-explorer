package com.nicowoch.recipe_exporter;

import com.nicowoch.recipe_exporter.logging.Logger;
import com.nicowoch.recipe_exporter.objects.Category;
import com.nicowoch.recipe_exporter.objects.Export;
import com.nicowoch.recipe_exporter.objects.Recipe;
import com.nicowoch.recipe_exporter.registers.IconsRegistry;
import com.nicowoch.recipe_exporter.registers.SlotsRegistry;
import com.nicowoch.recipe_exporter.registers.StacksRegistry;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JeiParser {
    private final IRecipeRegistry recipeRegistry;

    private final IconsRegistry iconsRegister;
    private final StacksRegistry stacksRegister;
    private final SlotsRegistry slotsRegister;
    private final RecipeParser recipeParser;

    public JeiParser(IRecipeRegistry recipeRegistry) {
        this.recipeRegistry = recipeRegistry;

        iconsRegister = new IconsRegistry();
        stacksRegister = new StacksRegistry(iconsRegister);
        slotsRegister = new SlotsRegistry();
        recipeParser = new RecipeParser(stacksRegister, slotsRegister, iconsRegister);
    }

    public Export parse() {
        Logger.info(TextFormatting.GRAY + "Parsing started!");

        Export output = new Export();

        List<IRecipeCategory<?>> jeiCategories = recipeRegistry.getRecipeCategories().stream().map(s -> (IRecipeCategory<?>) s).collect(Collectors.toList());

        Logger.info(TextFormatting.GRAY + "Parsing categories...");
        output.recipes_categories = parseCategories(jeiCategories);
        Logger.info(TextFormatting.GREEN + "Done parsing categories");

        Logger.info(TextFormatting.GRAY + "Parsing recipes...");
        output.recipes = parseRecipes();
        Logger.info(TextFormatting.GREEN + "Done parsing recipes");

        Logger.info(TextFormatting.GRAY + "Fetching registered items, slots and icons");
        output.items = stacksRegister.getRegister();
        output.slots = slotsRegister.getRegister();
        output.icons_base64 = iconsRegister.getRegister();

        Logger.info(TextFormatting.GREEN + "Export generated");
        return output;
    }

    private List<Category> parseCategories(List<IRecipeCategory<?>> categories) {
        return categories.stream().map(this::parseCategory).collect(Collectors.toList());
    }

    private Category parseCategory(IRecipeCategory<?> recipeCategory) {
        Category category = new Category();

        category.uid = recipeCategory.getUid();
        category.mod_name = recipeCategory.getModName();
        category.display_name = recipeCategory.getTitle();

        return category;
    }

    private List<Recipe> parseRecipes() {
        List<Tuple<IRecipeCategory<?>, IRecipeWrapper>> jeiRecipes = new ArrayList<>();

        List<IRecipeCategory<?>> categories = recipeRegistry.getRecipeCategories().stream().map(s -> (IRecipeCategory<?>) s).collect(Collectors.toList());

        for (IRecipeCategory<?> category : categories) {
            Logger.info(TextFormatting.DARK_BLUE + "Found recipe category \"" + category.getTitle() + "\"");

            for (IRecipeWrapper recipe : recipeRegistry.getRecipeWrappers(category)) {
                jeiRecipes.add(new Tuple<>(category, recipe));
            }
        }

        Logger.info(TextFormatting.BLUE + "Found " + jeiRecipes.size() + " recipes to parse");

        List<Recipe> recipes = new ArrayList<>();

        for (int i = 0; i < jeiRecipes.size(); i++) {
            Tuple<IRecipeCategory<?>, IRecipeWrapper> jeiRecipe = jeiRecipes.get(i);

            if (i == 0 || (i + 1) % 1000 == 0) {
                Logger.info(TextFormatting.BLUE + "[" + jeiRecipe.getFirst().getTitle() + "] Parsing recipe " + (i + 1) + " / " + jeiRecipes.size());
            }

            if (i == 0 || (i + 1) % 250 == 0) {
                showProgressBar(i, jeiRecipes.size());
            }

            recipes.add(this.recipeParser.parseRecipe(jeiRecipe.getFirst(), jeiRecipe.getSecond()));
        }

        showProgressBar(jeiRecipes.size() - 1, jeiRecipes.size());

        return recipes;
    }

    private void showProgressBar(int recipe, int countOfRecipes) {
        float progress = (float) (recipe + 1) / (float) countOfRecipes;

        int all_bars = 20;
        int hashes = (int) Math.floor(progress * all_bars);

        String bar = String.join("", Collections.nCopies(hashes, "#")) + String.join("", Collections.nCopies(all_bars - hashes, "-"));

        String message = String.format(
                "%s[%s] %d / %d (%.0f %%)%s",
                TextFormatting.BLUE,
                bar, recipe + 1, countOfRecipes, (float) Math.round(progress * 100),
                TextFormatting.RESET
        );

        sendChatMessage(message);
    }

    private void sendChatMessage(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
    }
}
