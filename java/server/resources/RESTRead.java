package server.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AuthenticationDao;
import dao.EventDao;
import dao.IsForDao;
import dao.SessionTokenDao;
import server.model.Event;
import server.model.ReadBy;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/read")
public class RESTRead {
    private EventDao eventDao = new EventDao();
    private IsForDao isDao = new IsForDao();
    private SessionTokenDao sessTokenDao = new SessionTokenDao();
    private AuthenticationDao authDao = new AuthenticationDao();
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByName(@QueryParam("token") String token, @PathParam("id") int id) {
        if(token == null || sessTokenDao.getByToken(token) == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(!authDao.getByLogin(sessTokenDao.getByToken(token).getUsername()).isAdmin()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        System.out.println(id);
        ReadBy rb = isDao.getRead(id);
        return Response.status(Response.Status.OK).entity(rb).build();
    }
}
