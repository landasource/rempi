package org.landa.rempi.comm.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionFormatter {

    public static final String toString(final Throwable throwable) {

        final StringBuilder builder = new StringBuilder();

        builder.append("\n");
        builder.append(throwable.getMessage());
        final StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        builder.append(writer.toString());

        return builder.toString();
    }

}
