package edu.hm.cs.ivaacal.dataSource;

import edu.hm.cs.ivaacal.model.Availability;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Christoph
 * Date: 05.06.13
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
public interface IGoogleCalendar {

    public Availability getAvailable (String email);

    public Date getNexGroupOpening (String[] email);
}
