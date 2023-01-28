package server.model;

/** Organization model with appropriate getters and setters. */
public class Organization {
	private int id;
	private String name;
	private String email;
	private String website;
	public Organization() {
		this(null, null, null);
	}
	public Organization(String name, String email, String website) {
		this.name = name;
		this.email = email;
		this.website = website;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	@Override
	public String toString() {
		return "Organization name: " + name + "; email: " + email + "; website: " + website;
	}
}
