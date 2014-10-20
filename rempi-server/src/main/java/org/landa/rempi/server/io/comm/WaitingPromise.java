package org.landa.rempi.server.io.comm;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WaitingPromise {

    private final String expectedId;

    private final Promise promise;

    /**
     * @param expectedId
     * @param promise
     */
    public WaitingPromise(final String expectedId, final Promise promise) {
        super();
        this.expectedId = expectedId;
        this.promise = promise;
    }

    /**
     * @return the expectedId
     */
    public String getExpectedId() {
        return expectedId;
    }

    /**
     * @return the promise
     */
    public Promise getPromise() {
        return promise;
    }

}
