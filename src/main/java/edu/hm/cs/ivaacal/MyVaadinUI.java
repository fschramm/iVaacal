package edu.hm.cs.ivaacal;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import edu.hm.cs.ivaacal.controller.dataSource.GooglePlusSource;
import edu.hm.cs.ivaacal.exception.DataSourceException;
import edu.hm.cs.ivaacal.model.Worker;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;


/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinUI extends UI
{

	private static final Logger LOGGER = Logger.getLogger(MyVaadinUI.class);

	/**
	 * The Configuration of iVaaCal.
	 */
	private static Configuration config = IVaaCalConfiguration.getConfiguration();


	@Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

		final TextField googleIdField = new TextField();
		layout.addComponent(googleIdField);
        
        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
				try {
					Worker worker = new GooglePlusSource().loadWorker(googleIdField.getValue());
					VerticalLayout workerLayout = new VerticalLayout();
					workerLayout.addComponent(new Label(worker.getName()));
					workerLayout.addComponent(new Image(null, new ExternalResource(worker.getImageURL())));
					layout.addComponent(workerLayout);
				} catch (DataSourceException e) {
					LOGGER.error(e);
				}
			}
        });
        layout.addComponent(button);
    }

}
