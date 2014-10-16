package org.landa.rempi.comm;

import java.io.Serializable;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ServerGreeting implements Serializable {

    private String message;

    /**
     * @param message
     */
    public ServerGreeting(final String message) {
        super();
        this.message = message;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

}
