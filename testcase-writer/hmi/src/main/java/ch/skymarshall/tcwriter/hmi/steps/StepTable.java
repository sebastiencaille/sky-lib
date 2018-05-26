package ch.skymarshall.tcwriter.hmi.steps;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.swing.ContributionTableColumnModel;

import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.hmi.steps.StepsTableModel.Column;

public class StepTable extends JPanel {

	private final StepsTableModel stepsTableModel;

	public StepTable(final ListModel<TestStep> steps, final TestCase tc) {
		setLayout(new BorderLayout());
		this.stepsTableModel = new StepsTableModel(steps, tc.getModel());

		final JTable stepsTable = new JTable(this.stepsTableModel);
		final ContributionTableColumnModel<StepsTableModel.Column> columnModel = new ContributionTableColumnModel<>(
				stepsTable);
		columnModel.install();

		Arrays.stream(Column.values()).forEach(c -> stepsTable.getColumn(c).setCellEditor(new StepsCellEditor(tc)));
		add(new JScrollPane(stepsTable), BorderLayout.CENTER);
	}

}
