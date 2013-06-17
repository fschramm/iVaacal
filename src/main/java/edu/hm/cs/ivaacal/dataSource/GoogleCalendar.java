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
 * User: Christoph
 * Date: 05.06.13
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class GoogleCalendar implements IGoogleCalendar {

    /**
     * The logger for this class.
     */
    private final static Logger LOGGER = Logger.getLogger(GoogleCalendar.class);
    /**
     * The Service to request data from GoogleCalendar
     */
    private final CalendarService client;

    /**
     * Token to authorize on GoogleCalendar
     */
    private String token;


    public GoogleCalendar(){
        client = new CalendarService("iVaaCal");
        try {
            client.setUserCredentials("ivaacal.javarockstars@gmail.com", "schrumpfer");
        } catch (AuthenticationException e) {
            LOGGER.error(e);
        }
        GoogleAuthTokenFactory.UserToken userToken = (GoogleAuthTokenFactory.UserToken) client.getAuthTokenFactory().getAuthToken();
        token =  userToken.getValue();
    }

    @Override
    public Availability getAvailable(String email) {
        Availability actAv = getDailySchedule(email).get(0);
        Date left = new Date(actAv.getDate().getTime() - System.currentTimeMillis());
        left.setHours(left.getHours()-1);
        return new Availability(actAv.isBusy(), left, actAv.getTitle(),actAv.getLocation());
    }


    @Override
    public Date getNexGroupOpening(String[] email) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private List<Availability> getDailySchedule(String email){
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
        List<Availability> availabilities = new ArrayList<Availability>();
        for(CalendarEventEntry eventEntry : resultEventFeed.getEntries()){
            When eventTime = eventEntry.getTimes().get(0);
            Date eventStartDate = new Date(eventTime.getStartTime().getValue());
            Date eventEndDate = new Date(eventTime.getEndTime().getValue());
            if(eventStartDate.before(actTime) && eventEndDate.after(actTime)){
                actEvent = eventEntry;
            }
            boolean busy = actEvent.getTransparency().getValue().equals("http://schemas.google.com/g/2005#event.opaque");
            Date endtime =    new Date(actEvent.getTimes().get(0).getEndTime().getValue());
            String location =   actEvent.getLocations().get(0).getValueString();
            String title = actEvent.getTitle().getPlainText();
            availabilities.add(new Availability(busy,endtime,title,location));
        }
        return availabilities;
    }
}
