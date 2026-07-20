package ch.scaille.example.gui.controllermodel.impl;

import java.awt.event.ActionListener;
import java.util.Comparator;

import ch.scaille.example.gui.TestObject;
import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.mvc.GuiController;
import lombok.Getter;
import lombok.extern.java.Log;

@Getter
@Log
public class TestObjectControllerModelController extends GuiController {

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
				model.values().stream().map(Object::toString).forEach(log::info);
			}
		};
	}

    public ActionListener getCommitAction() {
		return _ -> tableModel.commit();
	}

}
