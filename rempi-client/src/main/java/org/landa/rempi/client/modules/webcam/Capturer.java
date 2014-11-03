package org.landa.rempi.client.modules.webcam;

import java.awt.Dimension;
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

    static Webcam webcam;

    // set capture driver for fswebcam tool
    static {
        Webcam.setDriver(new FsWebcamDriver());
        webcam = Webcam.getDefault();

        final Dimension[] viewSizes = webcam.getViewSizes();
        Dimension maxDimension = viewSizes[0];

        for (final Dimension dimension : viewSizes) {
            if (dimension.height > maxDimension.height) {
                maxDimension = dimension;
            }
        }
        webcam.setViewSize(maxDimension);

        webcam.open();

        webcam.open();
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
            exception.printStackTrace(System.err);
            channel.write(new CaptureResponse(command, null));
        }
    }

    /**
     * @return captured image from webcam
     */
    public BufferedImage captureImage() {

        // get default webcam and open it

        // get image from webcam device
        final BufferedImage image = webcam.getImage();

        return image;
    }
}
