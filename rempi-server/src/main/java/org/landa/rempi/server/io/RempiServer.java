package org.landa.rempi.server.io;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.landa.rempi.comm.Command;
import org.landa.rempi.comm.SyncCommand;
import org.landa.rempi.comm.livestream.handler.StreamFrameListener;
import org.landa.rempi.server.io.comm.Promise;
import org.landa.rempi.server.io.ssh.SecureServerPipelineFactory;

/**
 * Modification of {@link EchoServer} which utilizes Java object serialization.
 */
public class RempiServer {

    private final int port;
    private ServerBootstrap bootstrap;

    private final RempiServerHandler rempiServerHandler;
    private final Executor executor;
    private StreamFrameListener streamListener;

    public RempiServer(final int port, final Executor executor, final RempiServerHandler handler) {
        this.port = port;
        this.executor = executor;
        rempiServerHandler = handler;

    }

    public void run() {
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(executor, executor));

        bootstrap.setPipelineFactory(new SecureServerPipelineFactory(rempiServerHandler, new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(Command.class
                .getClassLoader()))));

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