package org.landa.rempi.comm.livestream;

import org.landa.rempi.comm.Command;

public class StartStreamCommand implements Command {

    private final int port;

    public StartStreamCommand(final int port) {
        super();
        this.port = port;

    }

    public int getPort() {
        return port;
    }

}
