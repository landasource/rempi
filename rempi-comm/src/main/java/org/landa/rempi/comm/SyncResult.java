package org.landa.rempi.comm;

import java.io.Serializable;

public abstract class SyncResult implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4356996490129356643L;
    private final String id;

    public SyncResult(final SyncCommand command) {
        this.id = command.getId();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    public abstract Object getResult();
}
