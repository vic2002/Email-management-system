package view.model;

import java.util.ArrayList;
import java.util.List;

/** A model describing from what a list of messages consists of. Used in case something is typed in the search field. */
public class SearchMessages {
	private int numberOfMessages;
	private List<SearchMessage> messages;

	public List<SearchMessage> getMessages() {
		return messages;
	}

	public void addMessage(SearchMessage sm) {
		messages.add(sm);
	}
	
	public void setMessages(List<SearchMessage> messages) {
		this.messages = messages;
	}
	
	public SearchMessages() {
		messages = new ArrayList<SearchMessage>();
	}

	public int getNumberOfMessages() {
		return numberOfMessages;
	}

	public void setNumberOfMessages(int numberOfMessages) {
		this.numberOfMessages = numberOfMessages;
	}
}
