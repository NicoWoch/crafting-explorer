package com.nicowoch.recipe_exporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nicowoch.recipe_exporter.jei.JeiPlugin;
import com.nicowoch.recipe_exporter.logging.LogData;
import com.nicowoch.recipe_exporter.logging.LogLevel;
import com.nicowoch.recipe_exporter.logging.Logger;
import com.nicowoch.recipe_exporter.logging.LoggerConsumerInfo;
import com.nicowoch.recipe_exporter.objects.*;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExportCommand extends CommandBase {
    private static final String EXPORTS_DIR = "./1_recipesexporter";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ItemSpecs.class, new ItemSpecsSerializer())
            .registerTypeAdapter(Ingredient.class, new IngredientSerializer())
            .create();

    @Override
    public String getName() {
        return "recipesexporter";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/recipesexporter";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        int subscription = Logger.subscribe(new LoggerConsumerInfo(this::logToSender, LogLevel.INFO));

        server.addScheduledTask(() -> {
            try {
                executeTask();
            } catch (CommandException e) {
                Logger.error(e);
            }
        });

        Logger.info("Command /export finished");
        Logger.unsubscribe(subscription);
    }

    private void executeTask() throws CommandException {
        Logger.debug("Worker has started!");

        try {
            Path dirPath = Paths.get(EXPORTS_DIR);
            createExportDirectory(dirPath);

            Export export = generateExport();

            saveExportToFile(export, dirPath, "export.json");
        } catch (Exception e) {
            Logger.error(e);
            throw new CommandException(e + e.getMessage());
        }
    }

    private void createExportDirectory(Path dirPath) throws CommandException {
        try {
            Files.createDirectories(dirPath);
        } catch (Exception e) {
            Logger.error(e);
            throw new CommandException("Failed to create directory: " + dirPath, e);
        }

        Logger.info(TextFormatting.GRAY + "Export directory is: " + dirPath.toAbsolutePath());
        Logger.info(TextFormatting.GRAY + "All data will be saved there");
    }

    @Nullable
    private Export generateExport() {
        IJeiRuntime runtime = JeiPlugin.getJeiRuntime(Logger::info);

        if (runtime == null) {
            return null;
        }

        JeiParser jeiParser = new JeiParser(runtime.getRecipeRegistry());

        return jeiParser.parse();
    }

    private void saveExportToFile(@Nullable Export export, Path dirPath, String fileName) {
        if (export == null) {
            Logger.error(TextFormatting.RED + "No export data found!",
                    "ExportCommand.saveExportToFile(null, %, %)", dirPath, fileName);
            return;
        }

        try {
            File file = new File(dirPath.resolve(fileName).toString());
            FileWriter writer = new FileWriter(file);

            gson.toJson(export, writer);

            writer.close();

            Logger.info(TextFormatting.GREEN + "Export saved to [" + file.getName() + "]");
        } catch (IOException e) {
            Logger.error(TextFormatting.RED + "Failed to save file! Error: " + e, e);
        }
    }

    private void logToSender(LogData data) {
        TextFormatting level_color = TextFormatting.RESET;

        switch (data.level) {
            case DEBUG:
            case INFO:
                level_color = TextFormatting.WHITE;
                break;
            case WARN:
                level_color = TextFormatting.YELLOW;
                break;
            case ERROR:
                level_color = TextFormatting.RED;
                break;
        }

        TextComponentString message = new TextComponentString(
                "[" + level_color + data.level + TextFormatting.RESET + "] " + data.getShortMessage() + TextFormatting.RESET
        );

        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(message);
        });
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // all players can use
    }
}
