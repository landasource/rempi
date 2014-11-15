package org.landa.rempi.client.modules.stream.live;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.landa.rempi.client.executors.Executor;
import org.landa.rempi.comm.livestream.StartStreamCommand;
import org.landa.rempi.comm.livestream.StopStreamCommand;
import org.landa.rempi.comm.livestream.handler.H264StreamEncoder;

import com.github.sarxos.webcam.Webcam;

public class LiveStreamHandler {

    protected final static Logger logger = Logger.getLogger(LiveStreamHandler.class.getSimpleName());

    protected volatile boolean isStreaming;
    private final Webcam webcam;
    private final Dimension dimension;
    private final ScheduledThreadPoolExecutor timeWorker;
    private final ExecutorService encodeWorker;
    private final H264StreamEncoder h264StreamEncoder;
    protected ScheduledFuture<?> imageGrabTaskFuture;
    protected int FPS = 25;

    private static LiveStreamHandler INSTANCE = null;

    private final Executor<StartStreamCommand> startExecutor = new StartExecutor();
    private final Executor<StopStreamCommand> stopExecutor = new StopExecutor();

    private volatile Channel channel;

    public static LiveStreamHandler instance() {
        if (null == INSTANCE) {
            INSTANCE = new LiveStreamHandler();
        }
        return INSTANCE;
    }

    public Executor<StartStreamCommand> startExecutor() {
        return startExecutor;
    }

    public Executor<StopStreamCommand> stopExecutor() {
        return stopExecutor;
    }

    private LiveStreamHandler() {

        Webcam.setAutoOpenMode(true);
        this.webcam = Webcam.getDefault();
        this.dimension = new Dimension(320, 240);
        webcam.setViewSize(dimension);

        this.timeWorker = new ScheduledThreadPoolExecutor(1);
        this.encodeWorker = Executors.newSingleThreadExecutor();
        this.h264StreamEncoder = new H264StreamEncoder(dimension, false);

    }

    private void startStream(final Channel channel) {

        if (!isStreaming) {
            this.channel = channel;
            //do some thing
            final Runnable imageGrabTask = new ImageGrabTask();
            final ScheduledFuture<?> imageGrabFuture = timeWorker.scheduleWithFixedDelay(imageGrabTask, 0, 1000 / FPS, TimeUnit.MILLISECONDS);
            imageGrabTaskFuture = imageGrabFuture;
            isStreaming = true;
        }
    }

    private void stopStream() {

        if (isStreaming) {
            //cancel the task
            imageGrabTaskFuture.cancel(false);
            webcam.close();
            isStreaming = false;

        }
    }

    protected volatile long frameCount = 0;

    private class ImageGrabTask implements Runnable {

        @Override
        public void run() {
            System.out.println("image grabed ,count :" + frameCount++);
            final BufferedImage bufferedImage = webcam.getImage();
            /**
             * using this when the h264 encoder is added to the pipeline
             */
            //channelGroup.write(bufferedImage);
            /**
             * using this when the h264 encoder is inside this class
             */
            encodeWorker.execute(new EncodeTask(bufferedImage));
        }

    }

    private class EncodeTask implements Runnable {
        private final BufferedImage image;

        public EncodeTask(final BufferedImage image) {
            super();
            this.image = image;
        }

        @Override
        public void run() {
            try {
                final Object msg = h264StreamEncoder.encode(image);
                if (msg != null) {
                    channel.write(msg);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class StartExecutor implements Executor<StartStreamCommand> {
        @Override
        public void execute(final StartStreamCommand command, final Channel channel) {
            instance().startStream(channel);
        }
    }

    private class StopExecutor implements Executor<StopStreamCommand> {
        @Override
        public void execute(final StopStreamCommand command, final Channel channel) {
            instance().stopStream();
        }
    }

}
