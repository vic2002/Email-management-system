package server.resources;

import java.io.IOException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.AttachmentDao;
import dao.AuthenticationDao;
import dao.SessionTokenDao;
import server.model.Attachments;
import server.model.FilterModel;
import server.model.User;

/** This class is responsible for displaying attachments. */
@Path("/files")
public class RESTFiles {
	private SessionTokenDao sessTokenDao = new SessionTokenDao();
	private AuthenticationDao authDao = new AuthenticationDao();
	private AttachmentDao attDao = new AttachmentDao();
	
	/** Displays all email attachments for the current user. 
	 *  Needs a token to know for which user the attachments should be shown.
	 *  @returns attachments. */
	@POST
	@Path("/filter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFilesByName(@QueryParam("token") String token, String jsonBody) {
		System.out.println("Files\n" + jsonBody);
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User user = authDao.getByLogin(sessTokenDao.getByToken(token).getUsername());
		FilterModel filter = null;
		try {
			filter = new ObjectMapper().readValue(jsonBody, FilterModel.class);
		} catch (IOException e) {
			System.out.println("Error with Object Mapper!");
			filter = new FilterModel("", null, null, "", false, "");
		}
		Attachments attachments = new Attachments();
		if (user.isAdmin()) {
			attachments.setAttachments(attDao.getFilteredAdmin(filter));
		} else {
			attachments.setAttachments(attDao.getFiltered(user.getOrganizationId(), filter));
		}
		attachments.setNumberOfAttachments(attachments.getAttachments().size());
		return Response.status(Response.Status.OK).entity(attachments).build();
	}
}
