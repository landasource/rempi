package org.landa.rempi.server.web.controller.client.event;

public class ClientDisconnectedMessage extends AbstractWsMessage {

    public ClientDisconnectedMessage(final String clientId) {
        super(clientId);
    }

    @Override
    public String getEventType() {
        return "disconnected";
    }

}
