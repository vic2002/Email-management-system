package server.resources;

import java.io.IOException;

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
import dao.EventDao;
import dao.SessionTokenDao;
import server.model.Event;

@Path("/event")
public class RESTEvent {
	private EventDao eventDao = new EventDao();
	private SessionTokenDao sessTokenDao = new SessionTokenDao();
	private AuthenticationDao authDao = new AuthenticationDao();
	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getByName(@QueryParam("token") String token, @PathParam("name") String name) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		return Response.status(Response.Status.OK).build();
	}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@QueryParam("token") String token, String jsonBody) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();	
		}
		Event event = null;
    	try {
    		event = new ObjectMapper().readValue(jsonBody, Event.class);
		} catch (IOException e) {
			System.out.println("(RESTEvent.create) Error with Object Mapper!");
		}
    	event.setCreatedBy(sessTokenDao.getByToken(token).getUsername());
    	eventDao.createEvent(event);
    	return Response.status(Response.Status.OK).build();
	}

	@PUT
	@Path("/edit/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("id") int id, @QueryParam("token") String token, String jsonBody) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();			
		}
		Event event = eventDao.getById(id);
		if(event == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		Event newData = null;
		try {
			newData = new ObjectMapper().readValue(jsonBody, Event.class);
		} catch (IOException e) {
			System.out.println("Error with Object Mapper!");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		eventDao.update(id, newData.getName());
		return Response.status(Response.Status.OK).entity(event).build();
	}
	
	@DELETE
	@Path("/delete")
	public Response delete(@QueryParam("token") String token, @QueryParam("id") int id) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();	
		}
    	eventDao.deleteEvent(id);
    	return Response.status(Response.Status.OK).build();
	}
}
