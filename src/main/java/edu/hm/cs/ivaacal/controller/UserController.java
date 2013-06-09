package edu.hm.cs.ivaacal.controller;

import edu.hm.cs.ivaacal.exception.ModifyUserException;
import edu.hm.cs.ivaacal.model.User;
import edu.hm.cs.ivaacal.model.Worker;
import edu.hm.cs.ivaacal.model.Group;
import edu.hm.cs.ivaacal.model.transport.GroupTO;
import edu.hm.cs.ivaacal.model.transport.UserTO;

/**
 * Interface for user controller, loads users by name.
 */
public interface UserController {

	/**
	 * Loads a User with all current components.
	 * @return The loaded user.
	 */
	public UserTO getUserTO();

	/**
	 * Creates a new Group for the user and adds it to his groups.
	 * @param groupName The name of the new group.
	 * @throws ModifyUserException If the group already exists.
	 */
	public void createGroup(final String groupName) throws ModifyUserException;

	/**
	 * Deletes a group from the user.
	 * @param groupName The name of the group to delete.
	 * @throws ModifyUserException If there is no group with the give name.
	 */
	public void deleteGroup(final String groupName) throws ModifyUserException;

	/**
	 * Adds a worker to the Group. The group has to be in the users groups list.
	 * @param workerID The ID of the worker to add.
	 * @param groupName  The name of the group to which the worker is added.
	 * @throws ModifyUserException If the group does not exist.
	 */
	public void addWorker(final String workerID, final String groupName) throws ModifyUserException;

	/**
	 * Removes a worker from the specified group. The group has to be in the users groups list.
	 * @param workerID The ID of the worker to remove.
	 * @param groupName The name of the group, where the worker is removed from.
	 * @throws ModifyUserException If the group does not exist or the user is not in the group.
	 */
	public void removeWorker(final String workerID, final String groupName) throws ModifyUserException;

    /**
     * Returns true if the user is logged in.
     * @return true if the user is loggen in.
     */
    public boolean isLoggedIn();

    /**
     * Sets a group visible or invisible.
     * @param groupName The name of the group to change visibility.
     * @param visible If the group is visible or not.
     */
    void setVisible(final String groupName, final boolean visible) throws ModifyUserException;

}
