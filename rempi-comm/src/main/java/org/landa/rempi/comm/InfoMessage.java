package org.landa.rempi.comm;

/**
 * Has no command meaning. Just for statistics or similars.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class InfoMessage implements Message {

    private String text;

    /**
     * @param text
     */
    public InfoMessage(final String text) {
        super();
        this.text = text;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text
     *            the text to set
     */
    public void setText(final String text) {
        this.text = text;
    }

}
