package edu.hm.cs.ivaacal.test;

import edu.hm.cs.ivaacal.dataSource.GoogleCalendar;
import edu.hm.cs.ivaacal.dataSource.IGoogleCalendar;
import edu.hm.cs.ivaacal.model.Availability;

/**
 * Created with IntelliJ IDEA.
 * User: Christoph
 * Date: 16.06.13
 * Time: 21:20
 * To change this template use File | Settings | File Templates.
 */
public class GoogleCalendarTest {

    public static void main(String[] args) {
        IGoogleCalendar gc = new GoogleCalendar();
        Availability av = gc.getAvailable("mohamed.abergna.1@gmail.com");
        System.out.println(av.getTitle());
        System.out.println("Busy: " + av.isBusy());
        System.out.println(av.getEndDate());
    }
}
