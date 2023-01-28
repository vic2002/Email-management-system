package server.model;

import java.util.List;

/** Attachments model with appropriate getters and setters. */
public class Attachments {
	private int numberOfAttachments;
	private List<Attachment> attachments;
	public int getNumberOfAttachments() {
		return numberOfAttachments;
	}
	public void setNumberOfAttachments(int numberOfAttachments) {
		this.numberOfAttachments = numberOfAttachments;
	}
	public List<Attachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
}
