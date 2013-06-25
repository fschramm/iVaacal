package edu.hm.cs.ivaacal.model.persistence;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Persistence object for a company.
 */
public class CompanyPO {

    private String location;

    private Collection<CompanyWorkerPO> workers = new LinkedList<>();

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Collection<CompanyWorkerPO> getWorkers() {
        return workers;
    }

    public void setWorkers(Collection<CompanyWorkerPO> workers) {
        this.workers = workers;
    }
}
