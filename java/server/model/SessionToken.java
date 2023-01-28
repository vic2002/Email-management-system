package server.model;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;

/** SessionToken model with appropriate getters and setters.  */
public class SessionToken {
	private String username;
	private String token;
	private Timestamp created;
	public SessionToken() {	}
	public SessionToken(String username) {
		this.username = username;
		token = GenerateToken();
		created = new Timestamp(System.currentTimeMillis());
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	/* How tokens are generated
	 * 1. Check if the user has provided valid username\password combination
	 * 2. Generate a completely meaningless token
	 * 3. Send a token AND store it somewhere in order not to check it every time
	 * 4. Call a function to check a token for a user each time any request within a session holds (i.e. search)
     */
	
	//TODO: GenerateToken should be moved to AuthenticationDao.java!
	/** This function generates random tokens for holding sessions. */
	private static String GenerateToken() {
		SecureRandom random = new SecureRandom();
		Base64.Encoder encoder = Base64.getUrlEncoder();
		byte[] bytes = new byte[24];
		random.nextBytes(bytes);
		String token = encoder.encodeToString(bytes);
		return token;
	}
}
