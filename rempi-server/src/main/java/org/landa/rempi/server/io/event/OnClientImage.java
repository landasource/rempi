package org.landa.rempi.server.io.event;

public class OnClientImage extends AbstractClientEvent {

    private final byte[] image;

    public OnClientImage(final String clientId, final byte[] bs) {
        super(clientId);
        this.image = bs;
    }

    /**
     * @return the image
     */
    public byte[] getImage() {
        return image;
    }

}
