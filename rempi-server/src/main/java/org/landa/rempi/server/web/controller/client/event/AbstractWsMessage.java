package org.landa.rempi.server.web.controller.client.event;

public abstract class AbstractWsMessage {
    private final String clientId;

    public AbstractWsMessage(final String clientId) {
        this.clientId = clientId;
    }

    public abstract String getEventType();

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

}
