package ch.scaille.example.gui.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.scaille.example.gui.TestObjectTableModel;
import ch.scaille.example.gui.model.impl.TableModelExampleView;
import ch.scaille.tcwriter.jupiter.DisabledIfHeadless;
import ch.scaille.tcwriter.pilot.swing.SwingPilot;
import ch.scaille.util.helpers.Logs;

@ExtendWith(DisabledIfHeadless.class)
class ModelExampleTest {

	private static final int FIXED_COLUMN_WIDTH = 15;

	@Test
	void testExample() throws InvocationTargetException, InterruptedException {

		final var view = new TableModelExampleView[1];
		EventQueue.invokeAndWait(() -> {
			view[0] = new TableModelExampleView();
			view[0].setVisible(true);
		});

		final var pilot = new SwingPilot(view[0]);
		pilot.setDefaultPollingTimeout(Duration.ofSeconds(1));

		var page = pilot.page(ModelExamplePage::new);

		page.listTable.fail().ifNot().asserted(context -> {
			final var component = context.getComponent();
			Assertions.assertEquals(FIXED_COLUMN_WIDTH, component.getColumn(TestObjectTableModel.Columns.A_SECOND_VALUE).getWidth());
			Assertions.assertEquals(component.getWidth() - FIXED_COLUMN_WIDTH, component.getColumn(TestObjectTableModel.Columns.A_FIRST_VALUE).getWidth());
		});
		page.listTable.assertValue(0, 0, "One");
		page.listTable.assertValue(1, 0, "Two");
		page.listTable.assertValue(2, 0, "Three");
		page.listTable.assertValue(3, 0, "Four");

		page.reverseOrder.setSelected(true);
		page.listTable.assertValue(3, 0, "One");
		page.listTable.assertValue(2, 0, "Two");
		page.listTable.assertValue(1, 0, "Three");
		page.listTable.assertValue(0, 0, "Four");

		page.enableFilter.setSelected(true);
		page.listTable.assertValue(1, 0, "Two");
		page.listTable.assertValue(0, 0, "Four");

		page.reverseOrder.setSelected(false);
		page.listTable.assertValue(0, 0, "Two");
		page.listTable.assertValue(1, 0, "Four");

		page.enableFilter.setSelected(false);
		page.listTable.assertValue(0, 0, "One");
		page.listTable.assertValue(1, 0, "Two");
		page.listTable.assertValue(2, 0, "Three");
		page.listTable.assertValue(3, 0, "Four");

		Logs.of(this).info(pilot.getActionReport().getFormattedReport());
	}

}
