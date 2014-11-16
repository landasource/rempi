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
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.landa.rempi.client.executors.Executor;
import org.landa.rempi.comm.livestream.StartStreamCommand;
import org.landa.rempi.comm.livestream.StopStreamCommand;
import org.landa.rempi.comm.livestream.handler.H264StreamEncoder;

import com.github.sarxos.webcam.Webcam;

public class LiveStreamHandler extends SimpleChannelUpstreamHandler {

    protected final static Logger logger = Logger.getLogger(LiveStreamHandler.class.getSimpleName());

    protected volatile boolean isStreaming;
    private Webcam webcam;
    private Dimension dimension;
    private ScheduledThreadPoolExecutor timeWorker;
    private ExecutorService encodeWorker;
    private H264StreamEncoder h264StreamEncoder;
    protected ScheduledFuture<?> imageGrabTaskFuture;
    protected int FPS = 25;

    private static LiveStreamHandler INSTANCE = null;

    private final Executor<StartStreamCommand> startExecutor = new StartExecutor();
    private final Executor<StopStreamCommand> stopExecutor = new StopExecutor();

    // private volatile Channel channel;

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
    }

    private void startStream(final int port) {

        if (!isStreaming) {

            isStreaming = true;
            liveStreamClient = new LiveStreamClient(port, this);
        }
    }

    private void stopStream() {

        if (isStreaming) {
            // cancel the task
            if (null != imageGrabTaskFuture) {
                imageGrabTaskFuture.cancel(false);
            }
            if (null != webcam) {
                webcam.close();
            }
            Webcam.getDiscoveryService().stop();
            isStreaming = false;
            if (null != liveStreamClient) {
                liveStreamClient.stop();
            }

        }
    }

    protected volatile long frameCount = 0;

    private LiveStreamClient liveStreamClient;

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {

        //        // Get the SslHandler from the pipeline
        //        // which were added in SecureChatPipelineFactory.
        //        final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
        //        // Begin handshake.
        //        final ChannelFuture channelFuture = sslHandler.handshake();
        //
        //        channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        //        channelFuture.addListener(new ChannelFutureListener() {
        //            @Override
        //            public void operationComplete(final ChannelFuture future) throws Exception {
        //                // if (future.isSuccess()) {
        //                // Commandor.addExecutor(ServerGreeting.class, new
        //                // Authenticator(RempiClient.getID()));
        //                // }
        //                if (future.isSuccess()) {
        //                    streamToChannel(future.getChannel());
        //                } else {
        //                    stopStream();
        //                }
        //
        //            }
        //        });

        streamToChannel(e.getChannel());
    }

    private void streamToChannel(final Channel channel) {

        Webcam.setAutoOpenMode(true);
        this.webcam = Webcam.getDefault();
        this.dimension = new Dimension(320, 240);
        webcam.setViewSize(dimension);

        this.timeWorker = new ScheduledThreadPoolExecutor(1);
        this.encodeWorker = Executors.newSingleThreadExecutor();
        this.h264StreamEncoder = new H264StreamEncoder(dimension, false);

        // this.channel = channel;
        // do some thing
        final Runnable imageGrabTask = new ImageGrabTask(channel);
        imageGrabTaskFuture = timeWorker.scheduleWithFixedDelay(imageGrabTask, 0, 1000 / FPS, TimeUnit.MILLISECONDS);

    }

    // ------------------------- Private classes
    // -----------------------------------------------
    // _________________________________________________________________________________________
    // -----------------------------------------------------------------------------------------

    private class ImageGrabTask implements Runnable {

        private final Channel channel;

        public ImageGrabTask(final Channel channel) {
            this.channel = channel;

        }

        @Override
        public void run() {
            // System.out.println("image grabed ,count :" + frameCount++);
            final BufferedImage bufferedImage = webcam.getImage();
            /**
             * using this when the h264 encoder is added to the pipeline
             */
            // channelGroup.write(bufferedImage);
            /**
             * using this when the h264 encoder is inside this class
             */
            encodeWorker.execute(new EncodeTask(bufferedImage, channel));
        }

    }

    private class EncodeTask implements Runnable {
        private final BufferedImage image;
        private final Channel channel;

        public EncodeTask(final BufferedImage image, final Channel channel) {
            super();
            this.image = image;
            this.channel = channel;
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
            instance().startStream(command.getPort());
        }
    }

    private class StopExecutor implements Executor<StopStreamCommand> {
        @Override
        public void execute(final StopStreamCommand command, final Channel channel) {
            instance().stopStream();
        }
    }

}
