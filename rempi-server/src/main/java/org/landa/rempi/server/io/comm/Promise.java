package org.landa.rempi.server.io.comm;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Promise<T> {

    private boolean completed = false;
    private boolean success = false;
    private T result;

    private final Object lock = new Object();

    public T get() {
        return result;
    }

    public boolean isCompleted() {
        synchronized (lock) {
            return completed;
        }
    }

    public void fail() {
        synchronized (lock) {
            completed = true;
        }
    }

    public void waitForComplete() {
        int loppHacker = 0;
        while (!isCompleted() && ++loppHacker < 10) {
            try {
                Thread.sleep(500);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void succeed(final T res) {
        synchronized (lock) {
            this.result = res;
            this.completed = true;
            this.success = true;
        }
    }

    public boolean isSucceeded() {
        return success;
    }

}
