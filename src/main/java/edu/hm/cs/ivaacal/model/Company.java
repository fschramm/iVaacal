package edu.hm.cs.ivaacal.model;

import java.util.Map;

/**
 *  A Company containing workers.
 */
public interface Company {

    /**
     * Get all workers from the company, with their id as key.
     * @return The workers in a map.
     */
	public Map<String, Worker> getWorkerMap();

    /**
     * Returns the physical location of the company as a string.
     * @return The location of the company as a string.
     */
    public String getLocation();

}
