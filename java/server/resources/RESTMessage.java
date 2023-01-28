package server.resources;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AttachmentDao;
import dao.AuthenticationDao;
import dao.IsForDao;
import dao.MessageDao;
import dao.SessionTokenDao;
import server.model.Attachments;
import server.model.MessageServer;
import server.model.User;
import view.model.Message;


/** This class is responsible for displaying everything for the message,
 * including all attachments. */
@Path("/message")
public class RESTMessage {
	private AuthenticationDao authDao = new AuthenticationDao();
	private SessionTokenDao sessTokenDao = new SessionTokenDao();
	private MessageDao messageDao = new MessageDao();
	private AttachmentDao attDao = new AttachmentDao();
	private IsForDao isForDao = new IsForDao();
	
	/** Displays an email. In case if an email for another user is requested,
	 * returns UNAUTHORIZED. */
	@GET
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessage(@PathParam("id") int id, @QueryParam("token") String token) throws SQLException {
		if(token == null || token.isBlank() || sessTokenDao.getByToken(token) == null) { // Unnathorized
	        return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User user = authDao.getByLogin(sessTokenDao.getByToken(token).getUsername());
		if(user == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(user.isAdmin()){
			Message message1 = messageDao.getMessageById(id);
        	if (message1 != null) {
        		isForDao.setAsOpened(user.getOrganizationId(), id);
            	Attachments attachments = new Attachments();
            	attachments.setAttachments(attDao.getAttachmentsFor(id));
            	attachments.setNumberOfAttachments(attachments.getAttachments().size());
            	message1.setAttachments(attachments);
            	return Response.status(Response.Status.OK).entity(message1).build();
        	}
		}
		if(!isForDao.isEmailForOrg(id, user.getOrganizationId())) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
        Message message = messageDao.getMessageById(id);
        if (message != null) {
        	isForDao.setAsOpened(user.getOrganizationId(), id);
            Attachments attachments = new Attachments();
            attachments.setAttachments(attDao.getAttachmentsFor(id));
            attachments.setNumberOfAttachments(attachments.getAttachments().size());
            message.setAttachments(attachments);
            return Response.status(Response.Status.OK).entity(message).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@QueryParam("token") String token, String jsonBody) {
		System.out.println("Body: " + jsonBody);
		if(token == null || token.isBlank() || sessTokenDao.getByToken(token) == null) { // Unnathorized
	        return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User user = authDao.getByLogin(sessTokenDao.getByToken(token).getUsername());
		if(user == null || !user.isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		MessageServer ms;
		try {
			ms = new ObjectMapper().readValue(jsonBody, MessageServer.class);
			ms.setSender(user.getUsername());
			ms.setSendAt(new Timestamp(System.currentTimeMillis()));
			ms.setSenderEmail(user.getEmail());
			ms.setOrgId(user.getOrganizationId());
		} catch (IOException e) {
			System.out.println("Error with Object Mapper!");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		int emailId = messageDao.createMessage(ms);
		return Response.status(Response.Status.OK).entity(emailId).build();
	}
	
	@DELETE
	@Path("/delete/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id, @QueryParam("token") String token, String jsonBody) {
		if(token == null || token.isBlank() || sessTokenDao.getByToken(token) == null) { // Unnathorized
	        return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User user = authDao.getByLogin(sessTokenDao.getByToken(token).getUsername());
		if(user == null || !user.isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		Message msg = messageDao.getMessageById(id);
		messageDao.deleteMessage(id);
		return Response.status(Response.Status.OK).entity(msg).build();
	}	
}