package server.model;

/** User model with appropriate getters and setters. 
 * <b>Do not store</b> plaintext passwords here! */
public class User {
	private String username;
	private String email;
	private byte[] hashedPassword;
	private byte[] salt;
	private int organizationId;
	private boolean isAdmin;
	public User() {
		this(null, null, null, null);
	}
	public User(String email, String username, byte[] password, byte[] salt) {
		this.email = email;
		this.username = username;
		this.hashedPassword = password;
		this.salt = salt;
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
	public byte[] getHashedPassword() {
		return hashedPassword;
	}
	public void setHashedPassword(byte[] hashedPassword) {
		this.hashedPassword = hashedPassword;
	}
	public byte[] getSalt() {
		return salt;
	}
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
	public int getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
}
