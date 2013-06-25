package edu.hm.cs.ivaacal.model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Christoph Waldleitner
 */
public class Availability {

    /**
     * Is the user busy on this Event?
     */
    private boolean busy;
    /**
     * Start date of the Event.
     */
    private Date startDate;
    /**
     * End date of the Event.
     */
    private Date endDate;
    /**
     * Title of the Event.
     */
    private String title;
    /**
     * Location for this Event.
     */
    private String location;

    /**
     * Constructor
     *
     * @param busy
     * @param startDate
     * @param endDate
     * @param title
     * @param location
     */
    public Availability(boolean busy, Date startDate, Date endDate, String title, String location){
        this.busy = busy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.location = location;
    }

    /**
     * Is the user on this Event busy?
     * @return      true = User is busy on this Event.
     *              false = User is not busy on this Event.
     */
    public boolean isBusy() {
        return busy;
    }

    /**
     * Returns title of this Event.
     *
     * @return      Title of the Event.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns location of this Event.
     *
     * @return      Location of the Event.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the start date of the Event.
     *
     * @return      Start date of the Event.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Returns the end date of the Event.
     *
     * @return      End date of the Event.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Set new start Date for this Event.
     *
     * @param startDate     new start Date.
     */
    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }
    /**
     * Set new end Date for this Event.
     *
     * @param endDate     new end Date.
     */
    public void setEndDate(Date endDate){
        this.endDate = endDate;
    }
}
