package com.nicowoch.recipe_exporter.registers;

import com.nicowoch.recipe_exporter.logging.Logger;
import com.nicowoch.recipe_exporter.objects.Ingredient;
import com.nicowoch.recipe_exporter.objects.Item;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class StacksRegistry extends RegisterBase<Item> {

    private static final UnknownStackParser unknownStackParser = new UnknownStackParser();

    private static final StackParserBase<?>[] stackParsers = new StackParserBase<?>[] {
            new ItemStackParser(),
            new FluidStackParser(),
            unknownStackParser,
    };

    public StacksRegistry(IconsRegistry iconsRegister) {
        for (StackParserBase<?> parser : stackParsers) {
            parser.setIconsRegister(iconsRegister);
        }
    }

    public Ingredient registerStack(Object stack) {
        StackParserBase.ParsingResult result = null;

        for (StackParserBase<?> parser : stackParsers) {
            result = parser.tryParseStack(stack);

            if (result != null) {
                break;
            }
        }

        if (result == null) {
            String errorMessage = "Failed to parse stack by all registered parsers: " + stack;

            Logger.error(errorMessage, TextFormatting.RED);
            throw new RuntimeException(errorMessage);
        }

        result.ingredient.item_index = findOrAddItemToRegistry(result.item);

        return result.ingredient;
    }

    @Override
    protected String getItemHash(Item item) {
        return item.id;
    }

    @Override
    protected boolean compareItems(Item a, Item b) {
        return a.equals(b);
    }
}
