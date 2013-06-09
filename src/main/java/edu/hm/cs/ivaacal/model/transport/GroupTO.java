package edu.hm.cs.ivaacal.model.transport;

import edu.hm.cs.ivaacal.model.Worker;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Femy
 * Date: 11/05/13
 * Time: 16:58
 * To change this template use File | Settings | File Templates.
 */
public class GroupTO {

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
	final private Collection<Worker> workers;

    /**
     * Indicates whether the group is visible or not.
     */
    private boolean isVisible;


	public GroupTO() {
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

	public Collection<Worker> getWorkers() {
		return workers;
	}

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
