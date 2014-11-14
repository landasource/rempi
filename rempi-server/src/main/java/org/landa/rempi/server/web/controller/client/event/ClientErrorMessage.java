package org.landa.rempi.server.web.controller.client.event;

import org.landa.rempi.comm.ErrorMessage;

/**
 *
 */
public class ClientErrorMessage extends AbstractWsMessage {

    private final ErrorMessage errorMessage;

    public ClientErrorMessage(final String clientId, final ErrorMessage errorMessage) {
        super(clientId);
        this.errorMessage = errorMessage;
    }

    @Override
    public String getEventType() {
        return "error";
    }

    /**
     * @return the errorMessage
     */
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

}
