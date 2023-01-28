package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBCall;
import server.model.SessionToken;

public class SessionTokenDao {
	private DBCall dbCall;
	
	public SessionTokenDao() {
		dbCall = new DBCall();
	}	

	/** Returns a token generated for a current logged in user.
	 * Returns <b>null</b> in case a user is not logged in. */
	public SessionToken getByUsername(String username) {
		ResultSet rs = dbCall.getSessionToken(username);
		SessionToken st = null;
		try {
			if(rs != null && rs.next()) {
				st = new SessionToken();
				st.setToken(rs.getString("token"));
				st.setCreated(rs.getTimestamp("timestamp"));
				st.setUsername(rs.getString("username"));
			}
		} catch (SQLException e) {
			System.out.println("(SessionToken.get()) Connection error: " + e);
		}
		return st;
	}
	
	/** Returns a user for whom a provided token. */
	public SessionToken getByToken(String token) {
		ResultSet rs = dbCall.getSessionTokenByToken(token);
		SessionToken st = null;
		try {
			if(rs != null && rs.next()) {
				st = new SessionToken();
				st.setToken(rs.getString("token"));
				st.setCreated(rs.getTimestamp("timestamp"));
				st.setUsername(rs.getString("username"));
			}
		} catch (SQLException e) {
			System.out.println("(SessionToken.get()) Connection error: " + e);
		}
		return st;
	}

	/** Returns a list of all tokens in the system. */
	public List<SessionToken> getAll() {
		ResultSet rs = dbCall.getSessionTokens();
		List<SessionToken> result = new ArrayList<SessionToken>();
		try {
			if(rs != null && rs.next()) {
				SessionToken st = new SessionToken();
				st.setToken(rs.getString("token"));
				st.setCreated(rs.getTimestamp("timestamp"));
				st.setUsername(rs.getString("username"));
				result.add(st);
			}
		} catch (SQLException e) {
			System.out.println("(SessionToken.get()) Connection error: " + e);
		}
		return result;
	}
	
	/** When a user logs in, a system should remember his details to perform further
	 * actions and do not ask to log in each time. This method creates a token for the provided
	 * username and remembers it for some time. */
	public void createForUser(String username) {
		SessionToken st = new SessionToken(username);
		dbCall.createSessionToken(username, st.getToken(), st.getCreated());
	}
	
	/** Deletes a session using a generated token. */
	public void deleteByToken(String token) {
		if(dbCall.getSessionTokenByToken(token) != null) {
			dbCall.deleteToken(token);
		}
	}

	/** Used in case of long sessions. If a token expires, a new one is generated 
	 * to avoid accidental logout. */
	public SessionToken updateToken(String username) {
		SessionToken st = new SessionToken(username);
		dbCall.updateSessionToken(st.getUsername(), st.getToken(), st.getCreated());
		return st;
	}
}
