package edu.hm.cs.ivaacal.controller;

import edu.hm.cs.ivaacal.exception.ModifyUserException;
import edu.hm.cs.ivaacal.model.Company;
import edu.hm.cs.ivaacal.model.Group;
import edu.hm.cs.ivaacal.model.User;
import edu.hm.cs.ivaacal.model.Worker;
import edu.hm.cs.ivaacal.model.transport.UserTO;
import edu.hm.cs.ivaacal.util.ModelConverter;
import org.apache.log4j.Logger;

/**
 * User Controller, .
 */
public class EphemeralUserController implements UserController {

	/**
	 * The logger for this class.
	 */
	private final static Logger LOGGER = Logger.getLogger(EphemeralUserController.class);

	private final User user;

	public EphemeralUserController(final String userName) {
		this.user = new User();
	    this.user.setName(userName);
	}

	public EphemeralUserController(final User user) {
		this.user = user;
	}

	public User getUser() {
		return this.user;
	}


	@Override
	public UserTO getUserTO() {
		return ModelConverter.covertUserToTO(this.user);
	}

	@Override
	public void createGroup(String groupName) throws ModifyUserException {

		if (user.getGroupMap().containsKey(groupName)) {
			throw new ModifyUserException("Group with name already contained.");
		}

		Group newGroup = new Group();
		newGroup.setName(groupName);

		user.getGroupMap().put(groupName, newGroup);
	}

	@Override
	public void deleteGroup(String groupName) throws ModifyUserException {
		user.getGroupMap().remove(groupName);
	}

	@Override
	public void addWorker(String workerID, String groupName) throws ModifyUserException {

		if (!user.getGroupMap().containsKey(groupName)) {
			throw new ModifyUserException("Could not add worker: Group not found.");
		}
		if (!Company.JAVA_ROCKSTARS.getWorkerMap().containsKey(workerID)) {
			throw new ModifyUserException("Could not add worker: Worker not found.");
		}

		Group group = user.getGroupMap().get(groupName);
		Worker worker = Company.JAVA_ROCKSTARS.getWorkerMap().get(workerID);

		group.getWorkerMap().put(workerID, worker);

	}

	@Override
	public void removeWorker(String workerID, String groupName) throws ModifyUserException {
		Group group = null;
		if (!user.getGroupMap().containsKey(groupName)) {
			group = user.getGroupMap().get(groupName);
			throw new ModifyUserException("Could not remove user: Group not found.");
		}
		if (!group.getWorkerMap().containsKey(workerID)){
			throw new ModifyUserException("Could not remove user: User not in group.");
		}
		group.getWorkerMap().remove(workerID);

	}

}
