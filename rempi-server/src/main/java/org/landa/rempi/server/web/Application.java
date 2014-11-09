package org.landa.rempi.server.web;

import io.pallas.core.WebApplication;
import io.pallas.core.controller.ControllerClass;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.landa.rempi.server.web.controller.client.ClientController;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@Specializes
@ApplicationScoped
public class Application extends WebApplication {

    @Inject
    private Logger logger;

    @PostConstruct
    private void init() {

        logger.info("Application started at: ");
    }

    @Override
    public String getName() {

        return "rempi-server";
    }

    @Override
    public ControllerClass getDefaultControllerClass() {
        return new ControllerClass(ClientController.class);
    }

}
