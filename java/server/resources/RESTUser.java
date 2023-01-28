package server.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dao.AuthenticationDao;
import dao.SessionTokenDao;
import server.model.User;

@Path("/user")
public class RESTUser {
	private SessionTokenDao sessTokenDao = new SessionTokenDao();
	private AuthenticationDao authDao = new AuthenticationDao();
	@DELETE
	@Path("/delete/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("username") String username, @QueryParam("token") String token) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();			
		}
		if(authDao.getByLogin(username) == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		User user = authDao.getByLogin(username);
		authDao.delete(user);
		return Response.status(Response.Status.OK).entity(user).build();
	}
}
