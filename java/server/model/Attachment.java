package server.model;

import java.sql.Timestamp;

/** Attachment model with appropriate getters and setters. */
public class Attachment {
	private int emailId;
	private Timestamp sendAt;
	private String name;
	private String content;
	public int getEmailId() {
		return emailId;
	}
	public void setEmailId(int emailId) {
		this.emailId = emailId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Timestamp getSendAt() {
		return sendAt;
	}
	public void setSendAt(Timestamp sendAt) {
		this.sendAt = sendAt;
	}
}
