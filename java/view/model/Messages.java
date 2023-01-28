package view.model;

import java.util.List;

/** A model describing from what a list of messages consists of. */
public class Messages {
	private int numberOfMessages;
	private List<Message> messages;
	public int getNumberOfMessages() {
		return numberOfMessages;
	}
	public void setNumberOfMessages(int numberOfMessages) {
		this.numberOfMessages = numberOfMessages;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
}
