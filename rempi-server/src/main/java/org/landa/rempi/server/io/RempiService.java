package org.landa.rempi.server.io;

import io.pallas.core.annotations.Startup;

import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.log4j.Logger;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@Startup
@ApplicationScoped
public class RempiService {

    private RempiServer rempiServer;

    @Inject
    private Logger logger;

    @Inject
    private Executor executor;

    @Inject
    private RempiServerHandler handler;

    @PostConstruct
    private void startService() {

        final int port = 9000;

        rempiServer = new RempiServer(port, executor, handler);

        rempiServer.run();

        logger.info("Rempi server started as port: " + port);
    }

    @PreDestroy
    public void stopService() {
        logger.info("Stop rempi server");
        rempiServer.stop();
    }

    @Produces
    public RempiServer getRempiServer() {
        return rempiServer;
    }

}
