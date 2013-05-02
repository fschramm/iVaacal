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
	private Long googlePlusID;

	/**
	 * The url to an image of the worker.
	 */
	private String imageURL;

	/**
	 * The Calendar ID of the worker.
	 */
	private Long CalendarID;

	/**
	 * Marks if the worker is currently available.
	 */
	private Boolean isAvailable;

	/**
	 * Date of the next change of the worker.
	 */
	private Date availabilityChangeDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getGooglePlusID() {
		return googlePlusID;
	}

	public void setGooglePlusID(Long googlePlusID) {
		this.googlePlusID = googlePlusID;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public Long getCalendarID() {
		return CalendarID;
	}

	public void setCalendarID(Long calendarID) {
		CalendarID = calendarID;
	}

	public Boolean getAvailable() {
		return isAvailable;
	}

	public void setAvailable(Boolean available) {
		isAvailable = available;
	}

	public Date getAvailabilityChangeDate() {
		return availabilityChangeDate;
	}

	public void setAvailabilityChangeDate(Date availabilityChangeDate) {
		this.availabilityChangeDate = availabilityChangeDate;
	}
}
