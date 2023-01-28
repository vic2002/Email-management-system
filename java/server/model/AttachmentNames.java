package server.model;

import java.util.List;

public class AttachmentNames {
    private int emailId;
    private List<String> attachmentNames;

    public AttachmentNames(int emailId, List<String> attachmentNames) {
        this.emailId = emailId;
        this.attachmentNames = attachmentNames;
    }

    public AttachmentNames() {

    }

    public int getEmailId() {
        return emailId;
    }

    public void setEmailId(int emailId) {
        this.emailId = emailId;
    }

    public List<String> getAttachmentNames() {
        return attachmentNames;
    }

    public void setAttachmentNames(List<String> attachmentNames) {
        this.attachmentNames = attachmentNames;
    }


    @Override
    public String toString() {
        return "AttachmentNames";
    }
}
