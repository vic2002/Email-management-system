package server.model;

import java.sql.Timestamp;

/** MessageServer model with appropriate getters and setters. */
public class MessageServer {
    private int eventId;
    private int orgId;
    private Timestamp sendAt;
    private String sender;
    private String senderEmail;
    private String subject;
    private String content;
    private Attachments attachments;

    /* For testing */
    public MessageServer (int eventId, int orgId, Timestamp sendAt, String sender,
    		String senderEmail, String subject, String content, Attachments attachments) {
    	this.eventId = eventId;
    	this.orgId = orgId;
    	this.sendAt = sendAt;
    	this.sender = sender;
    	this.senderEmail = senderEmail;
    	this.subject = subject;
    	this.content = content;
    	this.attachments = attachments;
    }
    
    /* Explicit constructor for Object mapper */
    public MessageServer () {}
    
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
    public int getOrgId() {
        return orgId;
    }
    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }
    public int getEventId() {
        return eventId;
    }
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

}
