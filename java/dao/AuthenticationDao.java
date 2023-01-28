package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import db.DBCall;
import server.model.SessionToken;
import server.model.User;

public class AuthenticationDao {
	private DBCall dbCall;
	private static SessionTokenDao SesTokenDao = new SessionTokenDao();
	
	public AuthenticationDao() {
		dbCall = new DBCall();
	}
	
	/** Performs log in. 
	 * @param login can be username or password
	 * @param hashedPassword is already hashed password. Plaintext password does not work. */
	public SessionToken login(String login, byte[] hashedPassword) {
		User user = getByLogin(login);
		ResultSet users = dbCall.login(user.getUsername(), user.getEmail(), hashedPassword);

		try {			
			if (users != null && users.next()) {
				if(SesTokenDao.getByUsername(user.getUsername()) != null) {
					return SesTokenDao.updateToken(user.getUsername());
				}
				String username = users.getString("username");
				SesTokenDao.createForUser(username);;
	    		return SesTokenDao.getByUsername(username);
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return null;
	}

	/** Returns a user object which has provided <b>login</b>. */
	public User getByLogin(String login) {
		Base64.Decoder decoder = Base64.getDecoder();
		ResultSet result = dbCall.getUserByLogin(login);
		User user = null;
		try {
			if(result != null && result.next()) {
				user = new User();
				user.setEmail(result.getString("email"));
				user.setUsername(result.getString("username"));
				user.setHashedPassword(decoder.decode(result.getBytes("password")));
				user.setSalt(decoder.decode(result.getBytes("salt")));
				user.setOrganizationId(result.getInt("org_id"));
				user.setAdmin(result.getBoolean("is_admin"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	/** In case if user wants to reset password, this method is used. 
	 * @param token defines a session token on which the reset is being performed.
	 * @param hashedPassword is a new password entered by a user.
	 * @param salt is already generated salt by some higher authority. */
	public void updateCredentials(String token, byte[] hashedPassword, byte[] salt) {
		User user = dbCall.getUserByToken(token);
		dbCall.resetPassword(user.getUsername(), hashedPassword, salt);
	}

	/** Returns a list representation of users existing in the database. */
	public List<User> getAll() {
		ResultSet result = dbCall.getAllUsers();
		List<User> users = new ArrayList<User>();
		try {
			while(result != null && result.next()) {
				User user = new User();
				user.setEmail(result.getString("email"));
				user.setUsername(result.getString("username"));
				user.setHashedPassword(result.getBytes("password"));
				user.setSalt(result.getBytes("salt"));
				user.setAdmin(result.getBoolean("is_admin"));
				user.setOrganizationId(result.getInt("org_id"));
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	/** Returns a list representation of users existing in the database matching a certain <b>filter</b>. */
	public List<User> getFiltered(String filter) {
		ResultSet result = dbCall.getFilteredUsers(filter);
		List<User> users = new ArrayList<User>();
		try {
			while(result != null && result.next()) {
				User user = new User();
				user.setEmail(result.getString("email"));
				user.setUsername(result.getString("username"));
				user.setHashedPassword(result.getBytes("password"));
				user.setSalt(result.getBytes("salt"));
				user.setAdmin(result.getBoolean("is_admin"));
				user.setOrganizationId(result.getInt("org_id"));
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	/** Creates a user. 
	 * @param already constructed User object. */
	public void create(User user) {
		dbCall.createUser(user.getEmail(), user.getUsername(), user.getHashedPassword(), user.getSalt());
	}
	
	/** Deletes a user.
	 * @param already constructed User object. */
	public void delete(User user) {
		dbCall.deleteUser(user.getEmail(), user.getUsername());
	}

	/** Returns a salt added to the password for a user in byte representation. */
	public byte[] getSaltFor(String login) {
		ResultSet result = dbCall.getSalt(login);
		try {
			return result.getBytes("salt");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}