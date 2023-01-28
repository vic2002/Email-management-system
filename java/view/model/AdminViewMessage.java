package view.model;

import java.sql.Timestamp;

/** A model describing how an administrator sees a message. */
public class AdminViewMessage {
	private int id;
	private Timestamp sendAt;
	private String sender;
	private String senderEmail;
	private String subject;
	private int numberOfAttachments;
	private int sentTo;
	private int readBy;
	
	public Timestamp getSendAt() {
		return sendAt;
	}
	public void setSendAt(Timestamp sendAt) {
		this.sendAt = sendAt;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNumberOfAttachments() {
		return numberOfAttachments;
	}
	public void setNumberOfAttachments(int numberOfAttachments) {
		this.numberOfAttachments = numberOfAttachments;
	}
	public int getSentTo() {
		return sentTo;
	}
	public void setSentTo(int sentTo) {
		this.sentTo = sentTo;
	}
	public int getReadBy() {
		return readBy;
	}
	public void setReadBy(int readBy) {
		this.readBy = readBy;
	}

}
