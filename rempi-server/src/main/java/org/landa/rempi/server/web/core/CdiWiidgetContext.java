package org.landa.rempi.server.web.core;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Specializes;

import org.apache.shiro.SecurityUtils;

@Specializes
public class CdiWiidgetContext extends io.pallas.core.view.engines.wiidget.integration.CdiWiidgetContext {

    @PostConstruct
    private void init() {

        set("user", SecurityUtils.getSubject());
    }

}
