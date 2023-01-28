package view.model;

public class LoginModel {
	private String token;
	private String orgName;
	private String username;
	private String isAdmin;
	public LoginModel(String token, String username, String orgName, String isAdmin) {
		this.token = token;
		this.username = username;
		this.orgName = orgName;
		this.isAdmin = isAdmin;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}
}
