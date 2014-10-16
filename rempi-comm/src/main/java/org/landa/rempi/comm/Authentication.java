package org.landa.rempi.comm;

import java.io.Serializable;

public class Authentication implements Serializable {

    private final String clientId;

    /**
     * @param clientId
     */
    public Authentication(final String clientId) {
        super();
        this.clientId = clientId;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

}
