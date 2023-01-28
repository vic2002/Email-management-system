package view.model;

import java.util.List;

import server.model.Event;

/** A model describing how the list of events looks like. */
public class Events {
	private int numberOfEvents;
	private List<Event> events;
	public int getNumberOfEvents() {
		return numberOfEvents;
	}
	public void setNumberOfEvents(int numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
}
