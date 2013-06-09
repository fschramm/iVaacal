package edu.hm.cs.ivaacal.controller;

import edu.hm.cs.ivaacal.dataSource.UserJsonFilePersistence;
import edu.hm.cs.ivaacal.dataSource.UserPersistence;
import edu.hm.cs.ivaacal.exception.DataSourceException;
import edu.hm.cs.ivaacal.exception.ModifyUserException;
import org.apache.log4j.Logger;

/**
 * User controller with persistent user-data.
 */
public class PersistentUserController extends EphemeralUserController {

	/**
	 * The logger for this class.
	 */
	private final static Logger LOGGER = Logger.getLogger(PersistentUserController.class);

	private UserPersistence userPersistence = new UserJsonFilePersistence();

	public PersistentUserController(final String userName) throws DataSourceException {
		super(new UserJsonFilePersistence().loadUser(userName));
	}

	@Override
	public void createGroup(String groupName) throws ModifyUserException {
		super.createGroup(groupName);
		saveUser();
	}

	@Override
	public void deleteGroup(String groupName) throws ModifyUserException {
		super.deleteGroup(groupName);
		saveUser();
	}

	@Override
	public void addWorker(String workerID, String groupName) throws ModifyUserException {
		super.addWorker(workerID, groupName);
		saveUser();
	}

	@Override
	public void removeWorker(String workerID, String groupName) throws ModifyUserException {
		super.removeWorker(workerID, groupName);
		saveUser();
	}

    @Override
    public void setVisible(String groupName, boolean visible)  throws ModifyUserException{
        super.setVisible(groupName, visible);
        saveUser();
    }

    private void saveUser() throws ModifyUserException {
		try {
			userPersistence.saveUser(this.getUser());
		} catch (DataSourceException e) {
			throw new ModifyUserException("Could not persistently modify user: " + e.getMessage());
		}
	}

    @Override
    public boolean isLoggedIn() {
        return true;
    }

}
