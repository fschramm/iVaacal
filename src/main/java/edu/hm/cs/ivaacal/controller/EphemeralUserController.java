package edu.hm.cs.ivaacal.controller;

import edu.hm.cs.ivaacal.exception.ModifyUserException;
import edu.hm.cs.ivaacal.model.*;
import edu.hm.cs.ivaacal.model.transport.UserTO;
import edu.hm.cs.ivaacal.util.ModelConverter;
import org.apache.log4j.Logger;

/**
 * User Controller without persistence.
 */
public class EphemeralUserController implements UserController {

	/**
	 * The logger for this class.
	 */
	private final static Logger LOGGER = Logger.getLogger(EphemeralUserController.class);

    /**
     * The model converter used.
     */
    private final ModelConverter modelConverter;

    /**
     * The Availability Controller used.
     */
    private final IAvailableController availableController = AvailableControllerImpl.getInstance();

    /**
     * The user attached to this controller.
     */
	private final User user;

    /**
     * The company used in this controller.
     */
	private final Company company;

    public EphemeralUserController() {
        this.user = new User();
        this.user.setName("guest user");
        this.company = JsonCompany.getInstance();
        this.modelConverter = new ModelConverter(this.company);
    }

	public EphemeralUserController(final String userName) {
		this.user = new User();
	    this.user.setName(userName);
		this.company = JsonCompany.getInstance();
        this.modelConverter = new ModelConverter(this.company);
	}

	public EphemeralUserController(final User user) {
		this.user = user;
		this.company = JsonCompany.getInstance();
        this.modelConverter = new ModelConverter(this.company);
	}

	public User getUser() {
		return this.user;
	}


	@Override
	public UserTO getUserTO() {
        updateGroupOpening();
		return modelConverter.covertUserToTO(this.user);
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
		if (!company.getWorkerMap().containsKey(workerID)) {
			throw new ModifyUserException("Could not add worker: Worker not found.");
		}

		Group group = user.getGroupMap().get(groupName);
		Worker worker = company.getWorkerMap().get(workerID);

		group.getWorkerMap().put(workerID, worker);

	}

	@Override
	public void removeWorker(String workerID, String groupName) throws ModifyUserException {
		Group group = null;
		if (!user.getGroupMap().containsKey(groupName)) {
			throw new ModifyUserException("Could not remove user: Group not found.");
		}
        group = user.getGroupMap().get(groupName);
		if (!group.getWorkerMap().containsKey(workerID)){
			throw new ModifyUserException("Could not remove user: User not in group.");
		}
		group.getWorkerMap().remove(workerID);

	}

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public void setVisible(final String groupName, final boolean visible)  throws ModifyUserException{
        Group group = null;
        if (!user.getGroupMap().containsKey(groupName)) {
            throw new ModifyUserException("Could set visibility: Group not found.");
        }
        group = user.getGroupMap().get(groupName);
        group.setVisible(visible);
    }

    /**
     * Updates the opening dates for all groups.
     */
    private void updateGroupOpening() {
        for (Group group: this.user.getGroupMap().values()) {
            group.setNextPossibleDate(availableController.getNexGroupOpening(group.getCalendarEmails()));
        }
    }

}
