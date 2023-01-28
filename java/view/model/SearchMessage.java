package view.model;

import java.sql.Timestamp;

/** A model describing from what a message consists of. Used in case something is typed in the search field. */
public class SearchMessage {
	private int id;
	private String sender;
	private String senderEmail;
	private String subject;
	private Timestamp sendAt;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSenderEmail() {
		return senderEmail;
	}
	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Timestamp getSendAt() {
		return sendAt;
	}
	public void setSendAt(Timestamp sendAt) {
		this.sendAt = sendAt;
	}
	@Override
	public String toString() {
		return "ID: " + id + "; sender: " + sender + "; senderEmail: " + senderEmail + "; subject: " + subject + "; sendAt: " + sendAt;
	}
}
