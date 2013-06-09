package edu.hm.cs.ivaacal;

import com.vaadin.annotations.Theme;
import com.vaadin.event.LayoutEvents;
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
import edu.hm.cs.ivaacal.util.AcceptNothing;
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
    private static final String CONTROLLER_SESSION_ATTRIBUTE = "controller";
    private static final int workersPerRow = 8;

    private boolean loggedIn = false;
    private UserController userController;
    private final VerticalLayout groupsLayout = new VerticalLayout();
    private final HorizontalLayout loginLogoutComponent = new HorizontalLayout();

    private Component generateGroupOptionsArea() {

        // The create group component
        HorizontalLayout groupOptionsComponent = new HorizontalLayout();

        final TextField textField = new TextField();
        textField.setInputPrompt("Name");
        textField.setWidth(null);

        HorizontalLayout buttonWrapper = new HorizontalLayout();

        Button button = new Button("Create group");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent clickEvent) {
                LOGGER.info("Click on create group occurred.");
                if (textField.getValue().isEmpty() || textField.getValue().equals("all")) {
                    LOGGER.info("Group with name " + textField.getValue() + " cannot be created.");
                    return;
                }
                try {
                    userController.createGroup(textField.getValue());
                    groupsLayout.removeAllComponents();
                    generateGroups(userController.getUserTO());
                    LOGGER.info("Group with name " + textField.getValue() + " created.");
                } catch (ModifyUserException e) {
                    LOGGER.error("Generate group failed: " + e.getMessage());
                }
            }
        });

        buttonWrapper.addComponent(button);
        buttonWrapper.setWidth(null);

        Component deleteDropArea = generateDeleteDropArea();
        deleteDropArea.setWidth(null);

        groupOptionsComponent.addComponent(textField);
        groupOptionsComponent.addComponent(buttonWrapper);
        groupOptionsComponent.addComponent(deleteDropArea);

        groupOptionsComponent.setWidth("100%");
        groupOptionsComponent.addStyleName("groupoptions");
        groupOptionsComponent.setMargin(true);
        groupOptionsComponent.setExpandRatio(deleteDropArea, 1.0f);
        groupOptionsComponent.setComponentAlignment(textField, Alignment.MIDDLE_LEFT);
        groupOptionsComponent.setComponentAlignment(buttonWrapper, Alignment.MIDDLE_LEFT);
        groupOptionsComponent.setComponentAlignment(deleteDropArea, Alignment.MIDDLE_RIGHT);

        return groupOptionsComponent;
    }

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
        long availableMinutes = (worker.getAvailabilityChangeDate().getTime() - new Date().getTime()) / 60000;
        long availableHours = availableMinutes / 60;
        availableMinutes %= 60;
        Label availability;
        if (worker.isAvailable() != null && worker.isAvailable()) {
            availability = new Label("available for:<br />" + (availableHours == 0l ? "" : availableHours + " hours, ") + availableMinutes + " minutes", ContentMode.HTML);
            availability.addStyleName("available");
        } else {
            availability = new Label("available in:<br />" + (availableHours == 0l ? "" : availableHours + " hours, ") + availableMinutes + " minutes", ContentMode.HTML);
            availability.addStyleName("unavailable");
        }
        workerComponent.addComponent(availability, 0, 1, 1, 1);

        DragAndDropWrapper draggableWorkerWrapper = new DragAndDropWrapper(workerComponent);
        draggableWorkerWrapper.setDragStartMode(DragAndDropWrapper.DragStartMode.WRAPPER);
        draggableWorkerWrapper.setData(worker);

        return draggableWorkerWrapper;
    }


    private Component generateLoginLogoutField() {

        final TextField usernameTexfield = new TextField();
        usernameTexfield.setInputPrompt("Username");
        final Button loginButton = new Button("Login");

        final Label usernameLabel = new Label();
        final Button logoutButton = new Button("Logout");

        loginButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                login(usernameTexfield.getValue());
                loginLogoutComponent.removeAllComponents();
                loginLogoutComponent.addComponent(usernameLabel);
                loginLogoutComponent.addComponent(logoutButton);
                usernameLabel.setValue("Logged in: " + userController.getUserTO().getName());
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

        if (userController.isLoggedIn()) {
            loginLogoutComponent.addComponent(usernameLabel);
            usernameLabel.setValue("Logged in: " + userController.getUserTO().getName());
            loginLogoutComponent.addComponent(logoutButton);
            loginLogoutComponent.setComponentAlignment(usernameLabel, Alignment.MIDDLE_LEFT);
            loginLogoutComponent.setComponentAlignment(logoutButton, Alignment.MIDDLE_LEFT);

        } else {
            loginLogoutComponent.addComponent(usernameTexfield);
            loginLogoutComponent.addComponent(loginButton);
            loginLogoutComponent.setComponentAlignment(usernameTexfield, Alignment.MIDDLE_LEFT);
            loginLogoutComponent.setComponentAlignment(loginButton, Alignment.MIDDLE_LEFT);
        }
        return loginLogoutComponent;
    }

    private Component generateHeader() {

        // The header Component
        HorizontalLayout headerComponent = new HorizontalLayout();

        // Add Login Fields
        Component loginlogout = generateLoginLogoutField();
        loginlogout.setWidth(null);

        // Add Headline
        Label title = new Label("iVaaCal");
        title.addStyleName("title");
        title.setWidth(null);

        headerComponent.addComponent(loginlogout);
        headerComponent.addComponent(title);
        headerComponent.setStyleName("header");
        headerComponent.setWidth("100%");
        headerComponent.setMargin(true);
        headerComponent.setExpandRatio(title, 1.0f);
        headerComponent.setComponentAlignment(loginLogoutComponent, Alignment.MIDDLE_LEFT);
        headerComponent.setComponentAlignment(title, Alignment.MIDDLE_LEFT);
        return headerComponent;
    }

    private Component generateGroup(final GroupTO group) {

        // The groupContainerComponent
        final VerticalLayout groupContainer = new VerticalLayout();
        groupContainer.setSizeFull();

        // The Group Head
        HorizontalLayout groupHeadContainer = new HorizontalLayout();
        // Display group name
        Label groupName = new Label(group.getName());
        groupName.setStyleName("groupname");
        groupName.setWidth(null);
        // Display next date
        Label nextDate = new Label("Dummy Date: 01.01.01 11:11:11");
        nextDate.setStyleName("groupdate");
        groupName.setWidth(null);
        // Delete button
        Button deleteButton = new Button("Delete group");
        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                try {
                    userController.deleteGroup(group.getName());
                    groupsLayout.removeComponent(groupContainer);
                } catch (ModifyUserException e) {
                    LOGGER.error("Delete group failed: " + e.getStackTrace());
                }
            }
        });
        deleteButton.setWidth(null);

        if (group.getName().equals("all")) {
            groupHeadContainer.addComponent(groupName);
            groupHeadContainer.setExpandRatio(groupName, 1.0f);
        } else {
            groupHeadContainer.addComponent(groupName);
            groupHeadContainer.addComponent(nextDate);
            groupHeadContainer.addComponent(deleteButton);
            groupHeadContainer.setComponentAlignment(deleteButton, Alignment.MIDDLE_RIGHT);
            groupHeadContainer.setComponentAlignment(nextDate, Alignment.MIDDLE_CENTER);
            groupHeadContainer.setExpandRatio(nextDate, 1.0f);
        }
        groupHeadContainer.setWidth("100%");
        groupHeadContainer.setComponentAlignment(groupName, Alignment.MIDDLE_LEFT);


        // For being able to display more than one row the group is a vertical layer
        final VerticalLayout groupContentContainer = new VerticalLayout();

        // Wrap the panel in DragAndDropWrapper
        DragAndDropWrapper groupHeadDragDropWrapper = new DragAndDropWrapper(groupHeadContainer);
        groupHeadDragDropWrapper.setStyleName("grouphead");
        // Define a group drag and drop handler
        DropHandler groupDropHandler = new DropHandler() {
            // Define accept criteria
            public AcceptCriterion getAcceptCriterion() {
                if (!group.getName().equals("all")) {
                    return AcceptAll.get();
                } else {
                    return new AcceptNothing();
                }
            }

            // Handle drop event
            public void drop(final DragAndDropEvent event) {
                final Transferable transferable = event.getTransferable();
                final DragAndDropWrapper sourceComponent = (DragAndDropWrapper) transferable.getSourceComponent();
                final TargetDetails dropTargetData = event.getTargetDetails();
                final DropTarget target = dropTargetData.getTarget();
                Worker worker = (Worker) sourceComponent.getData();
                try {
                    userController.addWorker(worker.getGooglePlusID(), group.getName());
                    // TODO: test functionality when setVisible is implemented
                    userController.setVisible(group.getName(), true);
                    LOGGER.info(worker.getName() + " (ID:" + worker.getGooglePlusID() + ") was dropped on group " + group.getName());
                    groupsLayout.removeAllComponents();
                    generateGroups(userController.getUserTO());
                } catch (ModifyUserException e) {
                    LOGGER.error("Add worker " + worker.getName() + "(ID:" + worker.getGooglePlusID() + ") failed: " + e.getStackTrace());
                }
                groupContentContainer.setVisible(true);
            }
        };
        // Add Drop Handler for Workers
        groupHeadDragDropWrapper.setDropHandler(groupDropHandler);
        groupContainer.addComponent(groupHeadDragDropWrapper);

        // And as the content the members of the group
        HorizontalLayout groupRowContainer = new HorizontalLayout();
        groupRowContainer.setWidth("100%");
        int counter = 0;
        for (Worker member : group.getWorkers()) {
            groupRowContainer.addComponent(generateWorker(member));
            if (++counter == workersPerRow) {
                groupContentContainer.addComponent(groupRowContainer);
                groupRowContainer = new HorizontalLayout();
                groupRowContainer.setWidth("100%");
                counter = 0;
            }
        }

        if (!group.getName().equals("all")) {
            groupContentContainer.setVisible(userController.isVisible(group.getName()));
            groupHeadContainer.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
                @Override
                public void layoutClick(final LayoutEvents.LayoutClickEvent layoutClickEvent) {
                    groupContentContainer.setVisible(!groupContentContainer.isVisible());
                    userController.setVisible(group.getName(), groupContentContainer.isVisible());
                    LOGGER.info("Visibility of group " + group.getName() + " toggled.");
                }
            });
        }
        groupContentContainer.setData(group);
        groupContentContainer.addComponent(groupRowContainer);
        groupContentContainer.setWidth("100%");
        groupContentContainer.addStyleName("groupcontent");

        groupContainer.addComponent(groupContentContainer);

        return groupContainer;
    }

    private Component generateGroups(final UserTO user) {

        groupsLayout.setMargin(true);

        // Generate an add groups one by one
        for (GroupTO group : user.getGroups()) {
            groupsLayout.addComponent(generateGroup(group));
        }

        return groupsLayout;

    }

    private Component generateDeleteDropArea() {

        HorizontalLayout dropArea = new HorizontalLayout();
        dropArea.setStyleName("deletearea");
        dropArea.setMargin(true);

        Label label = new Label("Drop worker here to delete from group!");

        dropArea.addComponent(label);

        dropArea.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

        DragAndDropWrapper dropAreaWrapper = new DragAndDropWrapper(dropArea);

        dropAreaWrapper.setDropHandler(new DropHandler() {
            @Override
            public void drop(final DragAndDropEvent dragAndDropEvent) {
                final Transferable transferable = dragAndDropEvent.getTransferable();
                final DragAndDropWrapper sourceComponent = (DragAndDropWrapper) transferable.getSourceComponent();
                final TargetDetails dropTargetData = dragAndDropEvent.getTargetDetails();
                final DropTarget target = dropTargetData.getTarget();
                final Worker worker = (Worker) sourceComponent.getData();
                final AbstractComponentContainer workerContainer = (AbstractComponentContainer) sourceComponent.getParent().getParent();
                final GroupTO group = (GroupTO) workerContainer.getData();
                if (!group.getName().equals("all")) {
                    try {
                        userController.removeWorker(worker.getGooglePlusID(), group.getName());
                        LOGGER.info(worker.getName() + " (ID:" + worker.getGooglePlusID() + ") was removed from group " + group.getName());
                        groupsLayout.removeAllComponents();
                        generateGroups(userController.getUserTO());
                    } catch (ModifyUserException e) {
                        LOGGER.info(worker.getName() + " (ID:" + worker.getGooglePlusID() + ") could not be removed from group " + group.getName());
                        e.printStackTrace();
                    }
                } else {
                    LOGGER.info(worker.getName() + " (ID:" + worker.getGooglePlusID() + ") cannot be removed to group " + group.getName());
                }
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }
        });

        dropAreaWrapper.setStyleName("deletearea");
        return dropAreaWrapper;

    }

    private boolean login(final String username) {

        try {
            this.userController = new PersistentUserController(username);
            getSession().setAttribute(CONTROLLER_SESSION_ATTRIBUTE, userController);
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
        getSession().setAttribute(CONTROLLER_SESSION_ATTRIBUTE, userController);
        groupsLayout.removeAllComponents();
        generateGroups(userController.getUserTO());
        this.loggedIn = false;
    }

    @Override
    protected void init(final VaadinRequest request) {

        this.userController = (UserController) getSession().getAttribute(CONTROLLER_SESSION_ATTRIBUTE);
        if (userController == null) {
            userController = new EphemeralUserController();
            getSession().setAttribute(CONTROLLER_SESSION_ATTRIBUTE, userController);
        }
        final VerticalLayout root = new VerticalLayout();
        setContent(root);

        // generate Header area
        root.addComponent(generateHeader());

        // generate create group
        root.addComponent(generateGroupOptionsArea());

        //generate group area
        root.addComponent(generateGroups(this.userController.getUserTO()));

        // testarea

    }

}
