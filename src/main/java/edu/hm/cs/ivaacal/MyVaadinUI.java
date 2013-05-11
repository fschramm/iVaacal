package edu.hm.cs.ivaacal;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import edu.hm.cs.ivaacal.controller.DummyUserController;
import edu.hm.cs.ivaacal.controller.dataSource.GooglePlusSource;
import edu.hm.cs.ivaacal.exception.DataSourceException;
import edu.hm.cs.ivaacal.exception.ModifyUserException;
import edu.hm.cs.ivaacal.model.Group;
import edu.hm.cs.ivaacal.model.User;
import edu.hm.cs.ivaacal.model.Worker;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private GridLayout generateWorker(Worker worker) {

        // TODO: build layout -> worker field
        GridLayout grid = new GridLayout();
        grid.addStyleName("worker");
        grid.setRows(2);
        grid.setColumns(2);

        // Fill in Image
        grid.addComponent(new Image(null, new ExternalResource(worker.getImageURL())), 0, 0);

        // Fill in name
        grid.addComponent(new Label(worker.getName(), ContentMode.HTML), 1, 0);

        // Fill in Availability
        // dummy start
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Label availability = new Label("available until:<br />" + dateFormat.format(new Date()), ContentMode.HTML);
        availability.addStyleName("available");
        // dummy end
        /*if(worker.isAvailable()) {
            availability =  new Label("available until:<br />"+worker.getAvailabilityChangeDate(), ContentMode.HTML);
            availability.addStyleName("available");
        }
        else {
            availability =  new Label("available again at:<br />"+worker.getAvailabilityChangeDate(), ContentMode.HTML);
            availability.addStyleName("unavailable");
        }*/
        grid.addComponent(availability, 0, 1, 1, 1);

        // TODO: add function -> drag and drop
        // TODO: add function -> initialize from user / default user object

        return grid;
    }


    // TODO: *************LOGIN*************
    private HorizontalLayout generateLoginField() {
        // TODO: build layout -> logged out fields
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

    private HorizontalLayout generateHeader() {

        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        HorizontalLayout login = generateLoginField();
        header.addComponent(login);
        Label title = new Label("iVaaCal");
        title.addStyleName("h2");
        header.addComponent(title);
        header.setComponentAlignment(title, Alignment.MIDDLE_RIGHT);
        header.setComponentAlignment(login, Alignment.MIDDLE_LEFT);

        return header;
    }

    // TODO: *************GROUPS*************
    private Accordion generateGroups(User user) throws ModifyUserException {

        // TODO: build layout -> groups accordion
        Accordion groups = new Accordion();
        groups.setHeight(100.0f, Unit.PERCENTAGE);

        // sample for generating a second group
        try {
            Worker member = new GooglePlusSource().loadWorker("111163807910558864867");
            final HorizontalLayout members = new HorizontalLayout();
            members.addComponent(generateWorker(member));
            members.setMargin(false);
            groups.addTab(members, "It's a me, Mario");
        } catch (DataSourceException e) {
            e.printStackTrace();
        }
        try {
            Worker member = new GooglePlusSource().loadWorker("111163807910558864867");
            final HorizontalLayout members = new HorizontalLayout();
            members.addComponent(generateWorker(member));
            members.setMargin(false);
            groups.addTab(members, "It's a me, Lol");
        } catch (DataSourceException e) {
            e.printStackTrace();
        }
        try {
            Worker member = new GooglePlusSource().loadWorker("111163807910558864867");
            final HorizontalLayout members = new HorizontalLayout();
            members.addComponent(generateWorker(member));
            members.setMargin(false);
            groups.addTab(members, "It's a me, Hubert");
        } catch (DataSourceException e) {
            e.printStackTrace();
        }
        try {
            Worker member = new GooglePlusSource().loadWorker("111163807910558864867");
            final HorizontalLayout members = new HorizontalLayout();
            members.addComponent(generateWorker(member));
            members.setMargin(false);
            groups.addTab(members, "It's a me, Hansi");
        } catch (DataSourceException e) {
            e.printStackTrace();
        }
        // end sample

        // TODO: add function -> initialize from user / default user object
        for (Group group : user.getGroups()) {
            final HorizontalLayout members = new HorizontalLayout();
            for (Worker member : group.getWorkers()) {
                members.addComponent(generateWorker(member));
            }
            members.setMargin(false);
            groups.addTab(members, group.getName());
        }

        // TODO: add function -> create group
        // TODO: add function -> delete group
        // TODO: add function -> add worker
        // TODO: add function -> delete worker

        return groups;
    }


    @Override
    protected void init(VaadinRequest request) {

        final VerticalLayout root = new VerticalLayout();
        setContent(root);

        // generate Header area
        root.addComponent(generateHeader());

        //generate group area
        try {
            root.addComponent(generateGroups(new DummyUserController("Sebastian Stumpf").getUser()));
        } catch (ModifyUserException e) {
            e.printStackTrace();
        }

        // TODO: *************SEARCH*************
        // TODO: build layout -> search fields
        // TODO: add function -> search
        // TODO: build layout -> show results


        // felix testarea
        /*final TextField googleIdField = new TextField();
        root.addComponent(googleIdField);
        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                try {
                    Worker worker = new GooglePlusSource().loadWorker(googleIdField.getValue());
                    VerticalLayout workerLayout = new VerticalLayout();
                    workerLayout.addComponent(new Label(worker.getName()));
                    workerLayout.addComponent(new Image(null, new ExternalResource(worker.getImageURL())));
                    root.addComponent(workerLayout);
                } catch (DataSourceException e) {
                    LOGGER.error(e);
                }
            }
        });
        root.addComponent(button);
        */
    }

}
