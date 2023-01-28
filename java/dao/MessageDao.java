package dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import db.DBCall;
import server.model.FilterModel;
import server.model.MessageServer;
import view.model.AdminViewMessage;
import view.model.AdminViewMessages;
import view.model.Message;
import view.model.SearchMessage;
import view.model.SearchMessages;

public class MessageDao {
	private DBCall dbCall;
	private AttachmentDao attDao;
	private IsForDao isForDao;
	
	public MessageDao() {
		this.dbCall = new DBCall();
		this.attDao = new AttachmentDao();
		this.isForDao = new IsForDao();
	}

	/** Returns a list of messages matching a certain request (filter). 
	 * @param orgId is an organization id which is logged in right now.
	 * Used to display only email belonging to this organization. */
	public SearchMessages filterMessages(int orgId, FilterModel filter) {
		SearchMessages messages = new SearchMessages();
		ResultSet rs = dbCall.filterMessages(orgId, filter);
		try {
            if (rs != null) {
                while (rs.next()) {
                	SearchMessage sm = new SearchMessage();
                    sm.setId(rs.getInt("id"));
                    sm.setSender(rs.getString("sender"));
                    sm.setSenderEmail(rs.getString("sender_email"));
                    sm.setSubject(rs.getString("subject"));
                    sm.setSendAt(rs.getTimestamp("send_at"));
                    messages.getMessages().add(sm);
                }
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
		return messages;
	}

	/** Returns a list of messages matching a certain request (filter). */
	public SearchMessages filterMessages(FilterModel filter) {
		SearchMessages messages = new SearchMessages();
		ResultSet rs = dbCall.filterMessages(filter);
		try {
			while (rs != null && rs.next()) {
				SearchMessage sm = new SearchMessage();
				sm.setId(rs.getInt("id"));
				sm.setSender(rs.getString("sender"));
				sm.setSenderEmail(rs.getString("sender_email"));
				sm.setSubject(rs.getString("subject"));
				sm.setSendAt(rs.getTimestamp("send_at"));
				messages.addMessage(sm);
			}
		} catch (SQLException e) {
			System.err.println(e);
		}
		return messages;
	}
	/** Returns a list of messages matching a certain request (filter)
	 * assuming at this stage an administrator access is already granted. */
	public AdminViewMessages filterMessagesAdmin(FilterModel filter) {
		AdminViewMessages messages = new AdminViewMessages();
		ResultSet rs = dbCall.filterMessages(filter);
		try {
			while (rs != null && rs.next()) {
               	AdminViewMessage avm = new AdminViewMessage();
               	avm.setId(rs.getInt("id"));
                avm.setSender(rs.getString("sender"));
               	avm.setSenderEmail(rs.getString("sender_email"));
                avm.setSubject(rs.getString("subject"));
              	avm.setSendAt(rs.getTimestamp("send_at"));
               	avm.setNumberOfAttachments(attDao.getAttachmentsFor(avm.getId()).size());
               	avm.setSentTo(isForDao.getNumberOfSent(avm.getId()));
                avm.setReadBy(isForDao.getNumberOfRead(avm.getId()));
                messages.addMessage(avm);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
		return messages;
	}
	
	public AdminViewMessages filterMessagesAdmin(int orgId, FilterModel filter) {
		AdminViewMessages messages = new AdminViewMessages();
		ResultSet rs = dbCall.filterMessages(orgId, filter);
		try {
            if (rs != null) {
                while (rs.next()) {
                	AdminViewMessage avm = new AdminViewMessage();
                	avm.setId(rs.getInt("id"));
                	avm.setSender(rs.getString("sender"));
                	avm.setSenderEmail(rs.getString("sender_email"));
                	avm.setSubject(rs.getString("subject"));
                	avm.setSendAt(rs.getTimestamp("send_at"));
                	avm.setNumberOfAttachments(attDao.getAttachmentsFor(avm.getId()).size());
                	avm.setSentTo(isForDao.getNumberOfSent(avm.getId()));
                	avm.setReadBy(isForDao.getNumberOfRead(avm.getId()));
                    messages.getMessages().add(avm);
                }
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
		return messages;
	}
	
	/** Returns a Message object which has the specified <b>id</b>. 
	 * @param id is a unique id assigned by a database to the email. */
	public Message getMessageById(int id) {
		Message message = null;
		ResultSet rs = dbCall.getMessageById(id);
		 try {
            if (rs != null && rs.next()) {
            	message = new Message();
            	message.setId(rs.getInt("id"));
            	message.setSender(rs.getString("sender"));
            	message.setSenderEmail(rs.getString("sender_email"));
            	message.setSubject(rs.getString("subject"));
            	message.setSendAt(rs.getTimestamp("send_at"));
            	message.setContent(rs.getString("content"));
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
		return message;
	}

	/** Create a message. 
	 * @param already formed Message object. */
	public int createMessage(MessageServer msg) {
		return dbCall.createMessage(msg.getSenderEmail(), msg.getSender(), msg.getContent(), msg.getSubject(), msg.getSendAt(), msg.getEventId(), msg.getOrgId());
	}
	
	/** Deletes a message.
	 * @param id is a unique id assigned by a database to the email. 
	 */
	public void deleteMessage(int id) {
		dbCall.deleteMessage(id);
	}

}
