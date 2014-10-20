package org.landa.rempi.client.modules.webcam;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.jboss.netty.channel.Channel;
import org.landa.rempi.client.executors.Executor;
import org.landa.rempi.comm.impl.CaptureCommand;
import org.landa.rempi.comm.impl.CaptureResponse;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.fswebcam.FsWebcamDriver;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Capturer implements Executor<CaptureCommand> {
    // set capture driver for fswebcam tool
    static {
        Webcam.setDriver(new FsWebcamDriver());
    }

    @Override
    public void execute(final CaptureCommand command, final Channel channel) {

        try {
            System.out.println("Capturing image");
            final BufferedImage captureImage = captureImage();

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(captureImage, "jpg", baos);
            final byte[] bytes = baos.toByteArray();

            System.out.println("Send image");

            channel.write(new CaptureResponse(command, bytes));

        } catch (final Exception exception) {
            channel.write(new CaptureResponse(command, null));
        }
    }

    /**
     * @return captured image from webcam
     */
    public BufferedImage captureImage() {

        // get default webcam and open it
        final Webcam webcam = Webcam.getDefault();
        webcam.open();

        // get image from webcam device
        final BufferedImage image = webcam.getImage();

        // close webcam
        webcam.close();

        return image;
    }
}
