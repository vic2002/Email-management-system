package server.resources;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AuthenticationDao;
import dao.IsForDao;
import dao.MessageDao;
import dao.SessionTokenDao;
import server.model.FilterModel;
import server.model.OrganizationIds;
import server.model.User;

@Path("/isfor")
public class RESTIsFor {
	private AuthenticationDao authDao = new AuthenticationDao();
	private SessionTokenDao sessTokenDao = new SessionTokenDao();
	private MessageDao messageDao = new MessageDao();
	private IsForDao isForDao = new IsForDao();
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response emailIsFor(@QueryParam("token") String token, String jsonBody) {
		System.out.println("emailIsFor: " + jsonBody);
		if(token == null || token.isBlank() || sessTokenDao.getByToken(token) == null) { // Unnathorized
	        return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		User user = authDao.getByLogin(sessTokenDao.getByToken(token).getUsername());
		if(user == null || !user.isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		OrganizationIds orgs;
		try {
			orgs = new ObjectMapper().readValue(jsonBody, OrganizationIds.class);
		} catch (IOException e) {
			System.out.println("Error with Object Mapper!");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		isForDao.addOrganizationsToSpecificEmail(orgs);
		return Response.status(Response.Status.OK).build();
	}	
}