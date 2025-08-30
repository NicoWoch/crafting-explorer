package com.nicowoch.recipe_exporter.logging;

import mcp.MethodsReturnNonnullByDefault;

import java.io.PrintWriter;
import java.io.StringWriter;

@MethodsReturnNonnullByDefault
public class LogData {
    public LogLevel level;
    public Object[] arguments;

    public LogData(LogLevel level, Object[] arguments) {
        this.level = level;
        this.arguments = arguments;
    }

    public String getShortMessage() {
        if (arguments.length == 0) {
            return "Log data not specified";
        } else if (arguments.length == 1) {
            return arguments[0].toString();
        } else {
            return arguments[0].toString() + " (+" + (arguments.length - 1) + ")";
        }
    }

    public String getLongText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Level: ").append(level);

        for (Object arg : arguments) {
            sb.append("\n").append(parseArgument(arg));
        }

        return sb.toString();
    }

    public String parseArgument(Object arg) {
        if (arg == null) {
            return ">>> Null Argument <<<";
        }

        if (arg instanceof Throwable) {
            Throwable throwable = (Throwable) arg;

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);

            return "Exception Details:\n" +
                    "Class: " + throwable.getClass().getName() + "\n" +
                    "Message: " + throwable.getMessage() + "\n" +
                    "Stack Trace:\n" + sw + "\n\n";
        }

        return "[" + arg.getClass().getSimpleName() + "] " + arg;
    }
}
