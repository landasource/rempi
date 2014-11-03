package org.landa.rempi.server.web.core;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */

@Stateless
public class TransactionalExecutor implements Executor {

    @Override
    @Asynchronous
    public void execute(final Runnable command) {

        Executors.newSingleThreadExecutor().execute(command);
    }

}
