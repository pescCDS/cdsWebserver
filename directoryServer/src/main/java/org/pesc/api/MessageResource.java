package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.Message;
import org.pesc.api.model.Message;
import org.pesc.service.MessageService;
import org.pesc.service.PagedData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/27/16.
 */
@Component
@WebService
@Path("/messages")
@Api("/messages")
public class MessageResource {

    private static final Log log = LogFactory.getLog(MessageResource.class);

    @Autowired
    private MessageService messageService;

    @Context
    private HttpServletResponse servletResponse;

    @GET
    @ApiOperation("Return messages that belong to the given id.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Message> getMessages(@QueryParam("organization_id") @ApiParam(value="The organization id.", required=true) Integer orgID,
                                                    @QueryParam("topic") @ApiParam(value="The topic.", required=false) String topic,
                                                    @QueryParam("content") @ApiParam(value="Content search terms.", required=false) String content,
                                                    @QueryParam("action_required") @ApiParam(value="Action required true/false.", required=false) Boolean actionRequired,
                                                    @QueryParam("created_time") @ApiParam(value="The create time.", required=false) Long createdTime,
                                                    @QueryParam("limit") @DefaultValue("5") Integer limit,
                                                    @QueryParam("offset") @DefaultValue("0") Integer offset


    ) {

        if (limit == null || offset == null) {
            limit = 5;
            offset = 0;
        }

        checkParameter(orgID, "organization_id");

        PagedData<Message> pagedData = new PagedData<Message>(limit,offset);

        messageService.search(orgID,topic,content,createdTime,actionRequired, pagedData);

        servletResponse.addHeader("X-Total-Count", String.valueOf(pagedData.getTotal()) );
        return pagedData.getData();
    }

    @Path("/{id}")
    @PUT
    @ApiOperation("Update a message property.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void dismissMessage(@PathParam("id")Integer id, @QueryParam("dismiss") @ApiParam(value="Dismiss property.", required=true) Boolean dismiss) {

        checkParameter(dismiss, "dismiss");

        messageService.setDismissed(id, dismiss);
    }


    private void checkParameter(Object param, String parameterName) {
        if (param == null) {
            throw new WebApplicationException(String.format("The %s parameter is required.", parameterName));
        }
    }

}

