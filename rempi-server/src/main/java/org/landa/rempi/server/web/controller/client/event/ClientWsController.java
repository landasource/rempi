package org.landa.rempi.server.web.controller.client.event;

import io.pallas.core.ws.Broadcaster;
import io.pallas.core.ws.events.OnOpen;
import io.pallas.core.ws.events.WebSocket;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.shiro.codec.Base64;
import org.landa.rempi.server.io.event.OnClientConnected;
import org.landa.rempi.server.io.event.OnClientDisconnected;
import org.landa.rempi.server.io.event.OnClientError;
import org.landa.rempi.server.io.event.OnClientImage;
import org.landa.rempi.server.io.livestream.OnLiveStreamFrame;
import org.landa.rempi.server.web.controller.client.ClientImage;

@ApplicationScoped
public class ClientWsController {

    @Inject
    @WebSocket(path = "/ws/rempi")
    private Broadcaster broadcaster;

    public void onWsConnection(@Observes @WebSocket(path = "/ws/rempi") final OnOpen event) {

    }

    public void onClientConnected(@Observes final OnClientConnected clientConnected) {
        broadcaster.broadcastJson(new ClientConnectedMessage(clientConnected.getClientId()));
    }

    public void onClientDicsonnected(@Observes final OnClientDisconnected disconnected) {
        broadcaster.broadcastJson(new ClientDisconnectedMessage(disconnected.getClientId()));
    }

    /**
     * @param clientError
     */
    public void onClientError(@Observes final OnClientError clientError) {
        broadcaster.broadcastJson(new ClientErrorMessage(clientError.getClientId(), clientError.getErrorMessage()));
    }

    public void onLiveStreamImage(@Observes final OnLiveStreamFrame frame) {
        System.out.println("ClientWsController.onLiveStreamImage()");
    }

    public void onImage(@Observes final OnClientImage clientImage) {

        final String image = Base64.encodeToString(clientImage.getImage());
        broadcaster.broadcastJson(new ClientImage(clientImage.getClientId(), image));
    }

}
