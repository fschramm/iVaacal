package edu.hm.cs.ivaacal.util;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;

/**
 * Created with IntelliJ IDEA.
 * @author: Basti
 * Date: 09.06.13
 * Time: 01:57
 * Accept Criterion that Accepts nothin.
 */
public class AcceptNothing implements AcceptCriterion {

    @Override
    public boolean isClientSideVerifiable() {
        return true;
    }

    @Override
    public void paint(final PaintTarget paintTarget) throws PaintException {
    }

    @Override
    public void paintResponse(final PaintTarget paintTarget) throws PaintException {
    }

    @Override
    public boolean accept(final DragAndDropEvent dragAndDropEvent) {
        return false;
    }
}
