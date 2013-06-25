package edu.hm.cs.ivaacal.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.cs.ivaacal.controller.AvailableControllerImpl;
import edu.hm.cs.ivaacal.controller.IAvailableController;
import edu.hm.cs.ivaacal.dataSource.GooglePlusSource;
import edu.hm.cs.ivaacal.exception.DataSourceException;
import edu.hm.cs.ivaacal.model.persistence.CompanyPO;
import edu.hm.cs.ivaacal.model.persistence.CompanyWorkerPO;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    private void loadWorkers(final Collection<CompanyWorkerPO> companyWorkerPOs) {

        GooglePlusSource googlePlusSource =  new GooglePlusSource();

        try {
            for (CompanyWorkerPO companyWorkerPO: companyWorkerPOs) {
                Worker worker = googlePlusSource.loadWorker(companyWorkerPO.getGooglePlusID());
                worker.setCalendarEmail(companyWorkerPO.getCalendarEmail());
                workerMap.put(worker.getGooglePlusID(), worker);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Added Worker: " + worker.getName() + " ID: " + worker.getGooglePlusID());
                }
            }
        } catch (DataSourceException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Updates the availability status for all workers.
     */
    private void updateWorkerAvailability() {
        IAvailableController availableController = AvailableControllerImpl.getInstance();
        for (Worker worker: workerMap.values()) {
            Availability availability =  availableController.getAvailable(worker.getCalendarEmail());
            worker.setAvailabilityChangeDate(availability.getEndDate());
            worker.setAvailable(!availability.isBusy());
        }
    }

	@Override
	public  Map<String, Worker> getWorkerMap() {
        updateWorkerAvailability();
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
