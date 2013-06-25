package edu.hm.cs.ivaacal.controller;

import edu.hm.cs.ivaacal.dataSource.GoogleCalendar;
import edu.hm.cs.ivaacal.dataSource.GoogleMaps;
import edu.hm.cs.ivaacal.model.Availability;
import edu.hm.cs.ivaacal.model.CacheEntry;
import edu.hm.cs.ivaacal.model.JsonCompany;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Chrisotph Waldleitner
 */
public class AvailableControllerImpl implements IAvailableController {

    /**
     * Max sec until the list will be refreshed.
     */
    private static final int CACHE_MAX_TIME_SEC = 10;

    /**
     * Max millis between two events for drive back to company.
     */
    private static final int MAX_TIME_BETWEEN_EVENTS = 1000 * 60 * 30;

    /**
     * Min time in millis for the next group meeting.
     */
    private static final int MIN_TIME_FOR_GROUP_MEETING = 1000 * 60 * 60;
    /**
     * The logger for this class.
     */
    private final static Logger LOGGER = Logger.getLogger(AvailableControllerImpl.class);

    /**
     * Adapter to google calendar api.
     */
    private final GoogleCalendar calendar = new GoogleCalendar();

    /**
     * Location of the company.
     */
    private final String location = JsonCompany.getInstance().getLocation();

    /**
     * Instance of the GoogleMaps for duration requests.
     */
    private final GoogleMaps maps = new GoogleMaps(location);

    /**
     * Cached Availabilities for each user.
     */
    private Map<String, CacheEntry> cache = new HashMap<String, CacheEntry>();

    /**
     * Comparator for Availability based on their start Date.
     */
    private final Comparator<Availability> comparator;

    /**
     * Constructor
     */
    public AvailableControllerImpl() {

        /**
         * Initialisation of the comparator.
         */
        this.comparator = new Comparator<Availability>() {
            @Override
            public int compare(Availability o1, Availability o2) {
                if (o1.getStartDate().before(o2.getStartDate())) {
                    return -1;
                } else if (o1.getStartDate().after(o2.getStartDate())) {
                    return 1;
                }
                return 0;
            }
        };
    }

    @Override
    public Availability getAvailable(String email) {
        return getActuellAvailability(email, new Date(System.currentTimeMillis()));
    }

    @Override
    public Date getNexGroupOpening(String[] email) {
        Date actTime = new Date(System.currentTimeMillis());
        Date startDate = null;
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String anEmail : email) {
                Availability actAvailability = getActuellAvailability(anEmail, actTime);
                if (actAvailability.isBusy() || (actAvailability.getStartDate().after(actTime)&&(actAvailability.getStartDate().getTime()-actTime.getTime())<MIN_TIME_FOR_GROUP_MEETING)) {
                    actTime = new Date(actAvailability.getEndDate().getTime());
                    changed = true;
                    break;
                }
            }
        }
        return actTime;
    }

    /**
     * Returns the actual Availability for a user on a defined time.
     *
     * @param email     email of the user
     * @param actTime   The time to search on
     * @return          actuell Availability for the given time
     */
    private Availability getActuellAvailability(String email, Date actTime){
        ArrayList<Availability> availabilities = getAvailability(email);
        for (int i = 0; i < availabilities.size(); i++) {
            Availability actEvent = availabilities.get(i);
            if (actEvent.getStartDate().before(actTime) && actEvent.getEndDate().after(actTime)) {
                Date endDate = actEvent.getEndDate();
                for (int j = i + 1; j < availabilities.size(); j++) {
                    Availability nextEvent = availabilities.get(j);
                    if (actEvent.isBusy() != nextEvent.isBusy() || (nextEvent.getStartDate().getTime() - endDate.getTime()) > MAX_TIME_BETWEEN_EVENTS) {
                        return new Availability(actEvent.isBusy(), actEvent.getStartDate(), endDate, actEvent.getTitle(), actEvent.getLocation());
                    }
                    endDate = nextEvent.getEndDate();
                }
            } else if (actEvent.getStartDate().after(actTime)) {
                if (actEvent.isBusy()) {
                    return new Availability(false, new Date(System.currentTimeMillis()), actEvent.getStartDate(), "At Work", location);
                }
            }
        }
        return null;
    }

    /**
     * Returns the chached Availabilities for a user or requests a new list.
     *
     * @param email     email of the user
     * @return          list of next availabilities for a user
     */
    private ArrayList<Availability> getAvailability(String email) {
        long actTime = System.currentTimeMillis();

        if (cache.containsKey(email)) {
            CacheEntry entry = cache.get(email);
            if (entry.getTime() > actTime - 1000 * 60 * CACHE_MAX_TIME_SEC) {
                return entry.getAvailability();
            }
        }

        ArrayList<Availability> availabilities = buildAvailabilities(email);
        cache.put(email, new CacheEntry(availabilities, System.currentTimeMillis()));

        return availabilities;
    }

    /**
     * Builds the availability list with "k l u k" controlling.
     *
     * @param email email-address of the user
     * @return list of all availibilities
     */
    private ArrayList<Availability> buildAvailabilities(String email) {
        ArrayList<Availability> calendarEntrys = calendar.getCalendarEntrys(email);
        Collections.sort(calendarEntrys, comparator);
        ArrayList<Availability> availabilities = new ArrayList<Availability>();

        if (calendarEntrys.size() > 0) {
            availabilities.add(getFinishingTime(null, calendarEntrys.get(0)));
            Availability lastEvent = null;
            for (int i = 0; i < calendarEntrys.size(); i++) {
                Availability actEvent = calendarEntrys.get(i);
                String location = actEvent.getLocation();
                if (!location.isEmpty()) {
                    int duration = maps.getDuration(location);
                    actEvent.setStartDate(new Date(actEvent.getStartDate().getTime() - duration * 1000));
                    actEvent.setEndDate(new Date(actEvent.getEndDate().getTime() + duration * 1000));
                }
                if (lastEvent == null) {

                } else if (lastEvent.getStartDate().getDate() != actEvent.getStartDate().getDate()) {
                    availabilities.add(getFinishingTime(lastEvent, actEvent));
                }
                availabilities.add(actEvent);
                lastEvent = actEvent;
            }
            availabilities.add(getFinishingTime(calendarEntrys.get(calendarEntrys.size() - 1), null));
        }
        return availabilities;
    }


    /**
     * Returns an Availability between two near Events.
     *
     * @param first  first event
     * @param second second event
     * @return Availability between the first and second event.
     */
    private Availability getTimeBetweenEvents(Availability first, Availability second) {
        Availability result = new Availability(true, new Date(first.getEndDate().getTime()), new Date(second.getStartDate().getTime()), "Still on location for next Event", first.getLocation());
        return result;
    }

    /**
     * Returns an Availability for finishing time.
     *
     * @param last  last event for this day or null if there is no last event
     * @param first first event for the next day or null if there is no next event
     * @return Availbility for the time between last/first event or between 20-8 o'clock.
     */
    private Availability getFinishingTime(Availability last, Availability first) {
        Date startDate;
        if (last == null) {
            startDate = new Date(first.getStartDate().getTime());
            startDate.setHours(0);
            startDate.setMinutes(0);
            startDate.setSeconds(0);
        } else {
            startDate = new Date(last.getEndDate().getTime());
            if (startDate.getDate() == last.getStartDate().getDate() && startDate.getHours() < 20) {
                startDate.setHours(20);
                startDate.setMinutes(0);
                startDate.setSeconds(0);
            }
        }

        Date endDate;
        if (first == null) {
            endDate = new Date(last.getEndDate().getTime());
            endDate.setHours(23);
            endDate.setMinutes(59);
            endDate.setSeconds(59);
        } else {
            endDate = new Date(first.getStartDate().getTime());
            if (endDate.getHours() > 8) {
                endDate.setHours(8);
                endDate.setMinutes(0);
                endDate.setSeconds(0);
            }
        }
        return new Availability(true, startDate, endDate, "Out of working time", "");
    }

}
