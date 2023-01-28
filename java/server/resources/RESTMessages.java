package server.resources;

import server.model.FilterModel;
import server.model.User;
import view.model.AdminViewMessage;
import view.model.AdminViewMessages;
import view.model.SearchMessages;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.AuthenticationDao;
import dao.MessageDao;
import dao.SessionTokenDao;

import java.io.IOException;
import java.sql.SQLException;

@Path("/messages")
public class RESTMessages {
	private AuthenticationDao authDao = new AuthenticationDao();
	private SessionTokenDao sessTokenDao = new SessionTokenDao();
	private MessageDao messageDao = new MessageDao();
	
	
	/** By default, all messages on a search page are displayed.
	 * This method shows all messages. */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSearchFiltered(@QueryParam("token") String token, String jsonBody) throws SQLException {
    	if(token == null || sessTokenDao.getByToken(token) == null) {
    		return Response.status(Response.Status.UNAUTHORIZED).build();
    	}
		User user = authDao.getByLogin(sessTokenDao.getByToken(token).getUsername());
		if(user == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
    	FilterModel filter = null;
    	try {
			filter = new ObjectMapper().readValue(jsonBody, FilterModel.class);
		} catch (IOException e) {
			System.out.println("(getSearchFilter) Error with Object Mapper!");
			filter = new FilterModel("", null, null, "", false, "");
		}
    	SearchMessages result = null;
    	if(user.isAdmin()) {
            result = messageDao.filterMessages(filter);
    	} else {
            result = messageDao.filterMessages(user.getOrganizationId(), filter);
    	}
        return Response.status(Response.Status.OK).entity(result).build();
    }
    
    /** In case something is typed in the search field this method is called to return only
     * messages matching the request. */
    @POST
    @Path("/filtered")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFiltered(@QueryParam("token") String token, String jsonBody) throws SQLException {
    	System.out.println("getFilterd with body: " + jsonBody);
    	if(token == null || sessTokenDao.getByToken(token) == null){
    		return Response.status(Response.Status.UNAUTHORIZED).build();
    	}
		User user = authDao.getByLogin(sessTokenDao.getByToken(token).getUsername());
		if(user == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
    	FilterModel filter = null;
    	try {
			filter = new ObjectMapper().readValue(jsonBody, FilterModel.class);
		} catch (IOException e) {
			System.out.println("(RESTMessages.getFiltered) Error with Object Mapper!");
			filter = new FilterModel("", null, null, "", false, "");
		}
    	AdminViewMessages messages = null;
    	if(user.isAdmin()) {
        	messages = messageDao.filterMessagesAdmin(filter);
    	} else {
        	messages = messageDao.filterMessagesAdmin(user.getOrganizationId(), filter);
    	}
    	System.out.println("Sending messages");
        return Response.status(Response.Status.OK).entity(messages).build();
    }
}
