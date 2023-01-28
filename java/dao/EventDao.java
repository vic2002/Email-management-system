package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBCall;
import server.model.Event;

public class EventDao {
	private DBCall dbCall = new DBCall();
	
	/** Returns an Event object by id. 
	 * @param id is a unique id assigned by a database to the event. */
	public Event getById(int id) {
		ResultSet rs = dbCall.getEventById(id);
		Event event = null;
		try {
			if(rs != null && rs.next()) {
				event = new Event();
				event.setId(id);
				event.setName(rs.getString("name"));
				event.setCreatedAt(rs.getTimestamp("created_at"));
				event.setCreatedBy(rs.getString("created_by"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
		return event;
	}
	
	/** Returns a list of all events. */
	public List<Event> getAll() {
		ResultSet rs = dbCall.getAllEvents();
		List<Event> result = new ArrayList<Event>();
		try {
			while(rs != null && rs.next()) {
				Event event = new Event();
				event.setId(rs.getInt("id"));
				event.setName(rs.getString("name"));
				event.setCreatedAt(rs.getTimestamp("created_at"));
				event.setCreatedBy(rs.getString("created_by"));
				event.setStartingOn(rs.getTimestamp("start_date"));
				event.setEndingOn(rs.getTimestamp("end_date"));
				result.add(event);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
		return result;
	}

	/** Returns a list of event matching a certain request (filter). */
	public List<Event> getFiltered(String filter) {
		ResultSet rs = dbCall.getFilteredEvents(filter);
		List<Event> result = new ArrayList<Event>();
		try {
			while(rs != null && rs.next()) {
				Event event = new Event();
				event.setId(rs.getInt("id"));
				event.setName(rs.getString("name"));
				event.setCreatedAt(rs.getTimestamp("created_at"));
				event.setCreatedBy(rs.getString("created_by"));
				event.setStartingOn(rs.getTimestamp("start_date"));
				event.setEndingOn(rs.getTimestamp("end_date"));
				result.add(event);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
		return result;
	}

	/** Creates an event. 
	 * @param already constructed Event object. */
	public void createEvent(Event event) {
		dbCall.createEvent(event.getName(), event.getStartingOn(), event.getEndingOn(), event.getCreatedBy(), event.getCreatedAt());
	}

	/** Deletes an already existing event in the system.
	 * @param id is a unique id assigned by a database to the event. */
	public void deleteEvent(int id) {
		dbCall.deleteEvent(id);
	}

	/** Updates an already existing event in the system.
	 * @param id is a unique id assigned by a database to the event. It will not be changed!
	 * @param name is a name of the event. */
	public void update(int id, String name) {
		dbCall.updateEvent(id, name);
	}
}
