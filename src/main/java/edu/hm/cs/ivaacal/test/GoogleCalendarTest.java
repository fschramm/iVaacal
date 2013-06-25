package edu.hm.cs.ivaacal.test;

import edu.hm.cs.ivaacal.controller.AvailableControllerImpl;
import edu.hm.cs.ivaacal.controller.IAvailableController;
import edu.hm.cs.ivaacal.dataSource.GoogleCalendar;
import edu.hm.cs.ivaacal.model.Availability;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Christoph
 * Date: 16.06.13
 * Time: 21:20
 * To change this template use File | Settings | File Templates.
 */
public class GoogleCalendarTest {

    public static void main(String[] args) {
        IAvailableController gc = new AvailableControllerImpl();
        String[] emails = {"mohamed.abergna.1@gmail.com", "steffi.ivaacal@gmail.com"};
        System.out.println();
        Availability av = gc.getAvailable(emails[0]);
        System.out.println(av.getTitle());
        System.out.println("Busy: " + av.isBusy());
        System.out.println(av.getStartDate() + "  -  "+av.getEndDate());

        Date nextFreeTime = gc.getNexGroupOpening(emails);
        System.out.println(nextFreeTime);
    }
}
