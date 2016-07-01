package com.landasource.rempi.server.common;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class RestExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(final Throwable exception) {
        return Response.serverError().entity(exception.getMessage()).build();
    }

}
