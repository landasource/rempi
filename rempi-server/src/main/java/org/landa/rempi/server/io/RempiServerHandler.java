package org.landa.rempi.server.io;

import java.net.InetAddress;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.ssl.SslHandler;
import org.landa.rempi.comm.Authentication;
import org.landa.rempi.comm.Command;
import org.landa.rempi.comm.ServerGreeting;
import org.landa.rempi.comm.impl.TextCommand;

/**
 * Handles both client-side and server-side handler depending on which
 * constructor was called.
 */
public class RempiServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(RempiServerHandler.class.getName());

    private final ConcurrentMap<String, Integer> clients = new ConcurrentHashMap<>();
    private final ChannelGroup channels = new DefaultChannelGroup("all");

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

        final Object message = e.getMessage();
        if (message instanceof Authentication) {
            final Authentication authentication = (Authentication) message;
            final String clientId = authentication.getClientId();

            channels.add(e.getChannel());
            clients.put(clientId, e.getChannel().getId());

            logger.info("Client authenticated: " + clientId);

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

        if (null != clientId) {
            clients.remove(clientId);
        }
        channels.remove(e.getChannel());

        super.channelDisconnected(ctx, e);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", e.getCause());
        //        e.getChannel().close();

    }

    public void broadcast(final String command) {

        channels.write(new TextCommand(command));
    }

    /**
     * @return the clients
     */
    public ConcurrentMap<String, Integer> getClients() {
        return clients;
    }

    public void send(final String clientId, final Command command) {

        final Channel channel = getChannelByClientId(clientId);
        if (null == channel) {
            logger.severe("Unknown client: " + clientId);
        } else if (channel.isWritable()) {
            logger.info("Send command: " + command);
            channel.write(command);
        }

    }

    public void disconnetClient(final String clientId) {
        final Channel channel = getChannelByClientId(clientId);
        if (null == channel) {
            logger.severe("Unknown client: " + clientId);
        } else if (channel.isConnected()) {
            clients.remove(clientId);
            channels.remove(channel);
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

    private static final class Greeter implements ChannelFutureListener {

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

}