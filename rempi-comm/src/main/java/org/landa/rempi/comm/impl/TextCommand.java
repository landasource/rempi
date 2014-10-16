package org.landa.rempi.comm.impl;

import org.landa.rempi.comm.Command;

public class TextCommand implements Command {

    private final String message;

    /**
     * @param message
     */
    public TextCommand(final String message) {
        super();
        this.message = message;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

}
