package org.landa.rempi.client.modules.auth;

import org.jboss.netty.channel.Channel;
import org.landa.rempi.client.executors.Commandor;
import org.landa.rempi.client.executors.Executor;
import org.landa.rempi.comm.Authentication;
import org.landa.rempi.comm.ServerGreeting;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Authenticator implements Executor<ServerGreeting> {

    private final String clientId;

    /**
     * @param clientId
     *            client id
     */
    public Authenticator(final String clientId) {
        super();
        this.clientId = clientId;
    }

    @Override
    public void execute(final ServerGreeting command, final Channel channel) {
        Commandor.removeExecutor(ServerGreeting.class);
        System.out.println("Authenticate");
        channel.write(new Authentication(clientId));

    }
}
