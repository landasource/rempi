package org.landa.rempi.server.web.modules.auth.service;

import io.pallas.core.annotations.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.Factory;

@Startup
@ApplicationScoped
public class SecurityManagerFactory {

    @PostConstruct
    private void init() {

        //1.
        final Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        //2.
        final org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        //3.
        SecurityUtils.setSecurityManager(securityManager);

    }

}
