package org.landa.rempi.server.web.controller.client;

import io.pallas.core.util.Json;
import io.pallas.core.ws.Broadcaster;
import io.pallas.core.ws.events.OnOpen;
import io.pallas.core.ws.events.WebSocket;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.landa.rempi.server.io.event.OnClientConnected;
import org.landa.rempi.server.io.event.OnClientDisconnected;
import org.landa.rempi.server.web.controller.client.event.ClientConnectedMessage;
import org.landa.rempi.server.web.controller.client.event.ClientDisconnectedMessage;

@ApplicationScoped
public class ClientWsController {

    @Inject
    @WebSocket(path = "/ws/rempi")
    private Broadcaster broadcaster;

    public void onWsConnection(@Observes @WebSocket(path = "/ws/rempi") final OnOpen event) {

    }

    public void onCLientConnected(@Observes final OnClientConnected clientConnected) {

        final String json = Json.create().toJsonText(new ClientConnectedMessage(clientConnected.getClientId()));

        broadcaster.broadcast(json);
    }

    public void onClientDicsonnected(@Observes final OnClientDisconnected disconnected) {

        final String json = Json.create().toJsonText(new ClientDisconnectedMessage(disconnected.getClientId()));

        broadcaster.broadcast(json);
    }

}
