package edu.hm.cs.ivaacal.model;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.cs.ivaacal.exception.DataSourceException;

import edu.hm.cs.ivaacal.model.persistence.WorkerPO;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * JsonCompany model object. Contains all workers from that company.
 */
public class JsonCompany implements Company {
	/**
	 * The logger for this class.
	 */
	private final static Logger LOGGER = Logger.getLogger(JsonCompany.class);

	private final static ObjectMapper objectMapper = new ObjectMapper();

	private static final String FILE_EXTENSION = ".json";

	private static final String PATH =  "appdata" + File.separator + "companies";

	private Map<String, Worker> workerMap = new HashMap<>();

	public static final Company JAVA_ROCKSTARS;

	static {
		Company javaRockstarsTemp;
		try {
			javaRockstarsTemp = new JsonCompany("javaRockstars");
		} catch (DataSourceException e) {
			LOGGER.error(e);
			javaRockstarsTemp = null;
		}
		JAVA_ROCKSTARS = javaRockstarsTemp;
	}

	private JsonCompany(final String companyName) throws DataSourceException {

		//GooglePlusSource googlePlusSource =  new GooglePlusSource();

		File companyFile = new File(PATH, companyName + FILE_EXTENSION);
		if (!companyFile.exists()) {
			throw new DataSourceException("Could not load company with name: " + companyName + " - File not found.");
		}
		Collection<WorkerPO> companyWorkers = null;
		try {
			companyWorkers = objectMapper.readValue(companyFile, new TypeReference<Collection<WorkerPO>>(){});
		} catch (IOException e) {
			throw new DataSourceException("Could not load company with name: " + companyName + " - File corrupt.");
		}

		Random random = new Random();

		//try {
			for (WorkerPO workerPO: companyWorkers) {
				Worker worker = null;// googlePlusSource.loadWorker(workerPO.getGooglePlusID());
				// TODO read calendar from google
				worker.setAvailable(Boolean.valueOf(worker.getName().length() % 2 == 0));
				worker.setAvailabilityChangeDate(new Date(System.currentTimeMillis() + random.nextInt(10000)));
				workerMap.put(worker.getGooglePlusID(), worker);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Added Worker: " + worker.getName() + " ID: " + worker.getGooglePlusID());
				}
			}
		/*} catch (DataSourceException e) {
			LOGGER.error(e);
		}   */

	}

	@Override
	public  Map<String, Worker> getWorkerMap() {
		return workerMap;
	}


}
