package org.landa.rempi.server.io.livestream;

import java.awt.image.BufferedImage;

import org.landa.rempi.server.io.event.AbstractClientEvent;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class OnLiveStreamFrame extends AbstractClientEvent {

    private final BufferedImage image;

    public OnLiveStreamFrame(final String clientId, final BufferedImage image) {
        super(clientId);
        this.image = image;
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

}
