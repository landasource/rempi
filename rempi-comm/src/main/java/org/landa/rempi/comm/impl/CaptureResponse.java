package org.landa.rempi.comm.impl;

import org.landa.rempi.comm.SyncCommand;
import org.landa.rempi.comm.SyncResult;

public class CaptureResponse extends SyncResult {

    private final byte[] bytes;

    public CaptureResponse(final SyncCommand command, final byte[] bytes) {
        super(command);
        this.bytes = bytes;
    }

    @Override
    public Object getResult() {
        return bytes;
    }

}
