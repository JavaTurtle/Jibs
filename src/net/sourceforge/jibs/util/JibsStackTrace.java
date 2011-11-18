package net.sourceforge.jibs.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Simple utilities to return the stack trace of an exception as a String.
 */
public final class JibsStackTrace {
    public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);

        aThrowable.printStackTrace(printWriter);

        return result.toString();
    }

    public static String getCustomStackTrace(Throwable aThrowable) {
        // add the class name and any message passed to constructor
        final StringBuffer result = new StringBuffer("");

        result.append(aThrowable.toString());

        final String NEW_LINE = System.getProperty("line.separator");

        result.append(NEW_LINE);

        // add each element of the stack trace
        StackTraceElement[] elements = aThrowable.getStackTrace();

        for (int i = 0; i < elements.length; i++) {
            StackTraceElement element = elements[i];

            result.append("        at ");
            result.append(element);

            if (i < (elements.length - 1)) {
                result.append(NEW_LINE);
            }
        }

        return result.toString();
    }
}
