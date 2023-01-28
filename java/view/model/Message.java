package view.model;

import java.sql.Timestamp;

import server.model.Attachments;

/** A model describing from what a message consists of. */
public class Message {
	private int id;
	private Timestamp sendAt;
	private String sender;
	private String senderEmail;
	private String subject;
	private String content;
	private Attachments attachments;
	
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Attachments getAttachments() {
		return attachments;
	}
	public void setAttachments(Attachments attachments) {
		this.attachments = attachments;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
