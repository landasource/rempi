package org.landa.rempi.server.io;

import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;

import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@Singleton
public class RempiService implements Provider<RempiServer> {

    private final NinjaProperties ninjaProperties;

    /**
     * @param ninjaProperties
     */
    @javax.inject.Inject
    public RempiService(final NinjaProperties ninjaProperties) {
        super();
        this.ninjaProperties = ninjaProperties;
    }

    private RempiServer rempiServer;

    @Start
    public void startService() {

        final int port = ninjaProperties.getIntegerWithDefault("rempi.server.port", 9000);

        rempiServer = new RempiServer(port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Stop rempi server");
                rempiServer.stop();
            }
        });

        rempiServer.run();

        System.out.println("Rempi server started as port: " + port);
    }

    @Dispose
    public void stopService() {
        rempiServer.stop();
    }

    @Override
    public RempiServer get() {
        return rempiServer;
    }

}
