/**
 *
 */
package org.landa.rempi.server.io.livestream;

import java.awt.image.BufferedImage;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.landa.rempi.comm.livestream.handler.StreamFrameListener;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ObserverStreamFrameListener implements StreamFrameListener {

    @Inject
    private Event<OnLiveStreamFrame> event;

    /*
     * (non-Javadoc)
     * @see
     * org.landa.rempi.comm.livestream.handler.StreamFrameListener#onFrameReceived
     * (java.awt.image.BufferedImage)
     */
    @Override
    public void onFrameReceived(final String clientId, final BufferedImage image) {

        event.fire(new OnLiveStreamFrame(clientId, image));
    }

}
