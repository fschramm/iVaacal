package edu.hm.cs.ivaacal.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Representation of a User of iVaaCal.
 */
public class User {

	/**
	 * The name of the user.
	 */
	private String name;

	/**
	 * The groups that the user has arranged and saved in his account.
	 */
	final private List<Group> groups;

	public User() {
		groups = new LinkedList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Group> getGroups() {
		return groups;
	}
}
