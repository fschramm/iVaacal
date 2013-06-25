package edu.hm.cs.ivaacal.model;

import java.util.*;

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
	final private Map<String, Worker> workerMap;

    /**
     * Indicates whether the group is visible or not.
     */
    private boolean isVisible = true;


	public Group() {
		workerMap = new HashMap<>();
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

	public Map<String, Worker> getWorkerMap() {
		return workerMap;
	}

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public String[] getCalendarEmails() {
        List<String> calendarEmails = new LinkedList<>();
        for (Worker worker: workerMap.values()) {
            calendarEmails.add(worker.getCalendarEmail());
        }

        return calendarEmails.toArray(new String[calendarEmails.size()]);
    }
}
