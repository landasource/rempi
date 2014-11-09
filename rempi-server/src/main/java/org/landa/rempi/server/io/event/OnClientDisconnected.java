package org.landa.rempi.server.io.event;

public class OnClientDisconnected extends AbstractClientEvent {

    public OnClientDisconnected(final String clientId) {
        super(clientId);
    }

}
