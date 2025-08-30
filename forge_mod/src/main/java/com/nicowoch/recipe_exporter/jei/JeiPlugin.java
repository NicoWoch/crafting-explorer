package com.nicowoch.recipe_exporter.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.IJeiRuntime;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@JEIPlugin
public class JeiPlugin implements IModPlugin {

    public static IJeiRuntime jeiRuntime = null;
    public static IModRegistry modRegistry = null;

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JeiPlugin.jeiRuntime = jeiRuntime;
    }

    @Override
    public void register(IModRegistry modRegistry) {
        JeiPlugin.modRegistry = modRegistry;
    }

    public static boolean isJeiNotInitialized() {
        return jeiRuntime == null || modRegistry == null;
    }

    @Nullable
    public static IJeiRuntime getJeiRuntime(Consumer<String> respond) {
        if (!Loader.isModLoaded("jei")) {
            respond.accept(TextFormatting.RED + "JEI is not present, cannot further continue!");
            return null;
        }

        respond.accept(TextFormatting.GREEN + "JEI detected!");

        if (JeiPlugin.isJeiNotInitialized()) {
            respond.accept(TextFormatting.RED + "JEI not initialized runtime or mod registry yet.");
            return null;
        }

        return JeiPlugin.jeiRuntime;
    }
}