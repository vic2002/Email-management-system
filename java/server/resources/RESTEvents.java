package server.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dao.EventDao;
import dao.SessionTokenDao;
import view.model.Events;

@Path("/events")
public class RESTEvents {
	private EventDao eventDao = new EventDao();
	private SessionTokenDao sessTokenDao = new SessionTokenDao();
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllEvents(@QueryParam("token") String token) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		Events events = new Events();
		events.setEvents(eventDao.getAll());
		events.setNumberOfEvents(events.getEvents().size());
		return Response.status(Response.Status.OK).entity(events).build();
	} 
	
	@GET
	@Path("/filtered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response filteredEvents(@QueryParam("token") String token, @QueryParam("filter") String filter) {
		if(token == null || sessTokenDao.getByToken(token) == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		Events events = new Events();
		if(filter == null || filter.isBlank()) {
			events.setEvents(eventDao.getAll());
		} else {
			events.setEvents(eventDao.getFiltered(filter));
		}
		events.setNumberOfEvents(events.getEvents().size());
		return Response.status(Response.Status.OK).entity(events).build();
	} 
}
