package org.landa.rempi.client.executors;

import org.jboss.netty.channel.Channel;
import org.landa.rempi.comm.Command;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public interface Executor<T extends Command> {

    void execute(T command, Channel channel);

}
