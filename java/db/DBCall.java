package db;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;

import server.model.FilterModel;
import server.model.User;

/** This class is responsible for making database queries.
 * It returns <u>unprocessed raw data</u> in ResultSets. For processed data returning
 * Java objects and primitive types please refer to
 * the appropriate methods in <u>dao</u> package. */
public class DBCall {
    private String host, dbName, url, username, password;
    
    public DBCall() {
    	loadDriver();
    	//ReadConfigFile rcf = new ReadConfigFile();
        host = "bronto.ewi.utwente.nl";
        dbName = "dab_di20212b_22"; // rcf.getUsername();
        url = "jdbc:postgresql://" + host + ":5432/" + dbName + "?currentSchema=kickin";
        username = dbName; // rcf.getUsername();
        password = "59VBwBTb41wjAdOo";// rcf.getPassword();
    }

    /** Loads the driver which is responsible for handling the connection with PostgreSQL. */
	private void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading driver: " + e);
        }
    }

    private Boolean goodDate(String date) {
		if (date == null) {
			return false;
		}
		Boolean good = !date.isBlank() && Character.isDigit(date.charAt(0)) && Character.isDigit(date.charAt(1))
			&& Character.isDigit(date.charAt(2)) && Character.isDigit(date.charAt(3)) && date.charAt(4) == '-'
			&& Character.isDigit(date.charAt(5))  && Character.isDigit(date.charAt(6)) && date.charAt(7) == '-'
			&& Character.isDigit(date.charAt(8)) && Character.isDigit(date.charAt(9));
		return good;
	}
	/** A query to the database for the set of emails matching specific filter. 
	@param orgId organization which performs a search — do not show emails which do not belong to this organization
	@param filter is what is being typed in the search field */
    public ResultSet filterMessages(int orgId, FilterModel filter) {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String toDate = "";
			String fromDate = "";
			String key = "";
			Boolean eng = !filter.getIsDutch();
			StringBuilder preparedQuery;
			StringBuilder whereQuery = new StringBuilder();
			if (eng) {
				preparedQuery = new StringBuilder("SELECT *, ts_rank(e.ts_english, q) AS rank FROM to_tsquery('english', ?) AS q, kickin.email AS e");
			} else {
				preparedQuery = new StringBuilder("SELECT *, ts_rank(e.ts_dutch, q) AS rank FROM to_tsquery('dutch', ?) AS q, kickin.email AS e");
			}
            preparedQuery.append(" JOIN kickin.isfor AS \"if\" ON \"if\".email_id = e.id AND \"if\".org_id = ?");
			if (filter.getStatus().equals("READ")) {
				preparedQuery.append(" AND if.opened is true");
			} else if (filter.getStatus().equals("UNREAD")) {
				preparedQuery.append(" AND if.opened is false");
			}
			if (goodDate(filter.getToDate())) {
				whereQuery.append(" AND e.send_at <= TO_TIMESTAMP(?, 'YYYY-MM-DD')");
				toDate = filter.getToDate();
			}
			if (goodDate(filter.getFromDate())) {
				whereQuery.append(" AND e.send_at >= TO_TIMESTAMP(?, 'YYYY-MM-DD')");
				fromDate = filter.getFromDate();
			}
            if (filter != null && filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
				if (eng) {
					whereQuery.append(" AND e.ts_english @@ q");
				} else {
					whereQuery.append(" AND e.ts_dutch @@ q");
				}
            	key = filter.getKeyword().replaceAll(" ", " & ");
            }
            if(whereQuery.length() > 0) {
            	preparedQuery.append(" WHERE").append(whereQuery.substring(" AND".length()));
            }
            if (filter.getSort() == null || filter.getSort().equals("DESC")) {
            	preparedQuery.append(" ORDER BY e.send_at DESC;");
            } else if (filter.getSort().equals("ASC")) {
                preparedQuery.append(" ORDER BY e.send_at ASC;");
            } else if (filter.getSort().equals("relevant")) {
                preparedQuery.append(" Order by rank DESC;");
            }
            PreparedStatement pStatement = connection.prepareStatement(preparedQuery.toString());
            pStatement.setString(1, key);
            pStatement.setInt(2, orgId);
            int id = 3;
            if (!toDate.isEmpty()) {
                pStatement.setString(id, toDate);
                id++;
            }
            if (!fromDate.isEmpty()) {
                pStatement.setString(id, fromDate);
                id++;
            }
            ResultSet resultSet = pStatement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.close();
            return resultSet;
        } catch (SQLException sqle) {
            System.err.println("(filterMessages) Error connecting: " + sqle);
            return null;
        }
    }
    
    /** A query to the database for the set of emails matching specific filter. 
	@param filter is what is being typed in the search field */
	public ResultSet filterMessages(FilterModel filter) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String toDate = "";
			String fromDate = "";
			String key = "";
			Boolean eng = !filter.getIsDutch();
			StringBuilder preparedQuery;
			StringBuilder whereQuery = new StringBuilder();
			if (eng) {
				preparedQuery = new StringBuilder("SELECT *, ts_rank(e.ts_english, q) AS rank FROM to_tsquery('english', ?) AS q, kickin.email AS e");
			} else {
				preparedQuery = new StringBuilder("SELECT *, ts_rank(e.ts_dutch, q) AS rank FROM to_tsquery('dutch', ?) AS q, kickin.email AS e");
			}
			if (goodDate(filter.getToDate())) {
				whereQuery.append(" AND e.send_at <= TO_TIMESTAMP(?, 'YYYY-MM-DD')");
				toDate = filter.getToDate();
			}
			if (goodDate(filter.getFromDate())) {
				whereQuery.append(" AND e.send_at >= TO_TIMESTAMP(?, 'YYYY-MM-DD')");
				fromDate = filter.getFromDate();
			}
			if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
				if (eng) {
					whereQuery.append(" AND e.ts_english @@ q");
				} else {
					whereQuery.append(" AND e.ts_dutch @@ q");
				}
				key = filter.getKeyword().replaceAll(" ", " & ");
			}
			if(whereQuery.length() > 0) {
				preparedQuery.append(" WHERE").append(whereQuery.substring(" AND".length()));
			}
			if (filter.getSort() == null || filter.getSort().equals("DESC")) {
				preparedQuery.append(" ORDER BY e.send_at DESC;");
			} else if (filter.getSort().equals("ASC")) {
				preparedQuery.append(" ORDER BY e.send_at ASC;");
			} else if (filter.getSort().equals("relevant")) {
				preparedQuery.append(" Order by rank DESC;");
			}
			PreparedStatement pStatement = connection.prepareStatement(preparedQuery.toString());
			pStatement.setString(1, key);
			int id = 2;
			if (!toDate.isEmpty()) {
				pStatement.setString(id, toDate);
				id++;
			}
			if (!fromDate.isEmpty()) {
				pStatement.setString(id, fromDate);
				id++;
			}
			ResultSet resultSet = pStatement.executeQuery();
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(filterMessages) Error connecting: " + sqle);
			return null;
		}
	}

	/** Returns an email which has a particular id. */
    public ResultSet getMessageById(int id) {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
            String preparedQuery = "SELECT * FROM kickin.email WHERE id = ?;";
            PreparedStatement statement = connection.prepareStatement(preparedQuery);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.close();
            return resultSet;
        } catch (SQLException sqle) {
            System.err.println("(getMessageById) Error connecting: " + sqle);
            return null;
        }
     }

    /** Creates a database record with the email. */
	public int createMessage(String senderEmail, String sender, String content, String subject, Timestamp sendAt, int eventId, int orgId) {
    	int id = 0;
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String request = "INSERT INTO email (event_id, org_id, send_at, sender, sender_email, subject, content) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setInt(1, eventId);
			statement.setInt(2, orgId);
			statement.setTimestamp(3, sendAt);
			statement.setString(4, sender);
			statement.setString(5, senderEmail);
			statement.setString(6, subject);
			statement.setString(7, content);
			statement.executeUpdate();
			String getId = "Select e.id From email e Where e.sender_email = ? and e.content = ? and e.event_id = ?";
			statement = connection.prepareStatement(getId);
			statement.setString(1, senderEmail);
			statement.setString(2, content);
			statement.setInt(3, eventId);
			ResultSet rs = statement.executeQuery();
			if (rs != null && rs.next()) {
				id = rs.getInt("id");
			}
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch(SQLException e) {
			System.out.println("(createMessage) Connection error: " + e);
		}
		System.out.println(id);
		return id;
	}

	/** Deletes a message with the unique id assigned by the database. */
	public void deleteMessage(int id) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String request = "DELETE FROM \"kickin\".\"email\" WHERE \"email\".\"id\" = ?;";
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setInt(1, id);
			statement.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch(SQLException e) {
			System.out.println("(deleteOrganization) Connection error: " + e);
		}
	}


    /** A query to check whether a user with provided credentials exists.
	@returns a user if it exists
	@returns empty ResultSet if no user exists */
	public ResultSet login(String username, String email, byte[] password) {
		Base64.Encoder encoder = Base64.getEncoder();
		String passString = encoder.encodeToString(password);
		try {
			String request = "SELECT * FROM \"kickin\".\"user\""
					+ "WHERE (\"user\".\"username\" = ?"
					+ "OR \"user\".\"email\" = ?)"
					+ "AND \"user\".\"password\" = ?;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, username);
			statement.setString(2, email);
			statement.setString(3, passString);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(login) Error connecting: " + sqle);
			return null;
		}
	}
	
	/** Returns a salt added to the password for a user. */
	public ResultSet getSalt(String login) {
		try {
			String request = "SELECT \"user\".\"salt\" FROM \"kickin\".\"user\""
					+ "WHERE (\"user\".\"username\" = ?"
					+ "OR \"user\".\"email\" = ?);";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, login);
			statement.setString(2, login);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getSalt) Error connecting: " + sqle);
			return null;
		}
	}

	/** Returns user details for provided login. */
	public ResultSet getUserByLogin(String login) {
		try {
			String request = "SELECT * FROM \"kickin\".\"user\""
					+ "WHERE (\"user\".\"username\" = ?"
					+ "OR \"user\".\"email\" = ?);";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, login);
			statement.setString(2, login);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getUserByLogin) Error connecting: " + sqle);
			return null;
		}
	}

	public User getUserByToken(String token) {
		try {
			String request = "SELECT username FROM kickin.session WHERE token = ?";
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, token);
			ResultSet resultSet = statement.executeQuery();
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			while (resultSet != null && resultSet.next()) {
				User user = new User();
				user.setUsername(resultSet.getString(1));
				return user;
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return null;
	}

	/** Returns all users in the database. */
	public ResultSet getAllUsers() {
		try {
			String request = "SELECT * FROM \"kickin\".\"user\";";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getAlluser) Error connecting: " + sqle);
			return null;
		}
	}
	
	/** Returns specific set of users according to the provided filter. */
	public ResultSet getFilteredUsers(String filter) {
		if(filter == null || filter.isBlank()) {
			return getAllUsers();
		}
		filter = "%" + filter + "%";
		try {
			String request = "SELECT * FROM \"kickin\".\"user\""
					+ "WHERE \"user\".\"username\" LIKE ?"
					+ "OR \"user\".\"email\" LIKE ?;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, filter);
			statement.setString(2, filter);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getFilteredUsers) Error connecting: " + sqle);
			return null;
		}
	}
	
	/**
	 * Creates a new user in a database with provided username, email and password.
	 * The server stores a password securely using Base64 encoding with additional salt.
	 * */
	public void createUser(String email, String username, byte[] hashedPassword, byte[] salt) {
		Base64.Encoder encoder = Base64.getEncoder();
		String passString = encoder.encodeToString(hashedPassword);
		String saltString = encoder.encodeToString(salt);
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
    		String request = "INSERT INTO \"kickin\".\"user\" (\"email\", \"username\", \"password\", \"salt\") VALUES(?, ?, ?, ?)";
    		PreparedStatement statement = connection.prepareStatement(request);
    		statement.setString(1, email);
    		statement.setString(2, username);
    		statement.setString(3, passString);
    		statement.setString(4, saltString);
			statement.execute();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.close();
		} catch(SQLException e) {
			System.out.println("(createUser) Connection error: " + e);
		}
	}
	
	/** Deletes a user with specified email and username if such user exists. */
	public void deleteUser(String email, String username) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
    		String request = "DELETE FROM \"kickin\".\"user\""
    				+ "WHERE (\"user\".\"email\" = ?"
    				+ "AND \"user\".\"username\" = ?);";
    		PreparedStatement statement = connection.prepareStatement(request);
    		statement.setString(1, email);
    		statement.setString(2, username);
			statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.close();
		} catch(SQLException e) {
			System.out.println("(deleteUser) Connection error: " + e);
		}
	}

	/** Returns a token for a user. */
	public ResultSet getSessionToken(String username) {
		try {
			String request = "SELECT * FROM \"kickin\".\"session\""
					+ "WHERE \"session\".\"username\" = ?;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getSessionToken) Error connecting: " + sqle);
			return null;
		}
	}

	/** Returns all active sessions with appropriate tokens. */
	public ResultSet getSessionTokens() {
		try {
			String request = "SELECT * FROM \"kickin\".\"session\";";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getSessionTokens) Error connecting: " + sqle);
			return null;
		}
	}

	/** Returns a user who uses a token. */
	public ResultSet getSessionTokenByToken(String token) {
		try {
			String request = "SELECT * FROM \"kickin\".\"session\""
					+ "WHERE \"session\".\"token\" = ?;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, token);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getSessionToken) Error connecting: " + sqle);
			return null;
		}
	}

    /** Creates a session token in the database for the current user. */
	public void createSessionToken(String username, String token, Timestamp created) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
    		String request = "INSERT INTO \"kickin\".\"session\" (\"username\", \"token\", \"timestamp\") VALUES(?, ?, ?)";
    		PreparedStatement statement = connection.prepareStatement(request);
    		statement.setString(1, username);
    		statement.setString(2, token);
    		statement.setTimestamp(3, created);
			statement.execute();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.close();
		} catch(SQLException e) {
			System.out.println("(createSessionToken) Error connecting: " + e);
		}
	}
	
	/** Deletes a record with the provided token. */
	public void deleteToken(String token) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
	    	String request = "DELETE FROM \"kickin\".\"session\" WHERE \"session\".\"token\" = ?;";
	    	PreparedStatement statement = connection.prepareStatement(request);
	    	statement.setString(1, token);
			statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch(SQLException e) {
			System.out.println("(deleteToken) Connection error: " + e);
		}
	}

	/** Force makes a new token for a user. */
	public void updateSessionToken(String username, String token, Timestamp created) {
		String request = "UPDATE \"kickin\".\"session\" SET \"token\" = ?, \"timestamp\" = ? WHERE \"session\".\"username\" = ?";
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(request);
    		statement.setString(1, token);
    		statement.setTimestamp(2, created);
    		statement.setString(3, username);
            statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.close();
		} catch (SQLException e) {
			System.out.println("(updateSessionToken) Error connecting: " + e);
		}		
	}

	public void resetPassword(String username, byte[] hashedPassword, byte[] salt) {
		Base64.Encoder encoder = Base64.getEncoder();
		String passString = encoder.encodeToString(hashedPassword);
		String saltString = encoder.encodeToString(salt);
		String request = "UPDATE \"kickin\".\"user\" SET \"password\" = ?, \"salt\" = ? WHERE \"username\" = ?";
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement pStatement = connection.prepareStatement(request);
			pStatement.setString(1, passString);
			pStatement.setString(2, saltString);
			pStatement.setString(3, username);
			pStatement.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			System.out.println(pStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Returns details of the organization by provided id. */
	public ResultSet getOrganizationById(int id) {
		try {
			String request = "SELECT * FROM \"kickin\".\"organization\""
					+ "WHERE \"organization\".\"id\" = ?;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizationById) Error connecting: " + sqle);
			return null;
		}
	}

	/** Returns details of the organization by provided email. */
	public ResultSet getOrganizationByEmail(String email) {
		try {
			String request = "SELECT * FROM \"kickin\".\"organization\""
					+ "WHERE \"organization\".\"email\" = ?;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, email);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizationByEmail) Error connecting: " + sqle);
			return null;
		}
	}

    /** Returns all organizations in the database.  */
	public ResultSet getAllOrganizations() {
		try {
			String request = "SELECT * FROM \"kickin\".\"organization\";";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizations) Error connecting: " + sqle);
			return null;
		}
	}
	
	/** Used in search query on the admin page. */
	public ResultSet getFilteredOrganizations(String filter) {
		filter = "%" + filter + "%";
		try {
			String request = "SELECT * FROM \"kickin\".\"organization\""
					+ "WHERE \"organization\".\"email\" LIKE ?"
					+ "OR \"organization\".\"name\" LIKE ?"
					+ "OR \"organization\".\"website\" LIKE ?;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, filter);
			statement.setString(2, filter);
			statement.setString(3, filter);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getFilteredOrganizations) Error connecting: " + sqle);
			return null;
		}
	}

	/** Creates an organization in the database. */
	public void createOrganization(String name, String email, String website) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
    		String request = "INSERT INTO \"kickin\".\"organization\" (\"name\", \"email\", \"website\") VALUES(?, ?, ?)";
    		PreparedStatement statement = connection.prepareStatement(request);
    		statement.setString(1, name);
    		statement.setString(2, email);
    		statement.setString(3, website);
			statement.execute();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.close();
		} catch(SQLException e) {
			System.out.println("(createOrganization) Connection error: " + e);
		}
	}

    /** Deletes an organization in the database. */
	public void deleteOrganization(int id) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
	    	String request = "DELETE FROM \"kickin\".\"organization\" WHERE \"organization\".\"id\" = ?;";
	    	PreparedStatement statement = connection.prepareStatement(request);
	    	statement.setInt(1, id);
			statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch(SQLException e) {
			System.out.println("(deleteOrganization) Connection error: " + e);
		}
	}

    /** Changes organization details in the database by taking all arguments. */
	public void updateOrganization(int id, String newName, String newEmail, String newWebsite) {
		String request = "UPDATE \"kickin\".\"organization\""
				+ "SET \"name\" = ?, \"email\" = ?, \"website\" = ?"
				+ "WHERE \"session\".\"id\" = ?";
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(request);
    		statement.setString(1, newName);
    		statement.setString(2, newEmail);
    		statement.setString(3, newWebsite);
    		statement.setInt(4, id);
            statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.close();
		} catch (SQLException e) {
			System.out.println("(updateSessionToken) Error connecting: " + e);
		}	
	}
	
	/** Returns event details by provided id. */
	public ResultSet getEventById(int id) {
		try {
			String request = "SELECT * FROM \"kickin\".\"event\""
					+ "WHERE \"event\".\"id\" = ?;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizationById) Error connecting: " + sqle);
			return null;
		}
	}

    /** Returns all events stored in the database. */
	public ResultSet getAllEvents() {
		try {
			String request = "SELECT * FROM \"kickin\".\"event\";";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizationById) Error connecting: " + sqle);
			return null;
		}
	}

    /** Returns specific events stored in the database. */
	public ResultSet getFilteredEvents(String filter) {
		if(filter == null || filter.isBlank()) {
			return getAllEvents();
		}
		filter = "%" + filter + "%";
		try {
			String request = "SELECT * FROM \"kickin\".\"event\""
					+ " WHERE \"event\".\"name\" LIKE ? or \"event\".\"created_by\" LIKE ?;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, filter);
			statement.setString(2, filter);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizationById) Error connecting: " + sqle);
			return null;
		}
	}

	// Attachments
	/** Returns files matching a certain filter assuming the administrator
	 * access was already granted. */
	public ResultSet getFilteredFilesAdmin(FilterModel filter) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String toDate = "";
			String fromDate = "";
			String key = "";
			Boolean eng = !filter.getIsDutch();
			StringBuilder preparedQuery;
			StringBuilder whereQuery = new StringBuilder();
			if (eng) {
				preparedQuery = new StringBuilder("SELECT *, ts_rank(a.ts_english, q) as rank FROM kickin.attachment AS a, to_tsquery('english', ?) AS q, kickin.email AS e Where a.email_id = e.id");
			} else {
				preparedQuery = new StringBuilder("SELECT *, ts_rank(a.ts_dutch, q) as rank FROM kickin.attachment AS a, to_tsquery('dutch', ?) AS q, kickin.email AS e Where a.email_id = e.id");
			}
			if (goodDate(filter.getToDate())) {
				whereQuery.append(" AND e.send_at <= TO_TIMESTAMP(?, 'YYYY-MM-DD')");
				toDate = filter.getToDate();
			}
			if (goodDate(filter.getFromDate())) {
				whereQuery.append(" AND e.send_at >= TO_TIMESTAMP(?, 'YYYY-MM-DD')");
				fromDate = filter.getFromDate();
			}
			if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
				if (eng) {
					whereQuery.append(" AND a.ts_english @@ q");
				} else {
					whereQuery.append(" AND a.ts_dutch @@ q");
				}
				key = filter.getKeyword().replaceAll(" ", " & ");
			}
			if(whereQuery.length() > 0) {
				preparedQuery.append(whereQuery);
			}
			if (filter.getSort() == null || filter.getSort().equals("relevant")) {
				preparedQuery.append(" Order by rank DESC;");
			} else if (filter.getSort().equals("ASC")) {
				preparedQuery.append(" ORDER BY e.send_at ASC;");
			} else if (filter.getSort().equals("DESC")) {
				preparedQuery.append(" ORDER BY e.send_at DESC;");
			}
			PreparedStatement pStatement = connection.prepareStatement(preparedQuery.toString());
			pStatement.setString(1, key);
			int id = 2;
			if (!toDate.isEmpty()) {
				pStatement.setString(id, toDate);
				id++;
			}
			if (!fromDate.isEmpty()) {
				pStatement.setString(id, fromDate);
				id++;
			}
			ResultSet resultSet = pStatement.executeQuery();
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getFilteredFilesAdmin) Error connecting: " + sqle);
			return null;
		}
	}

	/** Returns files matching a certain filter. */
	public ResultSet getFilteredFiles(int orgId, FilterModel filter) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String toDate = "";
			String fromDate = "";
			String key = "";
			Boolean eng = !filter.getIsDutch();
			StringBuilder preparedQuery;
			StringBuilder whereQuery = new StringBuilder();
			if (eng) {
				preparedQuery = new StringBuilder("SELECT Distinct a.name, a.email_id, a.send_at, ts_rank(a.ts_english, q) as rank FROM kickin.attachment AS a, to_tsquery('english', ?) q, kickin.email e");
			} else {
				preparedQuery = new StringBuilder("SELECT Distinct a.name, a.email_id, a.send_at, ts_rank(a.ts_dutch, q) as rank FROM kickin.attachment AS a, to_tsquery('dutch', ?) q, kickin.email e");
			}
			preparedQuery.append(" JOIN kickin.isfor AS \"if\" ON e.id = \"if\".email_id AND \"if\".org_id = ? AND a.email_id = e.id");
			if (goodDate(filter.getToDate())) {
				whereQuery.append(" AND e.send_at <= TO_TIMESTAMP(?, 'YYYY-MM-DD')");
				toDate = filter.getToDate();
			}
			if (goodDate(filter.getFromDate())) {
				whereQuery.append(" AND e.send_at >= TO_TIMESTAMP(?, 'YYYY-MM-DD')");
				fromDate = filter.getFromDate();
			}
			if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
				if (eng) {
					whereQuery.append(" AND a.ts_english @@ q");
				} else {
					whereQuery.append(" AND a.ts_dutch @@ q");
				}
				key = filter.getKeyword().replaceAll(" ", " & ");
			}
			if(whereQuery.length() > 0) {
				preparedQuery.append(" WHERE").append(whereQuery.substring(" AND".length()));
			}
			if (filter.getSort() == null || filter.getSort().equals("relevant")) {
				preparedQuery.append(" Order by rank DESC;");
			} else if (filter.getSort().equals("ASC")) {
				preparedQuery.append(" ORDER BY e.send_at ASC;");
			} else if (filter.getSort().equals("DESC")) {
				preparedQuery.append(" ORDER BY e.send_at DESC;");
			}
			PreparedStatement pStatement = connection.prepareStatement(preparedQuery.toString());
			pStatement.setString(1, key);
			pStatement.setInt(2, orgId);
			int id = 3;
			if (!toDate.isEmpty()) {
				pStatement.setString(id, toDate);
				id++;
			}
			if (!fromDate.isEmpty()) {
				pStatement.setString(id, fromDate);
				id++;
			}
			ResultSet resultSet = pStatement.executeQuery();
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(filterFiles) Error connecting: " + sqle);
			return null;
		}
	}
	
	/** Matches what media is in attachment objects. */
	public void createAttachmentContents(List<String> docNames, List<String> docList) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			/*String createTable = "Drop table if exists attachment; " +
				"Create Table attachment ( " +
				"attachment_id Serial, " +
				"email_id int NOT NULL, " +
				"name varchar(255), " +
				"ts_english tsvector, " +
				"ts_dutch tsvector," +
				"content varchar(50000)," +
				"PRIMARY KEY(attachment_id), " +
				"FOREIGN KEY(email_id) REFERENCES email(id)" +
				");";
			PreparedStatement st = connection.prepareStatement(createTable);
			st.executeUpdate();*/
			PreparedStatement st;
			String fillTable = "Insert into attachment (email_id, name, content) " +
				"Values ((Select e.id From email e Where to_tsvector(e.attachments) @@ to_tsquery(?)), ?, ?);";
			st = connection.prepareStatement(fillTable);
			for (int i = 0; i < docNames.size(); i++) {
				String name = docNames.get(i);
				String content = docList.get(i);
				try {
					content = new String(content.getBytes("UTF-8"), "ISO-8859-1");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				st.setString(1, name);
				st.setString(2, name);
				st.setString(3, content);
				st.executeUpdate();
			}
			/*String createIndex = "Create Index idx_content On attachment using GIN(ts_content); " +
				"Update attachment Set ts_content = " +
				"setweight(to_tsvector(coalesce(name, '')), 'A') || setweight(coalesce(ts_content, ''), 'B'); " +
				"Reindex Table attachment;";
			st = connection.prepareStatement(createIndex);
			st.executeUpdate();*/
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch (SQLException sqle) {
			System.err.println("(createAttachmentContents) Error connecting: " + sqle);
		}
	}

	public void addContentCol(List<String> docNames, List<String> docList) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String fillTable = "Update attachment Set content = ? " +
				"Where name = ?;";
			PreparedStatement st = connection.prepareStatement(fillTable);
			for (int i = 0; i < docNames.size(); i++) {
				String name = docNames.get(i);
				String content = docList.get(i);
				try {
					content = new String(content.getBytes("UTF-8"), "ISO-8859-1");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				st.setString(1, content);
				st.setString(2, name);
				st.executeUpdate();
			}
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch (SQLException sqle) {
			System.err.println("(addContentCol) Error connecting: " + sqle);
		}
	}
	
	/**  Returns all attachments belonging to the particular organization. */
	public ResultSet getAttachmentsFor(int id) {
		try {
			String request = "SELECT * FROM \"kickin\".\"attachment\""
					+ "WHERE \"attachment\".\"email_id\" = ?;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizationById) Error connecting: " + sqle);
			return null;
		}
	}

	/**  Returns all attachments of all organizations. */
	public ResultSet getAllAttachments() {
		try {
			String request = "SELECT * FROM \"kickin\".\"attachment\";";
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			ResultSet resultSet = statement.executeQuery();
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizationById) Error connecting: " + sqle);
			return null;
		}
	}
	
	/** Checks whether an attachment with the given name exists. */
	public Boolean attachmentExists(String name) {
		Boolean ok = false;
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String request = "Select a.name From attachment a Where a.name = ?";
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, name);
			ResultSet rs = statement.executeQuery();
			if (rs != null && rs.next()) {
				if (rs.getString("name").equals(name)) {
					ok = true;
				}
			}
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch(SQLException e) {
			System.out.println("(createAttachment) Connection error: " + e);
		}
		return ok;
	}

	public void createAttachmentByName(int emailId, List<String> attachmentNames) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String request = "INSERT INTO \"kickin\".\"attachment\" (\"name\", \"email_id\", \"content\") VALUES(?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(request);
			FileHandler fh = new FileHandler();
			for (String name : attachmentNames) {
				if (attachmentExists(name)) {
					continue;
				}
				System.out.println("Trying to get the file: " + name);
				statement.setString(1, name);
				statement.setInt(2, emailId);
				String content = fh.handleFile(name);
				try {
					content = new String(content.getBytes("UTF-8"), "ISO-8859-1");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (content.equals("")) {
					continue;
				}
				statement.setString(3, content);
				statement.executeUpdate();
			}
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch(SQLException e) {
			System.out.println("(createAttachment) Connection error: " + e);
		}
	}
	
	/** Make a new attachment record in the database. */
	public void createAttachment(String name, int emailId, String content) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String request = "INSERT INTO \"kickin\".\"attachment\" (\"name\", \"email_id\", \"content\") VALUES(?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setString(1, name);
			statement.setInt(2, emailId);
			statement.setString(3, content);
			statement.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch(SQLException e) {
			System.out.println("(createAttachment) Connection error: " + e);
		}
	}
	
	/** Deletes an attachment with the unique id assigned by the database. */
	public void deleteAttachment(int id) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String request = "DELETE FROM \"kickin\".\"attachment\" WHERE \"attachment\".\"attachment_id\" = ?;";
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setInt(1, id);
			statement.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch(SQLException e) {
			System.out.println("(deleteOrganization) Connection error: " + e);
		}
	}

	// IsFor
	public ResultSet getOrganizationIds(int id) {
		try {
			String request = "SELECT * FROM \"kickin\".\"isfor\""
					+ "WHERE \"isfor\".\"email_id\" = ?;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizationById) Error connecting: " + sqle);
			return null;
		}
	}

	public void addOrganizationsToSpecificEmail(int emailId, List<Integer> orgIds) {
		String request = "Insert into isfor(email_id, org_id, opened) Values(?, ?, false)";
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			for (int id : orgIds) {
				statement.setInt(1, emailId);
				statement.setInt(2, id);
				System.out.println(request);
				statement.executeUpdate();
			}
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch (SQLException e) {
			System.out.println("(updateSessionToken) Error connecting: " + e);
		}
	}

	public void setEmailAsOpenedForOrg(int organizationId, int id) {
		String request = "UPDATE \"kickin\".\"isfor\""
				+ " SET \"opened\" = true"
				+ " WHERE \"org_id\" = ? AND \"email_id\" = ?;";
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(request);
    		statement.setInt(1, organizationId);
    		statement.setInt(2, id);
            statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.close();
		} catch (SQLException e) {
			System.out.println("(updateSessionToken) Error connecting: " + e);
		}	
	}

	public ResultSet getOrganizationIdsWhereRead(int id) {
		try {
			String request = "SELECT * FROM \"kickin\".\"isfor\""
					+ "WHERE \"isfor\".\"email_id\" = ?"
					+ "AND \"isfor\".\"opened\" = true;";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizationById) Error connecting: " + sqle);
			return null;
		}
	}

	public ResultSet getOrganizationInformationWhereRead(int id) {
		try {
			String request = "Select o.name, o.email, o.website From organization o, isfor i Where o.id = i.org_id and i.opened is true and i.email_id = ?";
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizationById) Error connecting: " + sqle);
			return null;
		}
	}
	
	/** Obtains which emails belong to the specified organization. */
	public ResultSet getEmailIdsForOrg(int orgId) {
		try {
			String request = "SELECT * FROM \"kickin\".\"isfor\""
					+ "WHERE \"isfor\".\"org_id\" = ?";
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setInt(1, orgId);
			ResultSet resultSet = statement.executeQuery();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
			return resultSet;
		} catch (SQLException sqle) {
			System.err.println("(getOrganizationById) Error connecting: " + sqle);
			return null;
		}
	}
	
	/** Create a database event record. */
	public void createEvent(String name, Timestamp startingOn, Timestamp endingOn, String createdBy, Timestamp createdAt) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
    		String request = "INSERT INTO \"kickin\".\"event\" (\"name\", \"start_date\", \"end_date\", \"created_by\", \"created_at\") VALUES(?, ?, ?, ?, ?)";
    		PreparedStatement statement = connection.prepareStatement(request);
    		statement.setString(1, name);
    		statement.setTimestamp(2, startingOn);
    		statement.setTimestamp(3, endingOn);
    		statement.setString(4, createdBy);
    		statement.setTimestamp(5, createdAt);
			statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.close();
		} catch(SQLException e) {
			System.out.println("(createUser) Connection error: " + e);
		}
	}

	/** Deletes an event with the unique id assigned by the database. */
	public void deleteEvent(int id) {
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
			String request = "DELETE FROM \"kickin\".\"event\" WHERE \"event\".\"id\" = ?;";
			PreparedStatement statement = connection.prepareStatement(request);
			statement.setInt(1, id);
			statement.executeQuery();
			connection.commit();
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.close();
		} catch(SQLException e) {
			System.out.println("(deleteOrganization) Connection error: " + e);
		}
	}
	
	/** Replaces the event name.
	 * @param id is to find an event. It cannot be changed. */
	public void updateEvent(int id, String name) {
		String request = "UPDATE \"kickin\".\"event\""
				+ " SET \"name\" = ?"
				+ " WHERE \"id\" = ?;";
		try {
			Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
			connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(request);
    		statement.setInt(2, id);
    		statement.setString(1, name);
            statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.close();
		} catch (SQLException e) {
			System.out.println("(updateSessionToken) Error connecting: " + e);
		}			
	}
}
