package org.landa.rempi.server.io.livestream;

import io.pallas.core.annotations.Component;
import io.pallas.core.annotations.Configured;
import io.pallas.core.annotations.Startup;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.landa.rempi.server.io.pipeline.SecureServerPipelineFactory;

@Startup
@ApplicationScoped
@Component("liveStreamServer")
public class LiveStreamReceiverServer {

    private static final String DEFAULT_SERVER_PORT = "9010";

    @Inject
    @Configured(defaultValue = DEFAULT_SERVER_PORT)
    private Integer port;

    @Inject
    private Logger logger;

    @Inject
    private Executor executor;

    @Inject
    private LiveStreamHandler handler;

    @Inject
    private Decoder decoder;

    private ServerBootstrap bootstrap;

    @PostConstruct
    public void run() {
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(executor, executor));

        bootstrap.setPipelineFactory(new SecureServerPipelineFactory(false, handler, new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4), decoder));

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));

        logger.info("Stream receiver listening on: " + port);
    }

    @PreDestroy
    public void stop() {
        bootstrap.shutdown();
    }

    public Integer getPort() {
        return port;
    }

}
