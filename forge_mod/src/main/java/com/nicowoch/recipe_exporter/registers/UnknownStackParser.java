package com.nicowoch.recipe_exporter.registers;

import com.nicowoch.recipe_exporter.logging.Logger;
import com.nicowoch.recipe_exporter.objects.ItemSpecs;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
class UnknownStackParser extends StackParserBase<Object> {

    @Override
    protected Class<Object> GetStackType() {
        return Object.class;
    }

    @Override
    @Nullable
    public ParsingResult tryParseStack(@Nullable Object stack) {
        ParsingResult result = new ParsingResult();

        parseStack(stack, result);

        return result;
    }

    @Override
    protected void parseStack(@Nullable Object stack, ParsingResult result) {
        result.item.type = "Unknown";
        result.item.id = "<unknown>";
        result.item.display_name = "(Unknown Item)";
        result.item.metadata = 0;
        result.item.specific_data = new ItemSpecs();

        if (stack == null) {
            result.item.specific_data.add("stack_class", "null");
            result.item.specific_data.add("stack_to_string", "null");
        } else {
            result.item.specific_data.add("stack_class", stack.getClass().toString());
            result.item.specific_data.add("stack_to_string", stack.toString());
        }

        result.ingredient.count = 1;

        result.item.icon_index = this.iconsRegister.registerNoImage();

        Logger.warn(TextFormatting.GRAY + "Parsed unknown stack");
    }
}
