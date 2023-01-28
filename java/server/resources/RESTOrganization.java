package server.resources;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.AuthenticationDao;
import dao.OrganizationDao;
import dao.SessionTokenDao;
import server.model.Organization;
import server.model.User;

/** This class is responsible for managing organizations. All its methods need a token
to validate the action is performed by an administrator. */
@Path("/organization")
public class RESTOrganization {
	private OrganizationDao orgDao = new OrganizationDao();
	private SessionTokenDao sessTokenDao = new SessionTokenDao();
	private AuthenticationDao authDao = new AuthenticationDao();

	/** Returns details of all organizations.
	* @param token to ensure the action is performed by an administrator. */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response allOrganizations(@PathParam("id") int id, @QueryParam("token") String token) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();			
		}
		Organization organization = orgDao.getById(id);
		return Response.status(Response.Status.OK).entity(organization).build();
	}
	
	@GET
	@Path("/filtered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response filter(@QueryParam("token") String token, String keyword) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();			
		}
		List<Organization> organizations = orgDao.getFiltered(keyword);
		return Response.status(Response.Status.OK).entity(organizations).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserOrganization(@QueryParam("token") String token) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();			
		}
		User user = authDao.getByLogin(sessTokenDao.getByToken(token).getUsername());
		Organization organizationInfo = orgDao.getById(user.getOrganizationId());
		return Response.status(Response.Status.OK).entity(organizationInfo).build();
	}
	
	/** Changes details of an organization accordingly to the provided body in JSON.
	* @param token to ensure the action is performed by an administrator. */
	@PUT
	@Path("/edit/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editPost(@PathParam("id") int id, @QueryParam("token") String token, String jsonBody) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();			
		}
		Organization org = orgDao.getById(id);
		if(org == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		Organization newData = null;
		try {
			newData = new ObjectMapper().readValue(jsonBody, Organization.class);
		} catch (IOException e) {
			System.out.println("Error with Object Mapper!");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();			
		}
		orgDao.update(id, newData.getName(), newData.getEmail(), newData.getWebsite());
		return Response.status(Response.Status.OK).entity(org).build();
	}
	
    /** Deletes an organization.
	* @param token to ensure the action is performed by an administrator. */
	@DELETE
	@Path("/delete/{id}")
	public Response delete(@PathParam("id") int id, @QueryParam("token") String token) {
		System.out.println("Called delete on org with id: " + id);
		if(token == null || sessTokenDao.getByToken(token) == null) {
			System.out.println("UNAUTHORIZED 1");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			System.out.println("UNAUTHORIZED 2");
			return Response.status(Response.Status.UNAUTHORIZED).build();			
		}
		if(orgDao.getById(id) == null) {
			System.out.println("NOT_FOUND");
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		System.out.println("OrgDao.delete called");
		orgDao.deleteById(id);
		return Response.status(Response.Status.OK).build();
	}
	
	/** Creates an organization.
	* @param token to ensure the action is performed by an administrator. */
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrganization(@QueryParam("token") String token, String jsonBody) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();			
		}
		Organization org = null;
		try {
			org = new ObjectMapper().readValue(jsonBody, Organization.class);
		} catch (IOException e) {
			System.out.println("Error with Object Mapper!");
			org = new Organization();
		}
		orgDao.create(org);
		return Response.status(Response.Status.OK).build();
	}
}
