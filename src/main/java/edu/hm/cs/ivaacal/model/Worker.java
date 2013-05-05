package edu.hm.cs.ivaacal.model;

import java.util.Date;

/**
 * Representation of a Worker.
 */
public class Worker {

	/**
	 * The name of the worker.
	 */
	private String name;

	/**
	   The Google+ Id of the worker.
	 */
	private String googlePlusID;

	/**
	 * The url to an image of the worker.
	 */
	private String imageURL;

	/**
	 * The Calendar ID of the worker.
	 */
	private String CalendarID;

	/**
	 * Marks if the worker is currently available.
	 */
	private Boolean isAvailable;

	/**
	 * Date of the next change of the worker.
	 */
	private Date availabilityChangeDate;

	/**
	 * Getter for name attribute.
	 * @return The name of the worker.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for google+ id.
	 * @return The google+ id of the worker.
	 */
	public String getGooglePlusID() {
		return googlePlusID;
	}

	public void setGooglePlusID(String googlePlusID) {
		this.googlePlusID = googlePlusID;
	}

	/**
	 * Getter for image URL.
	 * @return The image URL of the worker.
	 */
	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	/**
	 * Getter for the calendar ID.
	 * @return The calendar ID of the worker.
	 */
	public String getCalendarID() {
		return CalendarID;
	}

	public void setCalendarID(String calendarID) {
		CalendarID = calendarID;
	}

	/**
	 * Getter for availability status.
	 * @return The availability of the worker.
	 */
	public Boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(Boolean available) {
		isAvailable = available;
	}

	/**
	 * Getter for availability change date. The date, when the availability os the worker will change next.
	 * @return The availability change date of the worker.
	 */
	public Date getAvailabilityChangeDate() {
		return availabilityChangeDate;
	}

	public void setAvailabilityChangeDate(Date availabilityChangeDate) {
		this.availabilityChangeDate = availabilityChangeDate;
	}
}
