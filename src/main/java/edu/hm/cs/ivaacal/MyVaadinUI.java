package edu.hm.cs.ivaacal;

import com.vaadin.annotations.Theme;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import edu.hm.cs.ivaacal.controller.EphemeralUserController;
import edu.hm.cs.ivaacal.controller.PersistentUserController;
import edu.hm.cs.ivaacal.dataSource.GooglePlusSource;
import edu.hm.cs.ivaacal.exception.DataSourceException;
import edu.hm.cs.ivaacal.exception.ModifyUserException;
import edu.hm.cs.ivaacal.model.Group;
import edu.hm.cs.ivaacal.model.User;
import edu.hm.cs.ivaacal.model.Worker;
import edu.hm.cs.ivaacal.model.transport.GroupTO;
import edu.hm.cs.ivaacal.model.transport.UserTO;
import fi.jasoft.dragdroplayouts.DDAccordion;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultAccordionDropHandler;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;


/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@Theme("ivaacal")
public class MyVaadinUI extends UI {

    private static final Logger LOGGER = Logger.getLogger(MyVaadinUI.class);

    /**
     * The Configuration of iVaaCal.
     */
    private static Configuration config = IVaaCalConfiguration.getConfiguration();


    // TODO: *************WORKER*************
    private Component generateWorker(Worker worker) {

        GridLayout grid = new GridLayout();
        grid.addStyleName("worker");
        grid.setRows(2);
        grid.setColumns(2);

        // Fill in Image
        grid.addComponent(new Image(null, new ExternalResource(worker.getImageURL())), 0, 0);

        // Fill in name
        grid.addComponent(new Label(worker.getName(), ContentMode.HTML), 1, 0);

        // Fill in Availability
        long availableMinutes = (worker.getAvailabilityChangeDate().getTime() - new Date().getTime())/60000;
        long availableHours = availableMinutes/60;
        availableMinutes %= 60;
        Label availability;
        if(worker.isAvailable() != null && worker.isAvailable()) {
            availability =  new Label("available for:<br />" + (availableHours == 0l? "": availableHours + " hours, ") + availableMinutes + " minutes", ContentMode.HTML);
            availability.addStyleName("available");
        }
        else {
            availability =  new Label("available in:<br />" + (availableHours == 0l? "": availableHours + " hours, ") + availableMinutes + " minutes", ContentMode.HTML);
            availability.addStyleName("unavailable");
        }
        grid.addComponent(availability, 0, 1, 1, 1);

        // TODO: add function -> drag and drop
        DragAndDropWrapper draggableWorkerWrapper = new DragAndDropWrapper(grid);
        draggableWorkerWrapper.setDragStartMode(DragAndDropWrapper.DragStartMode.WRAPPER);

        return draggableWorkerWrapper;
    }


    // TODO: *************LOGIN*************
    private Component generateLoginField() {

        HorizontalLayout login = new HorizontalLayout();
        login.addStyleName("login");
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        username.setInputPrompt("Username");
        password.setInputPrompt("Password");
        login.addComponent(username);
        login.addComponent(password);
        login.addComponent(new Button("Login"));

        // TODO: build layout -> logged in fields
        // TODO: add function -> login
        // TODO: add function -> logout
        return login;
    }

    private Component generateHeader() {

        // The header Component
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");

        // Add Login Fields
        Component login = generateLoginField();
        header.addComponent(login);

        // Add Headline
        Label title = new Label("iVaaCal");
        title.addStyleName("h2");
        header.addComponent(title);

        header.setComponentAlignment(title, Alignment.MIDDLE_RIGHT);
        header.setComponentAlignment(login, Alignment.MIDDLE_LEFT);

        return header;
    }

    private Component generateGroup(GroupTO group){

        // The group component
        VerticalLayout groupLayout = new VerticalLayout();
        groupLayout.setMargin(true);

        // The name of the group
        Panel panel = new Panel(group.getName());
        groupLayout.addComponent(panel);

        // The members of the group
        HorizontalLayout workersLayout = new HorizontalLayout();
        for (Worker member : group.getWorkers()) {
            workersLayout.addComponent(generateWorker(member));
        }
        panel.setContent(workersLayout);

        // TODO: add function -> create group
        // TODO: add function -> delete group
        // TODO: add function -> add worker per drag and drop
        // TODO: add function -> delete worker

        return groupLayout;
    }

    private Component generateGroups(UserTO user) {

        // The groups component
        VerticalLayout groupsLayout = new VerticalLayout();

        // Generate an add groups one by one
        for (GroupTO group : user.getGroups()) {
            groupsLayout.addComponent(generateGroup(group));
            groupsLayout.addComponent(generateGroup(group));
            groupsLayout.addComponent(generateGroup(group));
        }

        return groupsLayout;

    }

    @Override
    protected void init(VaadinRequest request) {

        final VerticalLayout root = new VerticalLayout();
        setContent(root);

        // generate Header area
        root.addComponent(generateHeader());

        //generate group area
        try {
            root.addComponent(generateGroups(new PersistentUserController("Sebastian Stumpf").getUserTO()));
        } catch (DataSourceException e) {
			e.printStackTrace();
		}

		// TODO: *************SEARCH*************
        // TODO: build layout -> search fields
        // TODO: add function -> search
        // TODO: build layout -> show results


        // testarea

    }

}
