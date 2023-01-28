package server.model;

import java.util.List;

/** ReadBy model with appropriate getters and setters. */
public class ReadBy {

    private List<Organization> orgs;

    public void setOrgs(List<Organization> orgs) {
        this.orgs = orgs;
    }

    public List<Organization> getOrgs() {
        return this.orgs;
    }

    public ReadBy() {

    }

    public ReadBy(List<Organization> orgs) {
        this.orgs = orgs;
    }

    @Override
    public String toString() {
        return "hi";
    }
}
