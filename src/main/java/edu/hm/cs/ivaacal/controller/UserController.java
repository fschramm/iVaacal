package edu.hm.cs.ivaacal.controller;

import edu.hm.cs.ivaacal.exception.ModifyUserException;
import edu.hm.cs.ivaacal.model.User;
import edu.hm.cs.ivaacal.model.Worker;
import edu.hm.cs.ivaacal.model.Group;

/**
 * Interface for user controller, loads users by name.
 */
public interface UserController {

	/**
	 * Loads a User with all current components.
	 * @return The loaded user.
	 */
	public User getUser();

	/**
	 * Creates a new Group for the user and adds it to his groups.
	 * @param groupName The name of the new group.
	 * @return Reference to the inserted group.
	 * @throws ModifyUserException If the group already exists.
	 */
	public Group createGroup(final String groupName) throws ModifyUserException;

	/**
	 * Deletes a group from the user.
	 * @param groupName The name of the group to delete.
	 * @throws ModifyUserException If there is no group with the give name.
	 */
	public void deleteGroup(final String groupName) throws ModifyUserException;

	/**
	 * Adds a worker to the Group. The group has to be in the users groups list.
	 * @param worker The worker to add.
	 * @param group  The group to which the worker is added.
	 * @throws ModifyUserException If the group does not exist.
	 */
	public void addWorker(final Worker worker, final Group group) throws ModifyUserException;

	/**
	 * Removes a worker from the specified group. The group has to be in the users groups list.
	 * @param worker The worker to remove.
	 * @param group The group, where the worker is removed from.
	 * @throws ModifyUserException If the group does not exist or the user is not in the group.
	 */
	public void removeWorker(final Worker worker, final Group group) throws ModifyUserException;

	/**
	 * Returns the Group with the given name.
	 * @param groupName Name of the group to retrieve.
	 * @return The group to return.
	 * @throws ModifyUserException If the user has no group with the given name.
	 */
	public Group getGroup(final String groupName) throws ModifyUserException;
}
