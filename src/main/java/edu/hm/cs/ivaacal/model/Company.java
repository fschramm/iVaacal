package edu.hm.cs.ivaacal.model;

import edu.hm.cs.ivaacal.dataSource.GooglePlusSource;
import edu.hm.cs.ivaacal.exception.DataSourceException;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Company model object. Contains all workers from that company.
 */
public class Company {
	/**
	 * The logger for this class.
	 */
	private final static Logger LOGGER = Logger.getLogger(Company.class);

	private Map<String, Worker> workerMap = new HashMap<>();

	public static final Company JAVA_ROCKSTARS = new Company();

	private Company() {
		// TODO change dummy workers
		GooglePlusSource googlePlusSource =  new GooglePlusSource();

		Collection<String> googlePlusIDs = Arrays.asList(new String[]{"100000772955143706751", "102116084957024922058",
				"+HelloKitty", "103829002444236169384", "103919114469990731787"});

		Random random = new Random();

		try {
			for (String googlePlusID: googlePlusIDs) {
				Worker worker = googlePlusSource.loadWorker(googlePlusID);
				worker.setAvailable(Boolean.valueOf(worker.getName().length() % 2 == 0));
				worker.setAvailabilityChangeDate(new Date(System.currentTimeMillis() + random.nextInt(10000)));
				workerMap.put(worker.getGooglePlusID(), worker);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Added Worker: " + worker.getName() + " ID: " + worker.getGooglePlusID());
				}
			}
		} catch (DataSourceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

	}

	public  Map<String, Worker> getWorkerMap() {
		return workerMap;
	}


}
