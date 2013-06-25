package edu.hm.cs.ivaacal.dataSource;

import com.google.gdata.client.GoogleAuthTokenFactory;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import edu.hm.cs.ivaacal.model.Availability;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Christoph Waldleitner
 */
public class GoogleCalendar {

    /**
     * The logger for this class.
     */
    private final static Logger LOGGER = Logger.getLogger(GoogleCalendar.class);
    /**
     * The Service to request data from GoogleCalendar
     */
    private final CalendarService client;


    /**
     * Constructor
     */
    public GoogleCalendar(){
        client = new CalendarService("iVaaCal");
        try {
            client.setUserCredentials("ivaacal.javarockstars@gmail.com", "schrumpfer");
        } catch (AuthenticationException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Returns the next entrys to a defined user.
     *
     * @param email         email of the user
     * @return              list of next Availabilities
     */
    public ArrayList<Availability> getCalendarEntrys(String email){
        Date actTime = new Date(System.currentTimeMillis());
        URL feedUrl = null;
        try {
            feedUrl = new URL("https://www.google.com/calendar/feeds/"+email+"/private/full");
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        CalendarQuery myQuery = new CalendarQuery(feedUrl);
        Date startTime = (Date)actTime.clone();
        startTime.setHours(0);
        startTime.setMinutes(0);
        startTime.setSeconds(0);

        myQuery.setMinimumStartTime(new DateTime(startTime.getTime()));
        CalendarEventFeed resultEventFeed = null;
        try {
            resultEventFeed = client.getFeed(myQuery, CalendarEventFeed.class);
        } catch (IOException e) {
            LOGGER.error(e);
        } catch (ServiceException e) {
            LOGGER.error(e);
        }

        CalendarEventEntry actEvent = null;
        ArrayList<Availability> availabilities = new ArrayList<Availability>();
        for(CalendarEventEntry eventEntry : resultEventFeed.getEntries()){
            When eventTime = eventEntry.getTimes().get(0);
            Date eventStartDate = new Date(eventTime.getStartTime().getValue());
            Date eventEndDate = new Date(eventTime.getEndTime().getValue());
            boolean busy = eventEntry.getTransparency().getValue().equals("http://schemas.google.com/g/2005#event.opaque");
            String location =   eventEntry.getLocations().get(0).getValueString();
            String title = eventEntry.getTitle().getPlainText();
            availabilities.add(new Availability(busy,eventStartDate,eventEndDate,title,location));
        }

        return availabilities;
    }
}
