package org.pesc.api.exception;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 6/14/16.
 */

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<ApiException> {

    @Override
    public Response toResponse(ApiException e) {
        return Response.status(e.getStatus()).entity(e.getApiError()).build();
    }
}