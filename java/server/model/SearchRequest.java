package server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SearchRequest {
    private String keyword;
    private int fromDate;
    private int toDate;
    private boolean read;
    private boolean unRead;
    private String sort;

    public SearchRequest() {
    }


    public SearchRequest(String keyword, int fromDate, int toDate, boolean read, boolean unRead) {
        this.keyword = keyword;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.read = read;
        this.unRead = unRead;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public int getFromDate() {
        return this.fromDate;
    }

    public int getToDate() {
        return this.toDate;
    }

    public boolean getUnRead() {
        return this.unRead;
    }

    public boolean getRead() {
        return this.read;
    }

    public String getSort() {
        return this.sort;
    }

    public String toString() {
        return "Keyword : " + this.keyword + "\n" +
                this.fromDate + "\n" +
                this.toDate + "\n" +
                this.read + "\n" +
                this.unRead + "\n" +
                this.sort;
    }
}


