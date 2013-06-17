package edu.hm.cs.ivaacal.dataSource;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusRequestInitializer;
import com.google.api.services.plus.model.Person;
import edu.hm.cs.ivaacal.IVaaCalConfiguration;
import edu.hm.cs.ivaacal.exception.DataSourceException;
import edu.hm.cs.ivaacal.model.Worker;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Data Access Object for google+ data.
 */
public class GooglePlusSource {

	/**
	 * The logger for this class.
	 */
	private final static Logger LOGGER = Logger.getLogger(GooglePlusSource.class);

	/**
	 * The Configuration of iVaaCal.
	 */
	private static Configuration config = IVaaCalConfiguration.getConfiguration();

	/**
	 * The http transport for API connections.
	 */
	private HttpTransport httpTransport = new NetHttpTransport();

	/**
	 * The Json Factory for conversion of responses.
	 */
	private JsonFactory jsonFactory = new JacksonFactory();

	/**
	 * The Google+ API connector.
	 */
	private Plus plus = new Plus.Builder(httpTransport, jsonFactory, null).setApplicationName(config.getString("GoogleAPIApplicationName"))
			.setGoogleClientRequestInitializer(new PlusRequestInitializer(config.getString("GoogleAPIKey"))).build();

	/**
	 * Load a worker from google+ by id.
	 * @param googlePlusId The google+ id of the worker.
	 * @return The loaded worker.
	 */
	public Worker loadWorker(final String googlePlusId) throws DataSourceException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting Google+ User with ID: " + googlePlusId);
		}
		Worker worker = new Worker();
		try {
			Person person = plus.people().get(googlePlusId).execute();
			worker.setName(person.getDisplayName());
			worker.setImageURL(person.getImage().getUrl());
			worker.setGooglePlusID(person.getId());
		} catch (IOException e) {
			LOGGER.error(e);
			throw new DataSourceException("Could not load worker. " + e.getMessage());
		}

		return worker;
	}

}
