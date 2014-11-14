package org.landa.rempi.comm;

import org.landa.rempi.comm.util.ExceptionFormatter;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ErrorMessage extends InfoMessage {

    private final String stackTrace;

    public ErrorMessage(final String text, final Throwable cause) {
        super(text);
        this.stackTrace = ExceptionFormatter.toString(cause);
    }

    /**
     * @return the stackTrace
     */
    public String getStackTrace() {
        return stackTrace;
    }

    private static final long serialVersionUID = -5365452259800186871L;

}
