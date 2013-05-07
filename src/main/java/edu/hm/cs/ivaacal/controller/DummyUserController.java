package edu.hm.cs.ivaacal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.hm.cs.ivaacal.controller.dataSource.GooglePlusSource;
import edu.hm.cs.ivaacal.exception.DataSourceException;
import edu.hm.cs.ivaacal.exception.ModifyUserException;
import edu.hm.cs.ivaacal.model.Group;
import edu.hm.cs.ivaacal.model.User;
import edu.hm.cs.ivaacal.model.Worker;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Dummy implementation. returns dummy values.
 */
public class DummyUserController implements UserController{

	/**
	 * The logger for this class.
	 */
	private final static Logger LOGGER = Logger.getLogger(DummyUserController.class);

	/**
	 * Name of the user, that this controller is assigned to.
	 */
	final String userName;

	final User user;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final File userFile;

	private static final String FILE_EXTENSION = ".json";

	private static final String PATH = "tmp" + File.separator + "dummyUsers";



	public DummyUserController(final String userName) throws ModifyUserException {
		this.userName = userName;
		User userTemp = null;
		userFile = new File(PATH, userName + FILE_EXTENSION);
		boolean isNewUser = false;
		if (userFile.exists()) {
			try {
				userTemp = objectMapper.readValue(new FileInputStream(this.userFile), User.class);
			} catch (IOException e) {
				throw new ModifyUserException("Could not load user.");
			}
		} else {
			isNewUser = true;
			userFile.getParentFile().mkdirs();
			userTemp = new User();
			userTemp.setName(userName);
			userTemp.getGroups().add(getAllGroup());
		}

		this.user = userTemp;
		if (isNewUser) {
			this.saveUser();
		}
	}


	@Override
	public User getUser() {
		return this.user;
	}

	public void saveUser() throws ModifyUserException {
		try {
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			objectMapper.writeValue(new FileOutputStream(this.userFile), this.user);
		} catch (IOException e) {
			throw new ModifyUserException(e);
		}
	}

	@Override
	public Group createGroup(String groupName) throws ModifyUserException {

		for (Group group : user.getGroups()) {
			if (group.getName().equals(groupName)) {
				throw new ModifyUserException("Group with name already contained.");
			}
		}

		Group newGroup = new Group();
		newGroup.setName(groupName);

		user.getGroups().add(newGroup);
		saveUser();

		this.saveUser();
		return newGroup;
	}

	@Override
	public void deleteGroup(String groupName) throws ModifyUserException {
		user.getGroups().remove(getGroup(groupName));
	}

	@Override
	public void addWorker(Worker worker, Group group) throws ModifyUserException {
		if (!user.getGroups().contains(group)) {
			throw new ModifyUserException("Could not add user: Group not found.");
		}
		group.getWorkers().add(worker);
		this.saveUser();
	}

	@Override
	public void removeWorker(Worker worker, Group group) throws ModifyUserException {
		if (!user.getGroups().contains(group)) {
			throw new ModifyUserException("Could not remove user: Group not found.");
		}
		if (!group.getWorkers().contains(worker)){
			throw new ModifyUserException("Could not remove user: User not in group. (by reference)");
		}
		group.getWorkers().remove(worker);
		this.saveUser();
	}

	@Override
	public Group getGroup(String groupName) throws ModifyUserException {
		for (Group group : user.getGroups()) {
			if (group.getName().equals(groupName)) {
				this.saveUser();
				return group;
			}
		}
		// The group was not found.
		throw new ModifyUserException("Group not found.");
	}

	private Group getAllGroup() {
		GooglePlusSource googlePlusSource =  new GooglePlusSource();
		int numberOfWorkers = 15;

		String googlePlusID = "100000772955143706751";

		Random random = new Random();

		List<Worker> workers = new LinkedList<>();

		try {
			for (int i = 0 ; i < numberOfWorkers; ++i) {
				Worker worker = googlePlusSource.loadWorker(googlePlusID);
				worker.setName(worker.getName() + " " + i);
				worker.setAvailable(Boolean.valueOf(i % 2 == 0));
				worker.setAvailabilityChangeDate(new Date(System.currentTimeMillis() + random.nextInt(10000)));
				workers.add(worker);
			}
		} catch (DataSourceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		Group allGroup = new Group();
		allGroup.setName("all");
		allGroup.getWorkers().addAll(workers);
		allGroup.setNextPossibleDate(new Date(System.currentTimeMillis() + random.nextInt(20000)));

		return allGroup;
	}

	public static void main(String... args) throws DataSourceException, ModifyUserException {

		new DummyUserController("Hans").saveUser();
	}
}
