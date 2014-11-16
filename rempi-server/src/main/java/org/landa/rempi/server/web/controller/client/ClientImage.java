package org.landa.rempi.server.web.controller.client;

import org.landa.rempi.server.web.controller.client.event.AbstractWsMessage;

public class ClientImage extends AbstractWsMessage {

    private final String base64Image;

    public ClientImage(final String clientId, final String image) {
        super(clientId);
        base64Image = image;
    }

    @Override
    public String getEventType() {
        return "image";
    }

    /**
     * @return the base64Image
     */
    public String getBase64Image() {
        return base64Image;
    }

}
