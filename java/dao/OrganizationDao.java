package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBCall;
import server.model.Organization;

public class OrganizationDao {
	private DBCall dbCall;
	
	public OrganizationDao() {
		dbCall = new DBCall();
	}	

	/** Returns an organization object by specified id.
	 * @param id is a unique id assigned by a database to the organization. */
	public Organization getById(int id) {
		ResultSet result = dbCall.getOrganizationById(id);
		Organization org = null;
		try {
			if(result != null && result.next()) {
				org = new Organization();
				org.setId(result.getInt("id"));
				org.setName(result.getString("name"));
				org.setEmail(result.getString("email"));
				org.setWebsite(result.getString("website"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return org;
	}
	
	/** Returns an organization object by specified email. */
	public Organization getByEmail(String email) {
		ResultSet result = dbCall.getOrganizationByEmail(email);
		Organization org = null;
		try {
			if(result != null && result.next()) {
				org = new Organization();
				org.setId(result.getInt("id"));
				org.setName(result.getString("name"));
				org.setEmail(result.getString("email"));
				org.setWebsite(result.getString("website"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return org;
	}

	/** Returns an list of all organizations existing in the system. */
	public List<Organization> getAll() {
		ResultSet result = dbCall.getAllOrganizations();
		List<Organization> organizations = new ArrayList<Organization>();
		try {
			while(result != null && result.next()) {
				Organization org = new Organization();
				org.setId(result.getInt("id"));
				org.setName(result.getString("name"));
				org.setEmail(result.getString("email"));
				org.setWebsite(result.getString("website"));
				organizations.add(org);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return organizations;
	}
	
	/** Returns a list of organizations matching a certain filter. */
	public List<Organization> getFiltered(String filter) {
		if(filter == null || filter.isBlank()) {
			return getAll();
		}
		ResultSet result = dbCall.getFilteredOrganizations(filter);
		List<Organization> organizations = new ArrayList<Organization>();
		try {
			if(result != null && result.next()) {
				Organization org = new Organization();
				org.setId(result.getInt("id"));
				org.setName(result.getString("name"));
				org.setEmail(result.getString("email"));
				org.setWebsite(result.getString("website"));
				organizations.add(org);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return organizations;
	}
	
	/** Creates an organization. 
	 * @param already constructed Organization object. */
	public void create(Organization org) {
		dbCall.createOrganization(org.getName(), org.getEmail(), org.getWebsite());
	}
	
	/** Creates an organization. Takes a details of the organization. */
	public void create(String name, String email, String website) {
		dbCall.createOrganization(name, email, website);
	}

	/** Deletes an organization.
	 * @param id is a unique id assigned by a database to the organization. */
	public void deleteById(int id) {
		dbCall.deleteOrganization(id);		
	}
	
	/** Updates details of the organization by replacing old information
	 * with the provided information. */
	public void update(int id, String newName, String newEmail, String newWebsite) {
		dbCall.updateOrganization(id, newName, newEmail, newWebsite);
	}
	
	public List<Organization> resultSetToOrganizations(ResultSet rs) {
		List<Organization> result = new ArrayList<Organization>();
		try {
			while(rs != null && rs.next()) {
				Organization org = new Organization();
				org.setId(rs.getInt("id"));
				org.setEmail(rs.getString("email"));
				org.setName(rs.getString("name"));
				org.setWebsite(rs.getString("website"));
				result.add(org);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		OrganizationDao OrgDao = new OrganizationDao();
		if(OrgDao.getAll().size() == 0) {
			//
			Organization kickIn = new Organization();
			kickIn.setName("Kick-In");
			kickIn.setEmail("info@kick-in.nl");
			kickIn.setWebsite("https://www.kick-in.nl/");
		}
	}
}
