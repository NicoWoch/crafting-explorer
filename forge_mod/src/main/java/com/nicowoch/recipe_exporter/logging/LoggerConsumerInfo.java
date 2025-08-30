package com.nicowoch.recipe_exporter.logging;

import java.util.function.Consumer;

public class LoggerConsumerInfo {
    Consumer<LogData> consumer;
    LogLevel level;

    public LoggerConsumerInfo(Consumer<LogData> consumer, LogLevel level) {
        this.consumer = consumer;
        this.level = level;
    }
}
