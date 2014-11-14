package org.landa.rempi.client;

import java.util.UUID;

/**
 * Main entry point of application.
 * 
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Main {

    public static void main(final String[] args) throws Exception {
        // Print usage if no argument is specified.
        if (args.length < 2) {
            System.err.println("Usage: " + RempiClient.class.getSimpleName() + " <host> <port> [<client id>]");
            return;
        }

        // Parse options.
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);

        final String id = args.length >= 3 ? args[2] : UUID.randomUUID().toString();

        System.out.println("Start client");
        new RempiClient(host, port, id).run();
    }

}
