package org.landa.rempi.server.io;

import java.net.InetAddress;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FailedChannelFuture;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.ssl.SslHandler;
import org.landa.rempi.comm.Authentication;
import org.landa.rempi.comm.Command;
import org.landa.rempi.comm.ErrorMessage;
import org.landa.rempi.comm.InfoMessage;
import org.landa.rempi.comm.ServerGreeting;
import org.landa.rempi.comm.SyncCommand;
import org.landa.rempi.comm.SyncResult;
import org.landa.rempi.server.io.comm.Promise;
import org.landa.rempi.server.io.comm.WaitingPromise;
import org.landa.rempi.server.io.event.OnClientConnected;
import org.landa.rempi.server.io.event.OnClientDisconnected;
import org.landa.rempi.server.io.event.OnClientError;

/**
 * Handles both client-side and server-side handler depending on which
 * constructor was called.
 */
public class RempiServerHandler extends SimpleChannelUpstreamHandler {

    private final ConcurrentMap<String, Integer> clients = new ConcurrentHashMap<>();
    private final ChannelGroup channels = new DefaultChannelGroup("all");

    @Inject
    private BeanManager beanManager;

    @Inject
    private org.apache.log4j.Logger logger;

    /**
     * Key: command id, Value: promise
     */
    private final ConcurrentMap<Integer, WaitingPromise> syncPromises = new ConcurrentHashMap<Integer, WaitingPromise>();

    @Override
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent && ((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS) {
            logger.info(e.toString());
        }

        super.handleUpstream(ctx, e);
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {

        // Get the SslHandler in the current pipeline.
        // We added it in SecureChatPipelineFactory.
        final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);

        // Get notified when SSL handshake is done.
        final ChannelFuture handshakeFuture = sslHandler.handshake();
        handshakeFuture.addListener(new Greeter(sslHandler, channels));

    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        super.messageReceived(ctx, e);

        final Integer channelId = e.getChannel().getId();
        final Object message = e.getMessage();

        final WaitingPromise promise = syncPromises.get(channelId);
        // TODO handle expected
        if (message instanceof SyncResult) {

            final SyncResult syncResult = (SyncResult) message;

            if (null != promise) {

                if (promise.getExpectedId().equals(syncResult.getId())) {
                    promise.getPromise().succeed(syncResult.getResult());
                    syncPromises.remove(syncResult.getId());
                } else {
                    logger.info("No expected sync result");
                    promise.getPromise().fail();
                }
            } else {
                logger.info("No promise waiting for:" + syncResult.getId());
            }

        } else if (message instanceof Authentication) {
            final Authentication authentication = (Authentication) message;
            final String clientId = authentication.getClientId();

            channels.add(e.getChannel());
            clients.put(clientId, e.getChannel().getId());

            logger.info("Client authenticated: " + clientId);

            // fire event
            final OnClientConnected connected = new OnClientConnected(clientId);
            beanManager.fireEvent(connected);

        } else if (message instanceof ErrorMessage) {

            final OnClientError clientError = new OnClientError(getClientIdOfChannel(channelId), (ErrorMessage) message);
            beanManager.fireEvent(clientError);

        } else if (message instanceof InfoMessage) {

            logger.info("Stati info from client: " + message.toString());

        } else {
            logger.info("Message from client: " + message.toString());

        }
    }

    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {

        final Integer channelId = e.getChannel().getId();

        String clientId = null;
        for (final Entry<String, Integer> entry : clients.entrySet()) {
            if (entry.getValue().equals(channelId)) {
                clientId = entry.getKey();
            }
        }

        channels.remove(e.getChannel());

        if (null != clientId) {
            clients.remove(clientId);
            beanManager.fireEvent(new OnClientDisconnected(clientId));
        }

        super.channelDisconnected(ctx, e);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        logger.warn("Unexpected exception from downstream.", e.getCause());
        //        e.getChannel().close();

    }

    public void broadcast(final String command) {
        channels.write(command);
    }

    /**
     * @return the clients
     */
    public ConcurrentMap<String, Integer> getClients() {
        return clients;
    }

    public ChannelFuture send(final String clientId, final Command command) {

        final Channel channel = getChannelByClientId(clientId);
        if (null == channel) {
            logger.error("Unknown client: " + clientId);
            throw new IllegalArgumentException("Unknown client:" + clientId);
        } else if (channel.isWritable()) {

            logger.info("Send command: " + command);

            final ChannelFuture future = channel.write(command);

            return future;
        }

        return new FailedChannelFuture(channel, new IllegalArgumentException("Unknown client:" + clientId));

    }

    public void disconnetClient(final String clientId) {
        final Channel channel = getChannelByClientId(clientId);
        if (null == channel) {
            logger.error("Unknown client: " + clientId);
        } else if (channel.isConnected()) {
            channel.disconnect();
        }
    }

    private Channel getChannelByClientId(final String clientId) {

        final Integer channelId = clients.get(clientId);
        if (null != channelId) {
            return channels.find(channelId);
        }

        return null;
    }

    /**
     * @param channelId
     * @return
     */
    private String getClientIdOfChannel(final Integer channelId) {

        for (final Entry<String, Integer> entry : clients.entrySet()) {

            if (entry.getValue().equals(channelId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private class Greeter implements ChannelFutureListener {

        private final SslHandler sslHandler;
        private final ChannelGroup channelGroup;

        Greeter(final SslHandler sslHandler, final ChannelGroup channelGroup) {
            this.sslHandler = sslHandler;
            this.channelGroup = channelGroup;

        }

        @Override
        public void operationComplete(final ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                // Once session is secured, send a greeting.
                final String welcome = "Welcome to " + InetAddress.getLocalHost().getHostName() + " secure Rempi server!\n" + "Your session is protected by "
                        + sslHandler.getEngine().getSession().getCipherSuite() + " cipher suite.\n";

                future.getChannel().write(new ServerGreeting(welcome));

                // Register the channel to the global channel list
                // so the channel received the messages from others.
                channelGroup.add(future.getChannel());

            } else {
                logger.info("Greeting close immediately");
                future.getChannel().close();
            }
        }
    }

    public Promise<Object> sendSyncCommand(final String clientId, final SyncCommand command) {
        final Promise<Object> promise = new Promise<Object>();

        final Channel channel = getChannelByClientId(clientId);

        syncPromises.put(channel.getId(), new WaitingPromise(command.getId(), promise));

        Executors.newSingleThreadExecutor().execute(new Runnable() {

            @Override
            public void run() {
                System.out.println("Send sync command");
                send(clientId, command);
            }
        });

        return promise;
    }
}