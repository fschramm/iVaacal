package edu.hm.cs.ivaacal.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Representation of a Worker-Group. Users can arrange workers in groups for easier access.
 */
public class Group {

	/**
	 * The name of the group.
	 */
	private String name;

	/**
	 * The next Date where all workers in the group share at least a defined amount of free time.
	 */
	private Date nextPossibleDate;

	/**
	 * The workers arranged in this group.
	 */
	final private List<Worker> workers;


	public Group() {
		workers = new LinkedList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getNextPossibleDate() {
		return nextPossibleDate;
	}

	public void setNextPossibleDate(Date nextPossibleDate) {
		this.nextPossibleDate = nextPossibleDate;
	}

	public List<Worker> getWorkers() {
		return workers;
	}
}
