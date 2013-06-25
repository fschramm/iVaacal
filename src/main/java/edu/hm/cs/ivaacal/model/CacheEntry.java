package edu.hm.cs.ivaacal.model;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Christoph Waldleitner
 */
public class CacheEntry {
    /**
     * List of next Availabilities of a user.
     */
    private final ArrayList<Availability> availability;
    /**
     * Time of last request for new Availabilities in millis.
     */
    private final long time;

    /**
     * Consturctor
     *
     * @param availability      list of next Availabilities
     * @param time              time of last request in millis.
     */
    public CacheEntry(ArrayList<Availability> availability, long time){
        this.availability = availability;
        this.time = time;
    }

    /**
     * Returns the list of next Availabilities
     *
     * @return      list of next Availabilities
     */
    public ArrayList<Availability> getAvailability() {
        return availability;
    }

    /**
     * Returns the time of last request.
     *
     * @return  time of last request in millis.
     */
    public long getTime() {
        return time;
    }
}
