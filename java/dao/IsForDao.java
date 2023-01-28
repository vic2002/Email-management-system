package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBCall;
import server.model.Organization;
import server.model.OrganizationIds;
import server.model.ReadBy;

public class IsForDao {
	private DBCall dbCall = new DBCall();
	
	/** Returns how many organizations obtained the email.
	 * It does not mean they have read this email.
	 * @param id is a unique id assigned by a database to the email. */
	public int getNumberOfSent(int id) {
		ResultSet rs = dbCall.getOrganizationIds(id);
		int count = 0;
		try {
			while(rs != null && rs.next()) {
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/** Returns which organizations have read the email.
	 * @param id is a unique id assigned by a database to the email. */
	public ReadBy getRead(int id) {
		ResultSet rs = dbCall.getOrganizationInformationWhereRead(id);
		List<Organization> orgs = new ArrayList<>();
		try {
			while(rs != null && rs.next()) {
				Organization org = new Organization();
				org.setName(rs.getString("name"));
				org.setEmail(rs.getString("email"));
				org.setWebsite(rs.getString("website"));
				orgs.add(org);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ReadBy(orgs);
	}

	/** Returns how many organizations have read the email.
	 * @param id is a unique id assigned by a database to the email. */
	public int getNumberOfRead(int id) {
		ResultSet rs = dbCall.getOrganizationIdsWhereRead(id);
		int count = 0;
		try {
			while(rs != null && rs.next()) {
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/** Checks whether an email destination is provided organization. 
	 * @param id is a unique id assigned by a database to the email.
	 * @param id is a unique id assigned by a database to the organization. */
	public boolean isEmailForOrg(int emailId, int orgId) {
		ResultSet rs = dbCall.getEmailIdsForOrg(orgId);
		try {
			while(rs != null && rs.next()) {
				if(rs.getInt("email_Id") == emailId) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void addOrganizationsToSpecificEmail(OrganizationIds orgs) {
		dbCall.addOrganizationsToSpecificEmail(orgs.getEmailId(), orgs.getOrganizationIds());
	}

	public void setAsOpened(int organizationId, int id) {
		dbCall.setEmailAsOpenedForOrg(organizationId, id);
	}
}
