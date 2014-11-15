package org.landa.rempi.client.executors;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.landa.rempi.client.modules.stream.live.LiveStreamHandler;
import org.landa.rempi.client.modules.webcam.Capturer;
import org.landa.rempi.comm.Command;
import org.landa.rempi.comm.impl.CaptureCommand;
import org.landa.rempi.comm.livestream.StartStreamCommand;
import org.landa.rempi.comm.livestream.StopStreamCommand;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Commandor {

    private static final Map<Class<?>, Class<?>> executors = new HashMap<Class<?>, Class<?>>();
    private static final Map<Class<?>, Executor> executorsInstances = new HashMap<Class<?>, Executor>();

    static {
        executors.put(CaptureCommand.class, Capturer.class);

        executorsInstances.put(StartStreamCommand.class, LiveStreamHandler.instance().startExecutor());
        executorsInstances.put(StopStreamCommand.class, LiveStreamHandler.instance().stopExecutor());
    }

    public static <T extends Command> void addExecutor(final Class<T> command, final Executor<T> executor) {
        executorsInstances.put(command, executor);
    }

    public static void removeExecutor(final Class<? extends Command> class1) {
        executorsInstances.remove(class1);

    }

    public void execute(final Command command, final Channel channel) {

        System.out.println("Execute with instances:" + executorsInstances + ", classes: " + executors);

        final Class<? extends Command> commandClass = command.getClass();

        Executor<Command> executor = null;
        if (executorsInstances.containsKey(commandClass)) {
            executor = executorsInstances.get(commandClass);
        } else {

            final Class<?> executorClass = executors.get(commandClass);
            if (null == executorClass) {
                System.err.println("No executor for: " + commandClass.getCanonicalName());
                executor = null;
                return;
            }
            try {

                executor = (Executor<Command>) executorClass.newInstance();

            } catch (final InstantiationException e) {
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {
            if (null != executor) {
                System.out.println("Call executor: " + executor.getClass());

                executor.execute(command, channel);
            }
        } catch (final Throwable e) {
            e.printStackTrace();
        }

    }
}
