package view.model;

import java.sql.Timestamp;

/**A model describing from what a file consists of. */
public class File {
	private int size;
	private String path;
	private String name;
	private Timestamp sendAt;
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getSendAt() {
		return sendAt;
	}
	public void setSendAt(Timestamp sendAt) {
		this.sendAt = sendAt;
	}
}
