package de.r3r57.neelix.view.resources.tabs.request;

import java.lang.reflect.Field;

import de.r3r57.neelix.controller.Controller;
import de.r3r57.neelix.model.Model;
import de.r3r57.neelix.model.resources.RequestType;
import de.r3r57.neelix.model.resources.exception.ErrorCode;
import de.r3r57.neelix.model.resources.exception.SystemException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class RequestTypeChooser extends ComboBox<RequestType> {

    private Controller controller;
    private Model model;

    public RequestTypeChooser(Model model) throws SystemException {
	super();
	if (model != null) {

	    this.model = model;

	    this.setPrefWidth(Double.MAX_VALUE);
	    this.setVisibleRowCount(Integer.MAX_VALUE);

	    populate();
	    initListenerAndEvents();
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "constructor").set("arg0", "model=" + model);
	}
    }

    public void setController(Controller controller) throws SystemException {
	if (controller != null) {
	    this.controller = controller;

	    controller.setRequestType(this.getSelectionModel().getSelectedItem());

	    if (this.getSelectionModel().getSelectedItem().isIndependent()) {
		controller.disableOSButtons();
	    }
	} else {
	    throw new SystemException(ErrorCode.ILLEGAL_ARGUMENT).set("class", this.getClass())
		    .set("method", "setController").set("arg0", "controller=" + controller);
	}
    }

    public void reset() {
	this.getSelectionModel().selectFirst();
    }

    private void populate() {

	this.setItems(FXCollections.observableArrayList(model.getRequestTypes()));
	addTooltips();
	this.getSelectionModel().selectFirst();

    }

    private void addTooltips() {

	this.setCellFactory(cell -> {
	    return new ListCell<RequestType>() {

		@Override
		public void updateItem(RequestType item, boolean empty) {
		    super.updateItem(item, empty);
		    if (item != null && !empty) {
			setText(item.getName());
			Tooltip tt = new Tooltip();
			tt.setWrapText(true);
			tt.setPrefWidth(500);
			tt.setText(item.getDescription());
			hackTooltipStartTiming(tt);
			setTooltip(tt);
		    }
		}

	    };
	});
    }

    private void hackTooltipStartTiming(Tooltip tooltip) {
	try {
	    Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
	    fieldBehavior.setAccessible(true);
	    Object objBehavior = fieldBehavior.get(tooltip);

	    Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
	    fieldTimer.setAccessible(true);
	    Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

	    objTimer.getKeyFrames().clear();
	    objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void initListenerAndEvents() {

	this.getSelectionModel().selectedItemProperty().addListener(
		(ObservableValue<? extends RequestType> observable, RequestType oldValue, RequestType newValue) -> {
		    controller.setRequestType(newValue);
		    if (newValue.isIndependent()) {
			controller.disableOSButtons();
		    } else {
			controller.enableOSButtons();
		    }
		});

    }

}
