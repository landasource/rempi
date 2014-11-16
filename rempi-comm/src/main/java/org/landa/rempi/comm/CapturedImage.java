package org.landa.rempi.comm;

public class CapturedImage implements Message {

    private final byte[] data;

    /**
     * @param data
     */
    public CapturedImage(final byte[] data) {
        super();
        this.data = data;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

}
