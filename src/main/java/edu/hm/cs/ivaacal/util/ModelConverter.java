package edu.hm.cs.ivaacal.util;

import edu.hm.cs.ivaacal.model.*;
import edu.hm.cs.ivaacal.model.persistence.WorkerPO;
import edu.hm.cs.ivaacal.model.transport.GroupTO;
import edu.hm.cs.ivaacal.model.transport.UserTO;
import edu.hm.cs.ivaacal.model.transport.WorkerTO;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts Model object to Transport objects.
 */
public class ModelConverter {

	/**
	 * The logger for this class.
	 */
	private final static Logger LOGGER = Logger.getLogger(ModelConverter.class);

    /**
     * The company used to get additional worker information.
     */
    private Company company;

    public ModelConverter(final Company company) {
        this.company = company;
    }

	/**
	 * Converts an user to a transfer object. Adds the "all" group.
	 * @param user The user to convert.
	 * @return The user transport object.
	 */
	public UserTO covertUserToTO(final User user) {

		UserTO userTO = new UserTO();

		userTO.setName(user.getName());

		// Create worker conversion map, to avoid creating duplicated workerTOs.
		Map<Worker, WorkerTO> workerConversionMap = new HashMap<>();
		for(Worker worker: company.getWorkerMap().values()) {
		   workerConversionMap.put(worker, convertWorkerToTO(worker));
		}

		// Create groupTos.
		for(Group group: user.getGroupMap().values()) {
			GroupTO groupTO = new GroupTO();
			groupTO.setName(group.getName());
		    groupTO.setNextPossibleDate(group.getNextPossibleDate());
            groupTO.setVisible(group.isVisible());

			for(Worker worker: group.getWorkerMap().values()) {
				groupTO.getWorkers().add(workerConversionMap.get(worker));
			}

			userTO.getGroups().add(groupTO);
		}

		// Add the "all" group.
		GroupTO groupTO = new GroupTO();
		groupTO.setName("all");
		groupTO.getWorkers().addAll(workerConversionMap.values());
		userTO.getGroups().add(groupTO);

		return userTO;
	}

	/**
	 * Converts a worker to a worker transfer object.
	 * @param worker The worker to convert.
	 * @return The transfer object.
	 */
	private WorkerTO convertWorkerToTO(final Worker worker) {
		WorkerTO workerTO = new WorkerTO();

		workerTO.setAvailabilityChangeDate(worker.getAvailabilityChangeDate());
		workerTO.setAvailable(worker.isAvailable());
		workerTO.setCalendarID(worker.getCalendarID());
		workerTO.setGooglePlusID(worker.getGooglePlusID());
		workerTO.setImageURL(worker.getImageURL());
		workerTO.setName(worker.getName());

		return workerTO;

	}

	/**
	 * Converts a worker to a worker persistence object.
	 * @param worker The worker to convert.
	 * @return The persistence object.
	 */
	public WorkerPO convertWorkerToPO(final Worker worker) {
		WorkerPO workerPO = new WorkerPO();

		workerPO.setGooglePlusID(worker.getGooglePlusID());

		return workerPO;
	}


}
