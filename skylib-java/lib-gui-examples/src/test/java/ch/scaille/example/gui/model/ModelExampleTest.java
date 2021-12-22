package ch.scaille.example.gui.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import javax.swing.JTable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.scaille.example.gui.TestObjectTableModel;
import ch.scaille.example.gui.model.impl.TableModelExampleView;
import ch.scaille.tcwriter.jupiter.DisabledIfHeadless;
import ch.scaille.tcwriter.pilot.Factories;
import ch.scaille.tcwriter.pilot.swing.SwingPilot;
import ch.scaille.util.helpers.Log;

@ExtendWith(DisabledIfHeadless.class)
class ModelExampleTest {

	@Test
	void testExample() throws InvocationTargetException, InterruptedException {

		final TableModelExampleView[] view = new TableModelExampleView[1];
		EventQueue.invokeAndWait(() -> {
			view[0] = new TableModelExampleView();
			view[0].setVisible(true);
		});

		final SwingPilot pilot = new SwingPilot(view[0]);
		pilot.setDefaultPollingTimeout(Duration.ofSeconds(1));

		ModelExamplePage page = pilot.page(ModelExamplePage::new);

		page.listTable.wait(Factories.<JTable>assertion(pc -> {
			JTable component = pc.component;
			Assertions.assertEquals(50, component.getColumn(TestObjectTableModel.Columns.A_SECOND_VALUE).getWidth());
			Assertions.assertEquals(component.getWidth() - 50,
					component.getColumn(TestObjectTableModel.Columns.A_FIRST_VALUE).getWidth());
		}));
		page.listTable.checkValue(0, 0, "One");
		page.listTable.checkValue(1, 0, "Two");
		page.listTable.checkValue(2, 0, "Three");
		page.listTable.checkValue(3, 0, "Four");

		page.reverseOrder.setSelected(true);
		page.listTable.checkValue(3, 0, "One");
		page.listTable.checkValue(2, 0, "Two");
		page.listTable.checkValue(1, 0, "Three");
		page.listTable.checkValue(0, 0, "Four");

		page.enableFilter.setSelected(true);
		page.listTable.checkValue(1, 0, "Two");
		page.listTable.checkValue(0, 0, "Four");

		page.reverseOrder.setSelected(false);
		page.listTable.checkValue(0, 0, "Two");
		page.listTable.checkValue(1, 0, "Four");

		page.enableFilter.setSelected(false);
		page.listTable.checkValue(0, 0, "One");
		page.listTable.checkValue(1, 0, "Two");
		page.listTable.checkValue(2, 0, "Three");
		page.listTable.checkValue(3, 0, "Four");

		Log.of(this).info(pilot.getActionReport().getFormattedReport());
	}

}
