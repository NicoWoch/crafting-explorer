package com.nicowoch.recipe_exporter.logging;

import java.util.HashMap;
import java.util.Map;

public class Logger {
    private static final Map<Integer, LoggerConsumerInfo> consumers = new HashMap<>();
    private static int nextConsumerIndex = 0;

    public static void logVerbose(LogLevel level, Object[] arguments) {
        for (LoggerConsumerInfo consumerInfo : consumers.values()) {
            if (consumerInfo.level.ordinal() <= level.ordinal()) {
                consumerInfo.consumer.accept(new LogData(level, arguments));
            }
        }
    }

    public static int subscribe(LoggerConsumerInfo consumerInfo) {
        consumers.put(nextConsumerIndex, consumerInfo);
        return nextConsumerIndex++;
    }

    public static void unsubscribe(int consumer_id) {
        consumers.remove(consumer_id);
    }

    public static void debug(Object... arguments) {
        logVerbose(LogLevel.DEBUG, arguments);
    }

    public static void info(Object... arguments) {
        logVerbose(LogLevel.INFO, arguments);
    }

    public static void warn(Object... arguments) {
        logVerbose(LogLevel.WARN, arguments);
    }

    public static void error(Object... arguments) {
        logVerbose(LogLevel.ERROR, arguments);
    }
}
