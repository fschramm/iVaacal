package edu.hm.cs.ivaacal.dataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.hm.cs.ivaacal.controller.EphemeralUserController;
import edu.hm.cs.ivaacal.exception.DataSourceException;
import edu.hm.cs.ivaacal.exception.ModifyUserException;
import edu.hm.cs.ivaacal.model.User;
import edu.hm.cs.ivaacal.model.Group;
import edu.hm.cs.ivaacal.model.Worker;
import edu.hm.cs.ivaacal.model.persistence.GroupPO;
import edu.hm.cs.ivaacal.model.persistence.UserPO;
import edu.hm.cs.ivaacal.model.persistence.WorkerPO;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Persists User data to Json files.
 */
public class UserJsonFilePersistence implements UserPersistence{

	/**
	 * The logger for this class.
	 */
	private final static Logger LOGGER = Logger.getLogger(UserJsonFilePersistence.class);

	private final static ObjectMapper objectMapper = new ObjectMapper();

	private static final String FILE_EXTENSION = ".json";

	private static final String PATH =  "appdata" + File.separator + "users";

	/**
	 * Save a user as Json in the filesystem.
	 * @param user The user to save.
	 */
	public void saveUser(final User user) throws DataSourceException {

		File userFile = new File(PATH, user.getName() + FILE_EXTENSION);
		if (!userFile.exists()) {
			userFile.getParentFile().mkdirs();
		}
		UserPO userPO = new UserPO();
		for (Group group: user.getGroupMap().values()) {
			GroupPO groupPO = new GroupPO();
			groupPO.setName(group.getName());

			for (Worker worker: group.getWorkerMap().values()) {
				WorkerPO workerPO = new WorkerPO();
				workerPO.setGooglePlusID(worker.getGooglePlusID());

				groupPO.getWorkers().add(workerPO);
			}

			userPO.getGroups().add(groupPO);
		}
		try {
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			objectMapper.writeValue(new FileOutputStream(userFile), userPO);
		} catch (IOException e) {
			throw new DataSourceException(e);
		}
	}

	/**
	 * Load a user from filesystem.
	 * @param userName The user name.
	 * @return The loaded user.
	 */
	public User loadUser(final String userName) throws DataSourceException {

		EphemeralUserController ephemeralUserController = new EphemeralUserController(userName);

		File userFile = new File(PATH, userName + FILE_EXTENSION);

		if (userFile.exists()) {
			try {
				UserPO userPO = objectMapper.readValue(new FileInputStream(userFile), UserPO.class);

				for (GroupPO groupPO : userPO.getGroups()) {
					ephemeralUserController.createGroup(groupPO.getName());

					for (WorkerPO workerPO : groupPO.getWorkers()) {
						ephemeralUserController.addWorker(workerPO.getGooglePlusID(), groupPO.getName());
					}
				}
			} catch (IOException e) {
				throw new DataSourceException("Could not load user.");
			} catch (ModifyUserException e) {
				throw new DataSourceException("Could not create restore user. Userfile may be corrupt. Reason: " + e.getMessage());
			}
		}

		return ephemeralUserController.getUser();
	}
}
