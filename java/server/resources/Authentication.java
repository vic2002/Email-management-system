package server.resources;

import extras.Encryption;
import server.model.Organization;
import server.model.SessionToken;
import server.model.User;
import view.model.LoginModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dao.AuthenticationDao;
import dao.OrganizationDao;
import dao.SessionTokenDao;

@Path("/authentication")
public class Authentication {
	private static AuthenticationDao authDao = new AuthenticationDao();
	private static SessionTokenDao sessTokenDao = new SessionTokenDao();
	private static OrganizationDao orgDao = new OrganizationDao();
	
	/** Performs login by provided username and password.
	 * It does not matter either a username or email is provided; the fuction accepts both. 
	 * @returns NOT_FOUND in case the user does not exist (usually wrong credentials) 
	 * @returns UNAUTHORIZED in case there was a trouble while generating a token. */
	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("login") String login, @QueryParam("password") String password) {
		User user = authDao.getByLogin(login);
		if(user == null) { // User not found (not registered)
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		byte[] salt = user.getSalt();
		byte[] hashedPassword = Encryption.HashPassword(password, salt); // hash password with salt
		SessionToken token = authDao.login(login, hashedPassword);
		if(token == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		Organization org = orgDao.getById(user.getOrganizationId());
		LoginModel lm = new LoginModel(token.getToken(), user.getUsername(), org.getName(), String.valueOf(user.isAdmin()));
		return Response.status(Response.Status.OK).entity(lm).type(MediaType.APPLICATION_JSON).build();
	}
	
	/** Performs registration of a new user. */
	@GET
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(@QueryParam("email") String email, @QueryParam("username") String username, @QueryParam("password") String password) {
		if(authDao.getByLogin(email) != null) {
			return Response.status(Response.Status.CONFLICT).build();
		}
		
		byte[] salt = Encryption.generateSalt();
		byte[] hashedPassword = Encryption.HashPassword(password, salt); // hash password with salt
		User user = new User(email, username, hashedPassword, salt);
		authDao.create(user);
		SessionToken token = authDao.login(email, hashedPassword);
		return Response.status(Response.Status.OK).entity(token).type(MediaType.APPLICATION_JSON).build();
	}
	
	/** Performs logout by deleting provided session from the database using a token. */
	@GET
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@QueryParam("token") String token) {
		if(sessTokenDao.getByToken(token) != null) {
			sessTokenDao.deleteByToken(token);
		}
		return Response.status(Response.Status.OK).build();
	}

	@GET
	@Path("/reset")
	public Response reset(@QueryParam("token") String token, @QueryParam("password") String password) {
		if(sessTokenDao.getByToken(token) != null) {
			byte[] salt = Encryption.generateSalt();
			byte[] hashedPassword = Encryption.HashPassword(password, salt); // hash password with salt
			authDao.updateCredentials(token, hashedPassword, salt);
		}
		return Response.status(Response.Status.OK).build();
	}
}