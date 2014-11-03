package org.landa.rempi.server.web.modules.auth;

import io.pallas.core.annotations.Configured;
import io.pallas.core.module.Module;

import javax.inject.Inject;

/**
 * @author lzsolt
 */
@io.pallas.core.annotations.Module
public class AuthModule extends Module {

    /** Is basic type. */
    @Inject
    @Configured(defaultValue = "true")
    private String basic;

    public String getBasic() {
        return basic;
    }

    public void setBasic(final String basic) {
        this.basic = basic;
    }

}
