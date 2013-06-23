package edu.hm.cs.ivaacal;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.annotations.Theme;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.*;
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
    private static final String ControllerSessionAttribute = "controller";
    private static final int workerWidth = 180;

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

        final Button button = new Button("Create group");
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

        HorizontalLayout createGroupArea = new HorizontalLayout();
        createGroupArea.setStyleName("create-group-area");
        createGroupArea.setHeight("49px");

        createGroupArea.addComponent(textField);
        createGroupArea.addComponent(buttonWrapper);
        createGroupArea.setComponentAlignment(textField, Alignment.MIDDLE_LEFT);
        createGroupArea.setComponentAlignment(buttonWrapper, Alignment.MIDDLE_LEFT);

        groupOptionsComponent.addComponent(createGroupArea);

        groupOptionsComponent.setWidth("100%");
        groupOptionsComponent.addStyleName("groupoptions");
        groupOptionsComponent.setMargin(true);

        groupOptionsComponent.setComponentAlignment(createGroupArea, Alignment.MIDDLE_LEFT);


        return groupOptionsComponent;
    }

    private Component generateWorker(final Worker worker) {

        GridLayout workerContainer = new GridLayout();
        workerContainer.addStyleName("worker");
        workerContainer.setRows(2);
        workerContainer.setColumns(2);
        workerContainer.setWidth("" + workerWidth);

        // image
        Image image = new Image(null, new ExternalResource(worker.getImageURL()));
        image.setWidth(null);

        // name
        Label name = new Label(worker.getName(), ContentMode.HTML);
        name.setWidth(null);

        // availability
        Label availability;
        long availableMinutes = (worker.getAvailabilityChangeDate().getTime() - new Date().getTime()) / 60000;
        long availableHours = availableMinutes / 60;
        availableMinutes %= 60;
        if (worker.isAvailable() != null && worker.isAvailable()) {
            availability = new Label("available for:<br />" + (availableHours == 0l ? "" : availableHours + " hours, ") + availableMinutes + " minutes", ContentMode.HTML);
            availability.addStyleName("available");
        } else {
            availability = new Label("available in:<br />" + (availableHours == 0l ? "" : availableHours + " hours, ") + availableMinutes + " minutes", ContentMode.HTML);
            availability.addStyleName("unavailable");
        }

        workerContainer.addComponent(image, 0, 0);
        workerContainer.addComponent(availability, 0, 1, 1, 1);
        workerContainer.addComponent(name, 1, 0);

        DragAndDropWrapper dragAndDropWrapperWorker = new DragAndDropWrapper(workerContainer);
        dragAndDropWrapperWorker.setDragStartMode(DragAndDropWrapper.DragStartMode.WRAPPER);
        dragAndDropWrapperWorker.addStyleName("worker");
        dragAndDropWrapperWorker.setData(worker);

        return dragAndDropWrapperWorker;
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
                loginLogoutComponent.setComponentAlignment(usernameLabel, Alignment.MIDDLE_LEFT);
                loginLogoutComponent.setComponentAlignment(logoutButton, Alignment.MIDDLE_LEFT);
            }
        });

        logoutButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                logout();
                loginLogoutComponent.removeAllComponents();
                loginLogoutComponent.addComponent(usernameTexfield);
                loginLogoutComponent.addComponent(loginButton);
                loginLogoutComponent.setComponentAlignment(usernameTexfield, Alignment.MIDDLE_LEFT);
                loginLogoutComponent.setComponentAlignment(loginButton, Alignment.MIDDLE_LEFT);
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

        VerticalLayout left = new VerticalLayout();
        VerticalLayout center = new VerticalLayout();
        VerticalLayout right = new VerticalLayout();

        // create left side
        // Add Login Fields
        Component loginlogout = generateLoginLogoutField();
        loginlogout.setWidth(null);
        HorizontalLayout loginLayout = new HorizontalLayout();
        loginLayout.addComponent(loginlogout);
        loginLayout.setMargin(true);
        Component createGroup = generateGroupOptionsArea();

        left.addComponent(loginLayout);
        left.addComponent(createGroup);
        left.setComponentAlignment(loginLayout, Alignment.TOP_LEFT);
        left.setComponentAlignment(createGroup, Alignment.BOTTOM_LEFT);

        // create center
        // Add Headline
        Image title = new Image();
        title.setSource(new ThemeResource("images/iVaaCal.png"));
        title.addStyleName("title");
        center.addComponent(title);
        center.setComponentAlignment(title, Alignment.MIDDLE_CENTER);

        // create right side
        Component dropZone = generateDeleteDropArea();
        dropZone.setSizeUndefined();
        right.setSizeFull();
        right.setMargin(true);
        right.addComponent(dropZone);
        right.setComponentAlignment(dropZone, Alignment.BOTTOM_RIGHT);
        right.setExpandRatio(dropZone, 1.0f);


        headerComponent.addComponent(left);
        headerComponent.addComponent(center);
        headerComponent.addComponent(right);

        headerComponent.setStyleName("header");
        headerComponent.setWidth("100%");

        return headerComponent;
    }

    private Component generateGroup(final GroupTO group) {

        // The groupContainerComponent
        final VerticalLayout groupContainer = new VerticalLayout();
        groupContainer.setStyleName("group");
        groupContainer.setSizeFull();

        // The Group Head
        HorizontalLayout groupHeadContainer = new HorizontalLayout();
        // Display group name
        Label groupName = new Label(group.getName());
        groupName.setStyleName("groupname");
        groupName.setWidth(null);
        //TODO: use userController method when implemented properly
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
            }
        };
        // Add Drop Handler for Workers
        groupHeadDragDropWrapper.setDropHandler(groupDropHandler);
        groupContainer.addComponent(groupHeadDragDropWrapper);


        // For being able to display more than one row the group is a vertical layer
        final HorizontalLayout groupContentContainer = new HorizontalLayout();
        groupContentContainer.setStyleName("groupcontent");
        groupContentContainer.setWidth(null);
        for (Worker member : group.getWorkers()) {
            groupContentContainer.addComponent(generateWorker(member));
        }

        if (!group.getName().equals("all")) {
            groupContentContainer.setVisible(group.isVisible());
            groupHeadContainer.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
                @Override
                public void layoutClick(final LayoutEvents.LayoutClickEvent layoutClickEvent) {
                    groupContentContainer.setVisible(!groupContentContainer.isVisible());
                    try {
                        userController.setVisible(group.getName(), groupContentContainer.isVisible());
                    } catch (ModifyUserException e) {
                        LOGGER.error("Set visible for group " + group.getName() + " failed: " + e.getStackTrace());
                    }
                    LOGGER.info("Visibility of group " + group.getName() + " toggled.");
                }
            });
        }


        DragAndDropWrapper groupContentDragDropWrapper = new DragAndDropWrapper(groupContentContainer);
        groupContentDragDropWrapper.setDropHandler(groupDropHandler);
        groupContentDragDropWrapper.addStyleName("groupcontent");
        groupContentDragDropWrapper.setWidth("100%");

        groupContentContainer.setData(group);
        groupContainer.addComponent(groupContentDragDropWrapper);

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
                final AbstractComponentContainer workerContainer = (AbstractComponentContainer) sourceComponent.getParent();
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
            getSession().setAttribute(ControllerSessionAttribute, userController);
        } catch (DataSourceException e) {
            LOGGER.info(username + " access denied: " + e.getMessage());
            return false;
        }
        groupsLayout.removeAllComponents();
        generateGroups(userController.getUserTO());
        return true;
    }

    private void logout() {

        this.userController = new EphemeralUserController();
        getSession().setAttribute(ControllerSessionAttribute, userController);
        groupsLayout.removeAllComponents();
        generateGroups(userController.getUserTO());
    }


    @Override
    protected void init(final VaadinRequest request) {

        final VerticalLayout root = new VerticalLayout();
        setContent(root);
        this.userController = (UserController) getSession().getAttribute(ControllerSessionAttribute);
        if (userController == null) {
            userController = new EphemeralUserController();
            getSession().setAttribute(ControllerSessionAttribute, userController);
        }
        // generate Header area
        root.addComponent(generateHeader());

        //generate group area
        root.addComponent(generateGroups(this.userController.getUserTO()));

        // testarea

    }

}
