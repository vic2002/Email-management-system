package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBCall;
import db.FileHandler;
import server.model.Attachment;
import server.model.AttachmentNames;
import server.model.FilterModel;

public class AttachmentDao {
	private DBCall dbCall = new DBCall();
	
	/**  Returns a list representation of all attachments belonging to the particular organization. */
	public List<Attachment> getAttachmentsFor(int id) {
		ResultSet rs = dbCall.getAttachmentsFor(id);
		List<Attachment> attachments = new ArrayList<>();
		try {
			while(rs != null && rs.next()) {
				Attachment a = new Attachment();
				a.setEmailId(rs.getInt("email_id"));
				a.setName(rs.getString("name"));
				attachments.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return attachments;
	}
	
	/** Returns a list representation of attachments for all organization. */
	public List<Attachment> getAll() {
		ResultSet rs = dbCall.getAllAttachments();
		List<Attachment> attachments = new ArrayList<>();
		try {
			while(rs != null && rs.next()) {
				Attachment attach = new Attachment();
				attach.setName(rs.getString("name"));
				attach.setEmailId(rs.getInt("email_id"));
				attachments.add(attach);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return attachments;
	}

	/** Returns a list representation of attachments matching a certain request (filter). 
	 * @param orgId is an organization id which is currently logged in. */	
	public List<Attachment> getFiltered(int orgId, FilterModel filter) {
		ResultSet result = dbCall.getFilteredFiles(orgId, filter);
		List<Attachment> attachments = new ArrayList<>();
		try {
			while (result != null && result.next()) {
				Attachment attach = new Attachment();
				attach.setName(result.getString("name"));
				attach.setEmailId(result.getInt("email_id"));
				attach.setSendAt(result.getTimestamp("send_at"));
				attachments.add(attach);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return attachments;
	}

	/** Returns a list representation of attachments matching a certain request
	 * (filter) assuming at this stage an administrator access is already granted. */
	public List<Attachment> getFilteredAdmin(FilterModel filter) {
		ResultSet result = dbCall.getFilteredFilesAdmin(filter);
		List<Attachment> attachments = new ArrayList<>();
		try {
			while(result != null && result.next()) {
				Attachment attach = new Attachment();
				attach.setName(result.getString("name"));
				attach.setEmailId(result.getInt("email_id"));
				attach.setSendAt(result.getTimestamp("send_at"));
				attachments.add(attach);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return attachments;
	}
	
	/** Creates an attachment. 
	 * @param already constructed Attachment object. */
	public void create(Attachment attach) {
		dbCall.createAttachment(attach.getName(), attach.getEmailId(), attach.getContent());
		FileHandler fh = new FileHandler();
		fh.createPreview(attach.getName());
	}

	public void create(AttachmentNames at) {
		dbCall.createAttachmentByName(at.getEmailId(), at.getAttachmentNames());
	}
	
	/** Deletes an already existing attachment in the system.
	 * @param id is a unique id assigned by a database to the attachment. */
	public void deleteById(int id) {
		dbCall.deleteAttachment(id);
	}

}
