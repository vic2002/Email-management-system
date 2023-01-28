package server.model;

import java.util.List;

/** OrganizationIds is a special class to contain only unique identifiers. */
public class OrganizationIds {
    private int emailId;
    private List<Integer> organizationIds;

    public OrganizationIds(int emailId, List<Integer> organizationIds) {
        this.emailId = emailId;
        this.organizationIds = organizationIds;
    }

    public OrganizationIds() {

    }

    public void setEmailId(int emailId) {
        this.emailId = emailId;
    }

    public void setOrganizationIds(List<Integer> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public int getEmailId() {
        return this.emailId;
    }

    public List<Integer> getOrganizationIds() {
        return this.organizationIds;
    }

    @Override
    public String toString() {
        return "OrganizationIds";
    }
}
