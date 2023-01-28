package view.model;

import java.util.List;

import server.model.Organization;

/** A model describing how the list of organizations looks like. */
public class Organizations {
	private int numberOfOrganizations;
	private List<Organization> organizations;

	public List<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
	}

	public int getNumberOfOrganizations() {
		return numberOfOrganizations;
	}

	public void setNumberOfOrganizations(int numberOfOrganizations) {
		this.numberOfOrganizations = numberOfOrganizations;
	}
	
}
