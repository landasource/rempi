package org.landa.rempi.client.modules.stream.simplelive;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.jboss.netty.channel.Channel;
import org.landa.rempi.client.executors.Executor;
import org.landa.rempi.comm.CapturedImage;
import org.landa.rempi.comm.livestream.StartStreamCommand;
import org.landa.rempi.comm.livestream.StopStreamCommand;

import com.github.sarxos.webcam.Webcam;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Streamer {

    private static Streamer INST = null;
    private ScheduledThreadPoolExecutor timerExecutor;
    private ScheduledFuture<?> scheduledFuture;
    private final Webcam webcam;
    private final Dimension dimension;

    public static Streamer instance() {
        if (null == INST) {
            INST = new Streamer();
        }
        return INST;
    }

    static {
        Webcam.setAutoOpenMode(false);

        Webcam.getDiscoveryService().stop();
    }

    private Streamer() {
        Webcam.getDiscoveryService().scan();
        this.webcam = Webcam.getDefault();
        this.dimension = new Dimension(320, 240);
        webcam.setViewSize(dimension);
        this.webcam.open();
    }

    private void start(final Channel channel) {

        if (null == timerExecutor) { // if not runs

            timerExecutor = new ScheduledThreadPoolExecutor(1);
            final Runnable grabCommand = new ImageGrabber(channel);
            scheduledFuture = timerExecutor.scheduleAtFixedRate(grabCommand, 0, 1000 / 25, TimeUnit.MILLISECONDS);
        }

    }

    public void stop(final Channel channel) {

        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            timerExecutor = null;
            scheduledFuture = null;
        }

    }

    public Executor<StartStreamCommand> streamStarter() {
        return new Executor<StartStreamCommand>() {
            @Override
            public void execute(final StartStreamCommand command, final Channel channel) {
                start(channel);
            }
        };
    }

    public Executor<StopStreamCommand> streamStopper() {
        return new Executor<StopStreamCommand>() {
            @Override
            public void execute(final StopStreamCommand command, final Channel channel) {
                stop(channel);
            }
        };
    }

    private class ImageGrabber implements Runnable {

        private final Channel channel;

        public ImageGrabber(final Channel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            try {
                final BufferedImage image = webcam.getImage();
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                final byte[] byteArray = baos.toByteArray();

                channel.write(new CapturedImage(byteArray));
            } catch (final IOException exception) {
                System.err.println(exception);
            }
        }
    }

}
