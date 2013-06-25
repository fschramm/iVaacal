package edu.hm.cs.ivaacal.controller;

import edu.hm.cs.ivaacal.model.Availability;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Christoph Waldleitner
 */
public interface IAvailableController {

    /**
     *  Returns the actual availability of the user
     *
     * @param email     email of the user
     * @return          Availability of the user
     */
    public Availability getAvailable (String email);

    /**
     * Returns the next opening for a group.
     *
     * @param email     emails of each user in the group
     * @return          start Date of the next possible opening
     */
    public Date getNexGroupOpening (String[] email);
}
