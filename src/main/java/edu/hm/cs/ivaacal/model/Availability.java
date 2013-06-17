package edu.hm.cs.ivaacal.model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Christoph
 * Date: 05.06.13
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
public class Availability {

    private final boolean busy;

    private final Date date;

    private final String title;

    private final String location;

    public Availability(boolean busy, Date date, String title, String location){
        this.busy = busy;
        this.date = date;
        this.title = title;
        this.location = location;
    }


    public boolean isBusy() {
        return busy;
    }


    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }
}
