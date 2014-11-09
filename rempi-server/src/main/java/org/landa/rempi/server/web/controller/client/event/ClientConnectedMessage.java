package org.landa.rempi.server.web.controller.client.event;

public class ClientConnectedMessage extends AbstractWsMessage {

    public ClientConnectedMessage(final String clientId) {
        super(clientId);
    }

    @Override
    public String getEventType() {
        return "connected";
    }

}
