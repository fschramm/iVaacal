package edu.hm.cs.ivaacal.model.persistence;

import java.util.Collection;
import java.util.LinkedList;


/**
 * Persistence object for groups. Only contains key values.
 */
public class GroupPO {
	private String name;
	private Collection<WorkerPO> workers = new LinkedList<>();
    private boolean isVisible;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<WorkerPO> getWorkers() {
		return workers;
	}

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
