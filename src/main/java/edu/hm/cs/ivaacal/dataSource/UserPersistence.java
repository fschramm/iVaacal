package edu.hm.cs.ivaacal.dataSource;

import edu.hm.cs.ivaacal.exception.DataSourceException;
import edu.hm.cs.ivaacal.model.User;

/**
 * User persistence Interface.
 */
public interface UserPersistence {

	/**
	 * Persist a user.
	 * @param user The user to save.
	 */
	public void saveUser(final User user) throws DataSourceException;

	/**
	 * Load a persisted user.
	 * @param userName The user name.
	 * @return The loaded user.
	 */
	public User loadUser(final String userName) throws DataSourceException;
}
