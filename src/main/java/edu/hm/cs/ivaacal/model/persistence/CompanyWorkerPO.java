package edu.hm.cs.ivaacal.model.persistence;

/**
 * Representation of a worker used in a company.
 * Extends the worker by a mapping to the calendar email.
 */
public class CompanyWorkerPO extends WorkerPO{

    private String calendarEmail;

    public String getCalendarEmail() {
        return calendarEmail;
    }

    public void setCalendarEmail(String calendarEmail) {
        this.calendarEmail = calendarEmail;
    }

}
