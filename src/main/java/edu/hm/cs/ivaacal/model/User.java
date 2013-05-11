package edu.hm.cs.ivaacal.model;

import java.util.HashMap;
import java.util.Map;

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
	final private Map<String, Group> groupMap;

	public User() {
		groupMap = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Group> getGroupMap() {
		return groupMap;
	}
}
