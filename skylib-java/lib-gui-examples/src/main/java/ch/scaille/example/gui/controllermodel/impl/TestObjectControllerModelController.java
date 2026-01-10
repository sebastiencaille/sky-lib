package ch.scaille.example.gui.controllermodel.impl;

import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.logging.Logger;

import ch.scaille.example.gui.TestObject;
import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.mvc.GuiController;
import ch.scaille.util.helpers.Logs;
import lombok.Getter;

@Getter
public class TestObjectControllerModelController extends GuiController {

	private static final Logger LOGGER = Logs.of(TestObjectControllerModelController.class);
	
	private final ListModel<TestObject> model = new ListModel<>(
			ListViews.sorted(Comparator.comparingInt(TestObject::getASecondValue)));
	private final TestObjectControllerModelFrameModel tableModel;

	public TestObjectControllerModelController() {
		model.insert(new TestObject("Foo", 1));
		model.insert(new TestObject("Bar", 2));

		tableModel = new TestObjectControllerModelFrameModel(this, model) {
			@Override
			public void commit() {
				super.commit();
				model.values().stream().map(Object::toString).forEach(LOGGER::info);
			}
		};
	}

    public ActionListener getCommitAction() {
		return e -> tableModel.commit();
	}

}
