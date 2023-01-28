package server.model;

/** FilterModel is a class responsible for finding objects matching a certain request.
 * <b>keyword</b> is a keyword search. It could be:
 * — an email topic word;
 * — an email body word;
 * — a sender email;
 * — a sender name.
 *  <b>fromDate</b> and <b>toDate</b> are a time range set at the search page.
 *  <b>sort</b> is a search page setting "Sort by:".
 *  <b>read</b> is a search page setting "Read/Unread".
 *  <b>isDutch</b> is a search page setting.
 *  */
public class FilterModel {
	private String keyword;
	private String fromDate;
	private String toDate;
	private String sort;
	private String read;
	private boolean isDutch;
	private String status;
	
	/* Explicit constructor for Object mapper */
	public FilterModel() { }
	
	/* For testing */
	public FilterModel(String keyword, String fromDate, String toDate, String sort, boolean isDutch,String status) {
		this.keyword = keyword;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.sort = sort;
		this.isDutch = isDutch;
		this.status = status;
	}
	
	public String getKeyword() {
		return this.keyword;
	}

	public String getFromDate() {
		return this.fromDate;
	}

	public String getToDate() {
		return this.toDate;
	}

	public String getSort() {
		return this.sort;
	}

	public Boolean getIsDutch() {
		return this.isDutch;
	}

	public String getStatus() {
		return this.status;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setIsDutch(Boolean isDutch) {
		this.isDutch = isDutch;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "{keyword: " + this.keyword + ", fromDate: "
				+ this.fromDate + ", toDate: " +this.toDate + ", sort: " + this.sort + ", isDutch: " + this.isDutch +
			", status: " + this.status;
	}

	public String getRead() {
		return read;
	}

	public void setRead(String read) {
		this.read = read;
	}
}