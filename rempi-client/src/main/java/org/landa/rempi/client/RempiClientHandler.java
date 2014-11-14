package org.landa.rempi.client;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.landa.rempi.client.executors.Commandor;
import org.landa.rempi.client.modules.auth.Authenticator;
import org.landa.rempi.comm.Command;
import org.landa.rempi.comm.ErrorMessage;
import org.landa.rempi.comm.ServerGreeting;

/**
 * Handler implementation for the object echo client. It initiates the ping-pong
 * traffic between the object echo client and server by sending the first
 * message to the server.
 */
public class RempiClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(RempiClientHandler.class.getName());
    private final ClientBootstrap bootstrap;
    private final Timer timer;
    private long startTime = -1;
    private final String clientId;

    public RempiClientHandler(final ClientBootstrap bootstrap, final Timer timer, final String clientId) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.clientId = clientId;
    }

    @Override
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent && ((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS) {
            logger.info(e.toString());
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        if (startTime < 0) {
            startTime = System.currentTimeMillis();
        }

        // Get the SslHandler from the pipeline
        // which were added in SecureChatPipelineFactory.
        final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
        // Begin handshake.
        final ChannelFuture channelFuture = sslHandler.handshake();

        channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        channelFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    Commandor.addExecutor(ServerGreeting.class, new Authenticator(clientId));
                }

            }
        });

        //super.channelConnected(ctx, e);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final Object message = e.getMessage();

        if (message instanceof Command) {
            new Commandor().execute((Command) message, e.getChannel());
        } else {
            logger.warning("Unknown message: " + message);
        }

        super.messageReceived(ctx, e);
    }

    InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) bootstrap.getOption("remoteAddress");
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) {
        logger.info("Sleeping for: " + RempiClient.RECONNECT_DELAY + 's');
        timer.newTimeout(new TimerTask() {

            @Override
            public void run(final Timeout timeout) throws Exception {
                logger.info("Reconnecting to: " + getRemoteAddress());
                bootstrap.connect();
            }
        }, RempiClient.RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        //  logger.log(Level.WARNING, "Unexpected exception from downstream.", e.getCause());

        final Throwable cause = e.getCause();
        if (cause instanceof ConnectException) {
            startTime = -1;
            logger.severe("Failed to connect: " + cause.getMessage());
        }
        if (cause instanceof ReadTimeoutException) {
            // The connection was OK but there was no traffic for last period.
            logger.severe("Disconnecting due to no inbound traffic");
        } else {
            logger.log(Level.SEVERE, "Unexpected error", cause);
        }

        // notify server
        final Channel channel = ctx.getChannel();
        if (channel.isWritable()) {
            channel.write(new ErrorMessage("Error", cause));
        }

        //ctx.getChannel().close();
    }
}