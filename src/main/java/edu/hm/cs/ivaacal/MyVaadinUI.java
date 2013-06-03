package edu.hm.cs.ivaacal;

import com.vaadin.annotations.Theme;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import edu.hm.cs.ivaacal.controller.EphemeralUserController;
import edu.hm.cs.ivaacal.controller.PersistentUserController;
import edu.hm.cs.ivaacal.controller.UserController;
import edu.hm.cs.ivaacal.exception.DataSourceException;
import edu.hm.cs.ivaacal.exception.ModifyUserException;
import edu.hm.cs.ivaacal.model.Worker;
import edu.hm.cs.ivaacal.model.transport.GroupTO;
import edu.hm.cs.ivaacal.model.transport.UserTO;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import java.util.Date;


/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@Theme("ivaacal")
public class MyVaadinUI extends UI {

    private static final Logger LOGGER = Logger.getLogger(MyVaadinUI.class);
    private static Configuration config = IVaaCalConfiguration.getConfiguration();

    private boolean loggedIn = false;
    private UserController userController = new EphemeralUserController();
    private final VerticalLayout groupsLayout = new VerticalLayout();
    private final HorizontalLayout loginLogoutComponent = new HorizontalLayout();

    // TODO: add function -> create group
    private Component generateCreateGroup() {

        // The create group component
        HorizontalLayout createGroupComponent = new HorizontalLayout();
        final TextField textField = new TextField();
        textField.setInputPrompt("Name");
        createGroupComponent.addComponent(textField);

        Button button = new Button("Create group");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent clickEvent) {
                LOGGER.info("Click on create group occurred.");
                if(textField.getValue().isEmpty() || textField.getValue() == "all") return;
                GroupTO addedGroup;
                try {
                    userController.createGroup(textField.getValue());
                    //groupsLayout.addComponent(new Label(textField.getValue())userController.createGroup(textField.getValue()));
                } catch (ModifyUserException e) {
                    LOGGER.error("Generate group failed: " + e.getMessage());
                }
            }
        });
        createGroupComponent.addComponent(button);

        createGroupComponent.addStyleName("creategroup");

        return createGroupComponent;
    }

    // TODO: *************WORKER*************
    private Component generateWorker(final Worker worker) {

        GridLayout workerComponent = new GridLayout();
        workerComponent.addStyleName("worker");
        workerComponent.setRows(2);
        workerComponent.setColumns(2);

        // Fill in Image
        workerComponent.addComponent(new Image(null, new ExternalResource(worker.getImageURL())), 0, 0);

        // Fill in name
        workerComponent.addComponent(new Label(worker.getName(), ContentMode.HTML), 1, 0);

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
        workerComponent.addComponent(availability, 0, 1, 1, 1);

        // TODO: add function -> drag and drop
        DragAndDropWrapper draggableWorkerWrapper = new DragAndDropWrapper(workerComponent);
        draggableWorkerWrapper.setDragStartMode(DragAndDropWrapper.DragStartMode.WRAPPER);
        draggableWorkerWrapper.setData(worker);

        return draggableWorkerWrapper;
    }


    // TODO: *************LOGIN*************
    private Component generateLoginLogoutField() {

        final TextField usernameTexfield = new TextField();
        usernameTexfield.setInputPrompt("Username");
        final Button loginButton = new Button("Login");
        loginLogoutComponent.addComponent(usernameTexfield);
        loginLogoutComponent.addComponent(loginButton);

        final Label usernameLabel = new Label();
        final Button logoutButton = new Button("Logout");

        loginButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                login(usernameTexfield.getValue());
                loginLogoutComponent.removeAllComponents();
                loginLogoutComponent.addComponent(usernameLabel);
                loginLogoutComponent.addComponent(logoutButton);
                usernameLabel.setValue("Logged in: " + usernameTexfield.getValue());
            }
        });

        logoutButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                logout();
                loginLogoutComponent.removeAllComponents();
                loginLogoutComponent.addComponent(usernameTexfield);
                loginLogoutComponent.addComponent(loginButton);
            }
        });

        loginLogoutComponent.addStyleName("login");

        // TODO: build layout -> logged in fields
        // TODO: add function -> login
        // TODO: add function -> logout
        return loginLogoutComponent;
    }

    private Component generateHeader() {

        // The header Component
        HorizontalLayout headerComponent = new HorizontalLayout();
        headerComponent.setWidth("100%");
        headerComponent.setMargin(true);

        // Add Login Fields
        Component login = generateLoginLogoutField();
        headerComponent.addComponent(login);

        // Add Headline
        Label title = new Label("iVaaCal");
        title.addStyleName("h1");
        headerComponent.addComponent(title);

        headerComponent.setComponentAlignment(title, Alignment.MIDDLE_RIGHT);
        headerComponent.setComponentAlignment(login, Alignment.MIDDLE_LEFT);

        return headerComponent;
    }

    private Component generateGroup(final GroupTO group){

        // The group component
        final HorizontalLayout deletableGroupComponent = new HorizontalLayout();
        deletableGroupComponent.setMargin(true);
        deletableGroupComponent.setSizeFull();

        // For being able to display more than one row the group is a vertical layer
        VerticalLayout groupComponent = new VerticalLayout();
        deletableGroupComponent.addComponent(groupComponent);

        // The group is a panel with the name of the group
        Panel panel = new Panel(group.getName());
        // And as the content the members of the group
        final HorizontalLayout workersLayout = new HorizontalLayout();
        for (Worker member : group.getWorkers()) {
            workersLayout.addComponent(generateWorker(member));
        }
        panel.setContent(workersLayout);

        // TODO: add function -> add worker per drag and drop
        // Wrap the panel in DragAndDropWrapper
        DragAndDropWrapper dragDropWrapper = new DragAndDropWrapper(panel);
        groupComponent.addComponent(dragDropWrapper);

        // Add Drop Handler for Workers
        dragDropWrapper.setDropHandler(new DropHandler() {

            // Define accept criteria
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }
            // Handle drop event
            public void drop(final DragAndDropEvent event) {
                final Transferable transferable = event.getTransferable();
                final DragAndDropWrapper sourceComponent = (DragAndDropWrapper) transferable.getSourceComponent();
                final TargetDetails dropTargetData = event.getTargetDetails();
                final DropTarget target = dropTargetData.getTarget();
                Worker worker = (Worker)sourceComponent.getData();

                LOGGER.info(worker.getName() + " (ID:" + worker.getGooglePlusID() + ") was dropped on group " + group.getName());
                workersLayout.addComponent(event.getTransferable().getSourceComponent());
            }
        });

        // TODO: add function -> delete group
        // Add a delete Button
        if(group.getName() != "all") {
            Button button = new Button("X");
            button.setSizeFull();
            button.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent clickEvent) {
                    try {
                        userController.deleteGroup(group.getName());
                        groupsLayout.removeComponent(deletableGroupComponent);
                    } catch (ModifyUserException e) {
                        LOGGER.error("Delete group failed: " + e.getStackTrace());
                    }
                }
            });
            deletableGroupComponent.addComponent((button));

            deletableGroupComponent.setExpandRatio(groupComponent, 24);
            deletableGroupComponent.setExpandRatio(button, 1);
        }
        // TODO: add function -> delete worker

        return deletableGroupComponent;
    }

    private Component generateGroups(final UserTO user) {

        groupsLayout.setMargin(true);

        // generate create group
        groupsLayout.addComponent(generateCreateGroup());

        // Generate an add groups one by one
        for (GroupTO group : user.getGroups()) {
            groupsLayout.addComponent(generateGroup(group));
        }

        return groupsLayout;

    }

    private boolean login(final String username) {

        try {
            this.userController = new PersistentUserController(username);
        } catch (DataSourceException e) {
            LOGGER.info(username + " access denied: " + e.getMessage());
            return false;
        }
        this.loggedIn = true;
        groupsLayout.removeAllComponents();
        generateGroups(userController.getUserTO());
        return true;
    }

    private void logout() {

        this.userController = new EphemeralUserController();
        groupsLayout.removeAllComponents();
        generateGroups(userController.getUserTO());
        this.loggedIn = false;
    }

    @Override
    protected void init(final VaadinRequest request) {

        final VerticalLayout root = new VerticalLayout();
        setContent(root);

        // generate Header area
        root.addComponent(generateHeader());

        //generate group area
        root.addComponent(generateGroups(this.userController.getUserTO()));

		// TODO: *************SEARCH*************
        // TODO: build layout -> search fields
        // TODO: add function -> search
        // TODO: build layout -> show results


        // testarea

    }

}
