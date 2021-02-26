package ch.skymarshall.example.gui.model;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import javax.swing.JTable;

import org.junit.Assert;
import org.junit.Test;

import ch.skymarshall.example.gui.TestObjectTableModel;
import ch.skymarshall.example.gui.model.impl.TableModelExampleView;
import ch.skymarshall.tcwriter.pilot.StatePolling;
import ch.skymarshall.tcwriter.pilot.swing.SwingPilot;
import ch.skymarshall.tcwriter.pilot.swing.JTablePilot;
import ch.skymarshall.tcwriter.pilot.swing.JToggleButtonPilot;
import ch.skymarshall.util.helpers.Log;

public class ModelExampleTest {

	@Test
	public void testExample() throws InvocationTargetException, InterruptedException {

		final TableModelExampleView[] view = new TableModelExampleView[1];
		EventQueue.invokeAndWait(() -> {
			view[0] = new TableModelExampleView();
			view[0].setVisible(true);
		});

		final SwingPilot pilot = new SwingPilot(view[0]);
		pilot.setDefaultActionTimeout(Duration.ofSeconds(1));

		listTable(pilot).wait(StatePolling.<JTable>assertion(t -> {
			Assert.assertEquals(50, t.getColumn(TestObjectTableModel.Columns.A_SECOND_VALUE).getWidth());
			Assert.assertEquals(t.getWidth() - 50, t.getColumn(TestObjectTableModel.Columns.A_FIRST_VALUE).getWidth());
		}));
		listTable(pilot).checkValue(0, 0, "One");
		listTable(pilot).checkValue(1, 0, "Two");
		listTable(pilot).checkValue(2, 0, "Three");
		listTable(pilot).checkValue(3, 0, "Four");

		reverseOrder(pilot).setSelected(true);
		listTable(pilot).checkValue(3, 0, "One");
		listTable(pilot).checkValue(2, 0, "Two");
		listTable(pilot).checkValue(1, 0, "Three");
		listTable(pilot).checkValue(0, 0, "Four");

		enableFilter(pilot).setSelected(true);
		listTable(pilot).checkValue(1, 0, "Two");
		listTable(pilot).checkValue(0, 0, "Four");

		reverseOrder(pilot).setSelected(false);
		listTable(pilot).checkValue(0, 0, "Two");
		listTable(pilot).checkValue(1, 0, "Four");

		enableFilter(pilot).setSelected(false);
		listTable(pilot).checkValue(0, 0, "One");
		listTable(pilot).checkValue(1, 0, "Two");
		listTable(pilot).checkValue(2, 0, "Three");
		listTable(pilot).checkValue(3, 0, "Four");

		Log.of(this).info(pilot.getActionReport().getFormattedReport());
	}

	private JToggleButtonPilot reverseOrder(final SwingPilot pilot) {
		return new JToggleButtonPilot(pilot, "reverseOrder");
	}

	private JToggleButtonPilot enableFilter(final SwingPilot pilot) {
		return new JToggleButtonPilot(pilot, "enableFilter");
	}

	private JTablePilot listTable(final SwingPilot pilot) {
		return new JTablePilot(pilot, "listTable");
	}

}
