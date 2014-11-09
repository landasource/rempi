package org.landa.rempi.server.io.event;

public class OnClientConnected extends AbstractClientEvent {

    public OnClientConnected(final String clientId) {
        super(clientId);
    }

}
