package com.nicowoch.recipe_exporter.registers;

import com.nicowoch.recipe_exporter.objects.Ingredient;
import com.nicowoch.recipe_exporter.objects.Item;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
abstract class StackParserBase<T> {

    public static class ParsingResult {
        Item item = new Item();
        Ingredient ingredient = new Ingredient();
    }

    protected IconsRegistry iconsRegister;

    public final void setIconsRegister(IconsRegistry iconsRegister) {
        this.iconsRegister = iconsRegister;
    }

    protected abstract Class<T> GetStackType();

    @Nullable
    public ParsingResult tryParseStack(@Nullable Object stack) {
        Class<T> correctStackType = GetStackType();

        if (!correctStackType.isInstance(stack)) {
            return null;
        }

        ParsingResult result = new ParsingResult();

        parseStack(correctStackType.cast(stack), result);

        return result;
    }

    protected abstract void parseStack(T stack, ParsingResult result);
}
