package org.landa.rempi.server.io.event;

public class AbstractClientEvent {
    private final String clientId;

    public AbstractClientEvent(final String clientId) {
        this.clientId = clientId;

    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

}
