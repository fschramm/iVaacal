package edu.hm.cs.ivaacal.model.persistence;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Persistence object for a company.
 */
public class CompanyPO {

    private String location;

    private Collection<WorkerPO> workers = new LinkedList<>();

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Collection<WorkerPO> getWorkers() {
        return workers;
    }

    public void setWorkers(Collection<WorkerPO> workers) {
        this.workers = workers;
    }
}
