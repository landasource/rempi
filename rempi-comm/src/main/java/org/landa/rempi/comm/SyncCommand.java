package org.landa.rempi.comm;

import java.util.UUID;

/**
 * For synchronous commands.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public abstract class SyncCommand implements Command {

    private final String id;

    public SyncCommand() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

}
