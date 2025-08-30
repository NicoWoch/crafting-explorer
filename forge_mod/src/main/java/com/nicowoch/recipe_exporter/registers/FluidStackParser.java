package com.nicowoch.recipe_exporter.registers;

import com.nicowoch.recipe_exporter.logging.Logger;
import com.nicowoch.recipe_exporter.objects.ItemSpecs;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class FluidStackParser extends StackParserBase<FluidStack> {

    @Override
    protected Class<FluidStack> GetStackType() {
        return FluidStack.class;
    }

    @Override
    protected void parseStack(FluidStack stack, StackParserBase.ParsingResult result) {
        result.item.type = "Fluid";
        result.item.id = "<fluid>." + stack.getFluid().getName();
        result.item.display_name = stack.getLocalizedName();
        result.item.metadata = 0;

        result.item.specific_data = new ItemSpecs();
        parseSpecificData(stack, result.item.specific_data);

        result.ingredient.count = stack.amount;

        result.item.icon_index = this.iconsRegister.registerBase64Image(
                getFluidIconBase64(stack.getFluid(), stack.getFluid().getStill())
        );
    }

    private void parseSpecificData(FluidStack stack, ItemSpecs data) {
        Fluid fluid = stack.getFluid();

        data.add("unlocalized_name", stack.getUnlocalizedName());
        data.add("localized_name", stack.getLocalizedName());

        data.add("color", fluid.getColor());
        data.add("density", fluid.getDensity());
        data.add("luminosity", fluid.getLuminosity());
        data.add("temperature", fluid.getTemperature());
        data.add("viscosity", fluid.getViscosity());
    }

    @Nullable
    public String getFluidIconBase64(Fluid fluid, ResourceLocation resourceLocation) {
        // ChatGPT Code in this function, with little modifications
        try {
            // 1. Get the fluid still texture sprite
            TextureAtlasSprite sprite = Minecraft.getMinecraft()
                    .getTextureMapBlocks()
                    .getAtlasSprite(resourceLocation.toString());

            int[][] frames = sprite.getFrameTextureData(0);

            if (frames.length == 0 || frames[0] == null) {
                Logger.error("No frame texture data found for fluid: " + fluid.getName(), TextFormatting.RED);
                return null;
            }

            int size = sprite.getIconWidth(); // Usually 16
            int[] pixelData = frames[0];

            // 3. Create BufferedImage
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            image.setRGB(0, 0, size, size, pixelData, 0, size);

            // 4. Convert to Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            Logger.error("Error for fluid " + fluid.getName() + " : " + e, TextFormatting.RED);
            return IconsRegistry.NO_ITEM_IMAGE;
        }
    }
}
