package edu.hm.cs.ivaacal;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * iVaaCal configuration.
 */
public class IVaaCalConfiguration {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = Logger.getLogger(IVaaCalConfiguration.class);

	/**
	 * The configuration.
	 */
	private static Configuration configuration = null;

	/**
	 * Initialization for the configuration with xml data.
	 */
	static {
		try {
			configuration = new XMLConfiguration("iVaaCalConfig.xml");
		} catch (ConfigurationException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Private Constructor to avoid creation. The Configuration is for static use.
	 */
	private IVaaCalConfiguration() {}

	/**
	 * Returns the configuration for the iVaaCal application.
	 * @return The current configuration.
	 */
	public static Configuration getConfiguration() {
		return configuration;
	}
}
