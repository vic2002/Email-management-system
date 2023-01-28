package view.model;

import java.util.ArrayList;
import java.util.List;

/** A model describing how an administrator sees the list of messages. */
public class AdminViewMessages {
	private int numberOfMessages;
	private List<AdminViewMessage> messages;
	public AdminViewMessages() {
		messages = new ArrayList<AdminViewMessage>();
		numberOfMessages = 0;
	}
	public void addMessage(AdminViewMessage message) {
		messages.add(message);
	}
	public int getNumberOfMessages() {
		return numberOfMessages;
	}
	public void setNumberOfMessages(int numberOfMessages) {
		this.numberOfMessages = numberOfMessages;
	}
	public List<AdminViewMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<AdminViewMessage> messages) {
		this.messages = messages;
	}
}
