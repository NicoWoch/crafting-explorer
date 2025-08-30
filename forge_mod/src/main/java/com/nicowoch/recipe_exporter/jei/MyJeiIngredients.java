package com.nicowoch.recipe_exporter.jei;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IIngredientType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MyJeiIngredients implements IIngredients {

    public final Map<IIngredientType<?>, List<List<?>>> inputsByType = new HashMap<>();
    public final Map<IIngredientType<?>, List<List<?>>> outputsByType = new HashMap<>();

    private <T> List<List<?>> expandSubtypes(IIngredientType<T> ingredientType, List<List<T>> inputs) {
        IIngredientRegistry ingredientRegistry = JeiPlugin.modRegistry.getIngredientRegistry();
        IIngredientHelper<T> ingredientHelper = ingredientRegistry.getIngredientHelper(ingredientType);

        List<List<?>> expandedInputs = new ArrayList<>();

        for(List<T> input : inputs) {
            List<T> itemStacks = ingredientHelper.expandSubtypes(input);
            expandedInputs.add(itemStacks);
        }

        return expandedInputs;
    }

    private <T> IIngredientType<T> getIngredientType(Class<? extends T> ingredientClass) {
        IIngredientRegistry ingredientRegistry = JeiPlugin.modRegistry.getIngredientRegistry();
        return ingredientRegistry.getIngredientType(ingredientClass);
    }

    // New API - Inputs

    @Override
    public <T> void setInput(IIngredientType<T> ingredientType, T input) {
        setInputs(ingredientType, Collections.singletonList(input));
    }

    @Override
    public <T> void setInputs(IIngredientType<T> ingredientType, List<T> inputs) {
        List<List<T>> inputLists = new ArrayList<>();
        for (T input : inputs) {
            inputLists.add(Collections.singletonList(input));
        }
        setInputLists(ingredientType, inputLists);
    }

    @Override
    public <T> void setInputLists(IIngredientType<T> ingredientType, List<List<T>> inputs) {
        inputsByType.put(ingredientType, expandSubtypes(ingredientType, inputs));
    }

    @Override
    public <T> List<List<T>> getInputs(IIngredientType<T> ingredientType) {
        return castNested(inputsByType.getOrDefault(ingredientType, Collections.emptyList()));
    }

    // New API - Outputs

    @Override
    public <T> void setOutput(IIngredientType<T> ingredientType, T output) {
        setOutputs(ingredientType, Collections.singletonList(output));
    }

    @Override
    public <T> void setOutputs(IIngredientType<T> ingredientType, List<T> outputs) {
        List<List<T>> outputLists = new ArrayList<>();
        for (T output : outputs) {
            outputLists.add(Collections.singletonList(output));
        }
        setOutputLists(ingredientType, outputLists);
    }

    @Override
    public <T> void setOutputLists(IIngredientType<T> ingredientType, List<List<T>> outputs) {
        outputsByType.put(ingredientType, expandSubtypes(ingredientType, outputs));
    }

    @Override
    public <T> List<List<T>> getOutputs(IIngredientType<T> ingredientType) {
        return castNested(outputsByType.getOrDefault(ingredientType, Collections.emptyList()));
    }

    // Deprecated methods (for backward compatibility)

    @Override
    @Deprecated
    public <T> void setInput(Class<? extends T> ingredientClass, T input) {
        setInput(getIngredientType(ingredientClass), input);
    }

    @Override
    @Deprecated
    public <T> void setInputs(Class<? extends T> ingredientClass, List<T> input) {
        setInputs(getIngredientType(ingredientClass), input);
    }

    @Override
    @Deprecated
    public <T> void setInputLists(Class<? extends T> ingredientClass, List<List<T>> inputs) {
        setInputLists(getIngredientType(ingredientClass), inputs);
    }

    @Override
    @Deprecated
    public <T> void setOutput(Class<? extends T> ingredientClass, T output) {
        setOutput(getIngredientType(ingredientClass), output);
    }

    @Override
    @Deprecated
    public <T> void setOutputs(Class<? extends T> ingredientClass, List<T> outputs) {
        setOutputs(getIngredientType(ingredientClass), outputs);
    }

    @Override
    @Deprecated
    public <T> void setOutputLists(Class<? extends T> ingredientClass, List<List<T>> outputs) {
        setOutputLists(getIngredientType(ingredientClass), outputs);
    }

    @Override
    @Deprecated
    public <T> List<List<T>> getInputs(Class<? extends T> ingredientClass) {
        return getInputs(getIngredientType(ingredientClass));
    }

    @Override
    @Deprecated
    public <T> List<List<T>> getOutputs(Class<? extends T> ingredientClass) {
        return getOutputs(getIngredientType(ingredientClass));
    }

    // Utility method to cast safely
    @SuppressWarnings("unchecked")
    private static <T> List<List<T>> castNested(List<List<?>> list) {
        return (List<List<T>>) (List<?>) list;
    }
}
