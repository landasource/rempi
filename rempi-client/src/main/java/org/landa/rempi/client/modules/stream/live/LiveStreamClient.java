package org.landa.rempi.client.modules.stream.live;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.landa.rempi.client.RempiClient;
import org.landa.rempi.client.pipeline.SecureClientPipelineFactory;

class LiveStreamClient {

    private final ClientBootstrap bootstrap;

    public LiveStreamClient(final int port, final ChannelUpstreamHandler channelUpstreamHandler) {

        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new SecureClientPipelineFactory(new LengthFieldPrepender(4, false), channelUpstreamHandler));

        // Start the connection attempt.
        final InetSocketAddress serverAddress = new InetSocketAddress(RempiClient.getHost(), port);
        bootstrap.setOption("remoteAddress", serverAddress);
        bootstrap.connect();

    }

    public void stop() {
        bootstrap.shutdown();
    }
}
