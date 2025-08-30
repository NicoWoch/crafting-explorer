package com.nicowoch.recipe_exporter;

import com.nicowoch.recipe_exporter.logging.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;

@Mod(
	modid = RecipeExporter.MODID,
	name = RecipeExporter.NAME,
	version = RecipeExporter.VERSION
)
public class RecipeExporter {
	public static final String MODID = "recipe_exporter_112";
	public static final String NAME = "Recipe Exporter 1.12";
	public static final String VERSION = "1.0";
	
	public static final org.apache.logging.log4j.Logger BASE_LOGGER = LogManager.getLogger(MODID);

	private Integer subscription_base = null;
	private Integer subscription_own = null;

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		clearOwnLogFile();
		own_file_logger.open();

		subscription_base = Logger.subscribe(new LoggerConsumerInfo(RecipeExporter::logToBaseLogger, LogLevel.DEBUG));
		subscription_own = Logger.subscribe(new LoggerConsumerInfo(RecipeExporter::logToOwnFile, LogLevel.DEBUG));

		event.registerServerCommand(new ExportCommand());

		Logger.info(RecipeExporter.MODID + " has been started, registered /export command");
	}

	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		Logger.info(RecipeExporter.MODID + " is stopping now");

		if (subscription_base != null) {
			Logger.unsubscribe(subscription_base);
			subscription_base = null;
		}

		if (subscription_own != null) {
			Logger.unsubscribe(subscription_own);
			subscription_own = null;
		}

		own_file_logger.close();
	}

	private static void logToBaseLogger(LogData data) {
		String message = TextFormatting.getTextWithoutFormattingCodes(data.getLongText());

		switch (data.level) {
			case DEBUG:
				BASE_LOGGER.debug(message);
				break;
			case INFO:
				BASE_LOGGER.info(message);
				break;
			case WARN:
				BASE_LOGGER.warn(message);
				break;
			case ERROR:
				BASE_LOGGER.error(message);
				break;
		}
	}

	private static final BasicFileLogger own_file_logger = new BasicFileLogger("recipesexporter.log");

	private static void logToOwnFile(LogData data) {
		StringBuilder message = new StringBuilder("[" + data.level + "]");

		for (Object argument : data.arguments) {
			String arg = data.parseArgument(argument);

			arg = TextFormatting.getTextWithoutFormattingCodes(arg);

			message.append("\n\t").append(arg);
		}

		message.append("\n");

        own_file_logger.log(message.toString());
	}

	private static void clearOwnLogFile() {
		own_file_logger.clearAllLogs();
	}
}
