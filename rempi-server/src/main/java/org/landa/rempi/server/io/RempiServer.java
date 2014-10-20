package org.landa.rempi.server.io;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.landa.rempi.comm.Command;
import org.landa.rempi.comm.SyncCommand;
import org.landa.rempi.server.io.comm.Promise;
import org.landa.rempi.server.io.ssh.SecureServerPipelineFactory;

/**
 * Modification of {@link EchoServer} which utilizes Java object serialization.
 */
public class RempiServer {

    private final int port;
    private ServerBootstrap bootstrap;

    final RempiServerHandler rempiServerHandler = new RempiServerHandler();

    public RempiServer(final int port) {
        this.port = port;
    }

    public void run() {
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new SecureServerPipelineFactory(rempiServerHandler));

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
    }

    public void stop() {
        bootstrap.shutdown();
    }

    public void broadcast(final String command) {
        rempiServerHandler.broadcast(command);
    }

    public ConcurrentMap<String, Integer> getClients() {
        return rempiServerHandler.getClients();
    }

    public void sendCommand(final String clientId, final Command command) {
        rempiServerHandler.send(clientId, command);
    }

    public void disconnet(final String clientId) {
        rempiServerHandler.disconnetClient(clientId);
    }

    public Promise<Object> sendSyncCommand(final String clientId, final SyncCommand captureCommand) {

        return rempiServerHandler.sendSyncCommand(clientId, captureCommand);
    }

}