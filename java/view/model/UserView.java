package view.model;

public class UserView {
	private String username;
	private String email;
	private String organization;
	private String role;
	public UserView() {
		this(null, null, null, null);
	}
	public UserView(String email, String username, String organization, String role) {
		this.email = email;
		this.username = username;
		this.organization = organization;
		this.role = role;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}	
}
