package org.landa.rempi.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.landa.rempi.client.ssh.SecureClientPipelineFactory;

/**
 * Modification of {@link EchoClient} which utilizes Java object serialization.
 */
public class RempiClient {

    // Sleep 5 seconds before a reconnection attempt.
    static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "3"));

    private final String host;
    private final int port;
    private final String id;

    public RempiClient(final String host, final int port, final String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void run() {

        // Initialize the timer that schedules subsequent reconnection attempts.
        final Timer timer = new HashedWheelTimer();

        // Configure the client.
        final ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new SecureClientPipelineFactory(bootstrap, timer, id));

        // Start the connection attempt.
        final InetSocketAddress serverAddress = new InetSocketAddress(host, port);
        bootstrap.setOption("remoteAddress", serverAddress);
        bootstrap.connect();

        System.out.println("Client connected to: " + serverAddress.getHostName() + ":" + serverAddress.getPort());

    }
}
