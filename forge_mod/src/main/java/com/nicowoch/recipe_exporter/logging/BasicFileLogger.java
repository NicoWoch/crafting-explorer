package com.nicowoch.recipe_exporter.logging;

import java.io.FileWriter;
import java.io.IOException;

public class BasicFileLogger {
    private static final int max_internal_buffer_size = 2000;

    private final String filename;
    private FileWriter writer = null;
    private String internal_buffer = "";


    public BasicFileLogger(String filename) {
        this.filename = filename;
    }

    public void open() {
        try {
            writer = new FileWriter(this.filename);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Fatal BasicFileLogger couldn't open file \"" + this.filename + "\"! (" + e + ")"
            );
        }
    }

    public void close() {
        flush();

        try {
            writer.close();
            writer = null;
        } catch (IOException e) {
            throw new RuntimeException(
                    "Fatal BasicFileLogger couldn't close file \"" + this.filename + "\"! (" + e + ")"
            );
        }
    }

    public boolean isOpen() {
        return writer != null;
    }

    public void log(String message) {
        if (internal_buffer.length() + message.length() > max_internal_buffer_size) {
            flush();
        }

        internal_buffer += message;
    }

    public void flush() {
        if (!isOpen()) {
            open();
        }

        try {
            writer.write(internal_buffer);
            internal_buffer = "";
        } catch (IOException e) {
            throw new RuntimeException(
                    "Fatal BasicFileLogger couldn't flush logged data to file \"" + this.filename + "\"! (" + e + ")"
            );
        }
    }

    public void clearAllLogs() {
        try {
            new FileWriter(this.filename, false).close();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Fatal BasicFileLogger couldn't create new or clear existing file \"" + this.filename + "\"! (" + e + ")"
            );
        }
    }
}
