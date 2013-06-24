package edu.hm.cs.ivaacal.model;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.cs.ivaacal.dataSource.GooglePlusSource;
import edu.hm.cs.ivaacal.exception.DataSourceException;

import edu.hm.cs.ivaacal.model.persistence.CompanyPO;
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
	private static final Logger LOGGER = Logger.getLogger(JsonCompany.class);

    /**
     * The object mapper used to de-/serialize the company.
     */
	private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * The file extension that is used for company persistence files.
     */
    private static final String FILE_EXTENSION = ".json";

    /**
     * The path used for saving the persistence files.
     */
    private static final String PATH =  "appdata" + File.separator + "companies";

    /**
     * The default company used for the iVaaCal application.
     */
    private static final Company JAVA_ROCKSTARS;

    /**
     * All workers of this company, with their id as key.
     */
	private Map<String, Worker> workerMap = new HashMap<>();

    /**
     * The physical location of the company.
     */
    private String location;



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

		File companyFile = new File(PATH, companyName + FILE_EXTENSION);
		if (!companyFile.exists()) {
			throw new DataSourceException("Could not load company with name: " + companyName + " - File not found.");
		}
		CompanyPO companyPO;
		try {
            companyPO = objectMapper.readValue(companyFile, CompanyPO.class);
		} catch (IOException e) {
			throw new DataSourceException("Could not load company with name: " + companyName + " - File corrupt.");
		}

        location = companyPO.getLocation();

        loadWorkers(companyPO.getWorkers());

	}

    private void loadWorkers(final Collection<WorkerPO> workerPOs) {

        GooglePlusSource googlePlusSource =  new GooglePlusSource();
        Random random = new Random();

        try {
            for (WorkerPO workerPO: workerPOs) {
                Worker worker = googlePlusSource.loadWorker(workerPO.getGooglePlusID());
                // TODO read calendar from google
                worker.setAvailable(Boolean.valueOf(worker.getName().length() % 2 == 0));
                worker.setAvailabilityChangeDate(new Date(System.currentTimeMillis() + random.nextInt(10000)));
                workerMap.put(worker.getGooglePlusID(), worker);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Added Worker: " + worker.getName() + " ID: " + worker.getGooglePlusID());
                }
            }
        } catch (DataSourceException e) {
            LOGGER.error(e);
        }
    }

	@Override
	public  Map<String, Worker> getWorkerMap() {
		return workerMap;
	}

    @Override
    public String getLocation() {
        return location;
    }

    /**
     * Returns the default company instance.
     * @return The default company.
     */
    public static Company getInstance() {
        return JAVA_ROCKSTARS;
    }


}
