package server.resources;

import java.util.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dao.AuthenticationDao;
import dao.OrganizationDao;
import dao.SessionTokenDao;
import server.model.User;
import view.model.UserView;
import view.model.UsersView;

/** This class is responsible for displaying a list of users on the administrator page. */
@Path("/users")
public class RESTUsers {
	private AuthenticationDao authDao = new AuthenticationDao();
	private SessionTokenDao sessTokenDao = new SessionTokenDao();
	private OrganizationDao orgDao = new OrganizationDao();
	
	/** By default, all users are displayed, e.g. when the search field is empty. 
	 * It needs a session token to ensure the request is done by an administrator, not by a user. */
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers(@QueryParam("token") String token) {
		if(token == null || token.isBlank()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		UsersView users = new UsersView();
		List<UserView> list = new ArrayList<UserView>();
		for(User user : authDao.getAll()) {
			String role = user.isAdmin() ? "Admin" : "User";
			UserView uv = new UserView(user.getEmail(), user.getUsername(), orgDao.getById(user.getOrganizationId()).getName(), role);
			list.add(uv);
		}
		users.setUsers(list);
		users.setNumberOfUsers(users.getUsers().size());
		return Response.status(Response.Status.OK).entity(users).build();
	} 
	
	/** In case something is typed in the search field, this method is called. 
	 * It shows only that users which match the search request.
	 * It needs a session token to ensure the request is done by an administrator, not by a user.*/
	@GET
	@Path("/filtered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response filteredUsers(@QueryParam("filter") String filter, @QueryParam("token") String token) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		UsersView users = new UsersView();
		List<UserView> list = new ArrayList<UserView>();
		List<User> originalUsers = new ArrayList<User>();
		if(filter.isBlank()) {
			originalUsers.addAll(authDao.getAll());
		} else {
			originalUsers.addAll(authDao.getFiltered(filter));
		}
		for(User user : originalUsers) {
			String role = user.isAdmin() ? "Admin" : "User";
			String orgName = null;
			if(orgDao.getById(user.getOrganizationId()) != null) {
				orgName = orgDao.getById(user.getOrganizationId()).getName();
			}
			UserView uv = new UserView(user.getEmail(), user.getUsername(), orgName, role);
			list.add(uv);
		}
		users.setUsers(list);
		users.setNumberOfUsers(users.getUsers().size());
		
		users.setNumberOfUsers(users.getUsers().size());
		return Response.status(Response.Status.OK).entity(users).build();
	} 
}
