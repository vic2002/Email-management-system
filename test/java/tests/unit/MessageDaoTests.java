package tests.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import dao.EventDao;
import dao.MessageDao;
import server.model.Event;
import server.model.FilterModel;
import server.model.MessageServer;
import view.model.SearchMessages;

public class MessageDaoTests {
	/* Messages have events as foreign keys in the database and therefore
	 * they cannot exist without appropriate events. */
	/*private EventDao eventDao;
	private Event event1;
	private Event event2;
	private Event event3;
	private Event event4;
	
	List<Event> events;
	
	/* Messages linked to IDs. */
	/*private MessageDao messageDao;
	private MessageServer message1;
	private MessageServer message2;
	private MessageServer message3;
	private MessageServer message4;
	
	 Message IDs returned by a server. Initially set to -1.
	private int m1_id = -1;
	private int m2_id = -1;
	private int m3_id = -1;
	private int m4_id = -1;
	
	@BeforeEach
	public void setup() {
		makeEvents();
		makeMessages();
	}
	
	public void makeEvents() {
		eventDao = new EventDao();
		
		event1 = new Event();
		event2 = new Event();
		event3 = new Event();
		event4 = new Event();
		
		eventDao.createEvent(event1);
		eventDao.createEvent(event1);
		eventDao.createEvent(event1);
		eventDao.createEvent(event1);
		
		events = eventDao.getAll();
	}
	public void makeMessages() {
		messageDao = new MessageDao();
		makeEvents();
		message1 = new MessageServer (1, 1, new Timestamp((long) 40.000), "Inter-Actief",
				"mail@inter-actief.nl", "Invitation", "You are invited to the party!", null);
		message2 = new MessageServer (2, 2, 
				null, //has no timestamp 
				"Kick-In", "kick-in@mail.nl", "Invitation", "You are invited to the party!", null);
		message3 = new MessageServer(3, 3, new Timestamp((long) 150.000), "Comitee organization",
				"studentservices@utwente.nl", null, "Message with no title!", null);
		message4 = new MessageServer(1, 4, new Timestamp((long) 200.000), "Some organization",
				"org@mail.nl", "Reply", "Thank you!", null);
		
		m1_id = messageDao.createMessage(message1);
		m2_id = messageDao.createMessage(message2);
		m3_id = messageDao.createMessage(message3);
		m4_id = messageDao.createMessage(message4);
		
	}
	
	
	@Test
	public void checkIfMessagesGetId() {
		assertFalse(m1_id == -1);
		assertFalse(m2_id == -1);
		assertFalse(m3_id == -1);
		assertFalse(m4_id == -1);
	}

	@Test
	public void checkIfMessagesAreDeleted() {
		int initial_count = messageDao.filterMessages(1, 
				new FilterModel(null, null, null, null, false, null)).getNumberOfMessages(); //get all messages
		assertEquals(4, initial_count);
		messageDao.deleteMessage(m1_id);
		assertEquals(3, messageDao.filterMessages(null).getNumberOfMessages());
		messageDao.deleteMessage(m2_id);
		assertEquals(2, messageDao.filterMessages(null).getNumberOfMessages());
		messageDao.deleteMessage(m3_id);
		assertEquals(1, messageDao.filterMessages(null).getNumberOfMessages());
		messageDao.deleteMessage(m4_id);
		assertEquals(0, messageDao.filterMessages(null).getNumberOfMessages());
	}

	@Test
	public void checkIfMessagesAreFilteredProperlyByKeyword() {
		FilterModel filter_by_title_keyword = new FilterModel("Invitation", null, null, "", false, "");
		SearchMessages filtered1 = messageDao.filterMessages(filter_by_title_keyword);
		assertEquals(2, filtered1.getNumberOfMessages());
		
		FilterModel filter_by_text_keyword = new FilterModel("title", null, null, null, false, null);
		SearchMessages filtered2 = messageDao.filterMessages(filter_by_text_keyword);
		assertEquals(1, filtered2.getNumberOfMessages());
		
		FilterModel lowercase_and_uppercase = new FilterModel("TitLE", null, null, null, false, null);
		SearchMessages filtered3 = messageDao.filterMessages(lowercase_and_uppercase);
		assertEquals(1, filtered3.getNumberOfMessages());
		
		FilterModel filter_by_email = new FilterModel("kick-in@mail.nl", null, null, null, false, null);
		SearchMessages filtered4 = messageDao.filterMessages(filter_by_email);
		assertEquals(1, filtered4.getNumberOfMessages());
	}
	
	@AfterEach
	public void destroyTestData() {
		deleteMessages();
		deleteEvents();
	}

	public void deleteMessages() {
		messageDao.deleteMessage(m1_id);
		messageDao.deleteMessage(m2_id);
		messageDao.deleteMessage(m3_id);
		messageDao.deleteMessage(m4_id);
	}

	public void deleteEvents() {
		for (Event event : events) {
			eventDao.deleteEvent(event.getId());
		}
	}
*/
	
}
