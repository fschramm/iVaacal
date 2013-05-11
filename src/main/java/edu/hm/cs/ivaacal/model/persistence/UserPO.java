package edu.hm.cs.ivaacal.model.persistence;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Persistence object for users. Only contains key values.
 */
public class UserPO {

	private Collection<GroupPO> groups = new LinkedList<>();

	public Collection<GroupPO> getGroups() {
		return groups;
	}
}
