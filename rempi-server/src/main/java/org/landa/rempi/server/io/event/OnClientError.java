package org.landa.rempi.server.io.event;

import org.landa.rempi.comm.ErrorMessage;

/**
 * When something bad happened on client.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class OnClientError extends AbstractClientEvent {

    private final ErrorMessage errorMessage;

    public OnClientError(final String clientId, final ErrorMessage message) {
        super(clientId);
        this.errorMessage = message;
    }

    /**
     * @return the errorMessage
     */
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

}
