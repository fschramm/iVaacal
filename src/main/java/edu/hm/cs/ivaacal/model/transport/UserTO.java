package edu.hm.cs.ivaacal.model.transport;

import java.util.Collection;
import java.util.LinkedList;

/**
 * User Transport Object.
 */
public class UserTO {

	/**
	 * The name of the user.
	 */
	private String name;

	/**
	 * The groups that the user has arranged and saved in his account.
	 */
	final private Collection<GroupTO> groups;

	public UserTO() {
		groups = new LinkedList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<GroupTO> getGroups() {
		return groups;
	}

}
