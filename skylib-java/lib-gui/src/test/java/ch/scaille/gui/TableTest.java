package ch.scaille.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTable;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.model.views.IListView;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.swing.factories.SwingBindings;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.properties.ListProperty;

class TableTest {

	private static final IListView<TestObject> VIEW = ListViews.sorted(Comparator.comparingInt(TestObject::getVal));

	@NullMarked
	private static class Model extends GuiModel {

		private final ListProperty<TestObject> selection = new ListProperty<>("Selection", this);

		public Model(final ModelConfiguration.ModelConfigurationBuilder config) {
			super(config);
		}
	}

	@Test
	void testSelectionOnInsert() throws InvocationTargetException, InterruptedException {

		final var support = PropertyChangeSupportController.mainGroup(this);
		final var model = new Model(GuiModel.ModelConfiguration.builder().propertySupport(support));
		support.transmitChangesBothWays();

		final var listModel = new ListModel<>(VIEW);
		final var tableModel = new TestObjectTableModel(listModel);
		final var table = new JTable(tableModel);
		model.selection.bind(SwingBindings.multipleSelection(table, tableModel));

		final var object1 = new TestObject(1);
		final var object3 = new TestObject(3);
		final var object5 = new TestObject(5);

		EventQueue.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				listModel.insert(object1);
				listModel.insert(object3);
				listModel.insert(object5);
				model.selection.setValue(this, Collections.singletonList(object3));
				listModel.insert(new TestObject(2));
				assertEquals(1, model.selection.getValue().size());
				listModel.insert(new TestObject(4));
				assertEquals(1, model.selection.getValue().size());
			}
		});

	}
}
