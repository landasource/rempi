package com.landasource.rempi.server.rs.user;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.landasource.rempi.server.ejb.UserBean;

/**
 * Created by Zsolti on 2016.07.01..
 */
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserBean userBean;

    @GET
    @Path("/")
    public List<UserInfo> findList() {
        return userBean.findUsers();
    }

    @POST
    @Path("/")
    public UserInfo createUser(final UserInfo data) {
        return userBean.createUser(data);
    }

}
