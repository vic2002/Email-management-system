package server.model;

import java.sql.Timestamp;

/** Event model with appropriate getters and setters. */
public class Event {
	private int id;
	private String name;
	private String createdBy;
	private Timestamp createdAt;
	private Timestamp startingOn;
	private Timestamp endingOn;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getStartingOn() {
		return startingOn;
	}
	public void setStartingOn(Timestamp startingOn) {
		this.startingOn = startingOn;
	}
	public Timestamp getEndingOn() {
		return endingOn;
	}
	public void setEndingOn(Timestamp endingOn) {
		this.endingOn = endingOn;
	}
}
