package server.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dao.OrganizationDao;
import view.model.Organizations;

/** This class is responsible for displaying a list of organizations on the administrator page. */
@Path("/organizations")
public class RESTOrganizations {
	private OrganizationDao OrgDao = new OrganizationDao();
	
	/** By default, all organizations are displayed, e.g. when the search field is empty. */
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response allOrganizations(@QueryParam("token") String token) {
		Organizations organizations = new Organizations();
		organizations.setOrganizations(OrgDao.getAll());
		organizations.setNumberOfOrganizations(organizations.getOrganizations().size());
		return Response.status(Response.Status.OK).entity(organizations).build();
	}
	
	/** In case something is typed in the search field, this method is called. 
	 * It shows only that organizations which match the search request. */
	@GET
	@Path("/filtered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response filteredOrganizations(@QueryParam("token") String token, @QueryParam("filter") String filter) {
		Organizations organizations = new Organizations();
		if(filter.isBlank()) {
			organizations.setOrganizations(OrgDao.getAll());			
		} else {
			organizations.setOrganizations(OrgDao.getFiltered(filter));
		}
		organizations.setNumberOfOrganizations(organizations.getOrganizations().size());
		return Response.status(Response.Status.OK).entity(organizations).build();
	}
}
