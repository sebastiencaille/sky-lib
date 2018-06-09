package ch.skymarshall.tcwriter.hmi.steps;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.swing.ContributionTableColumn;
import org.skymarshall.hmi.swing.ContributionTableColumnModel;

import ch.skymarshall.tcwriter.generators.TestSummaryVisitor;
import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.hmi.TestRemoteControl;
import ch.skymarshall.tcwriter.hmi.steps.StepsTableModel.Column;

public class StepsTable extends JPanel {

	private final StepsTableModel stepsTableModel;

	private final TestSummaryVisitor summaryVisitor;

	private final JTable stepsJTable;

	public StepsTable(final ListModel<TestStep> steps, final TestCase tc, final TestRemoteControl testControl) {

		summaryVisitor = new TestSummaryVisitor(tc);

		setLayout(new BorderLayout());
		stepsTableModel = new StepsTableModel(steps, tc, testControl);

		testControl.setStepListener(stepsTableModel::stepUpdated);

		stepsJTable = new JTable(stepsTableModel);
		final ContributionTableColumnModel<StepsTableModel.Column> columnModel = new ContributionTableColumnModel<>(
				stepsJTable);
		columnModel.install();
		columnModel.configureColumn(
				ContributionTableColumn.fixedColumn(Column.BREAKPOINT, 20, new DefaultTableCellRenderer()));
		columnModel
				.configureColumn(ContributionTableColumn.fixedColumn(Column.STEP, 20, new DefaultTableCellRenderer()));
		columnModel.configureColumn(
				ContributionTableColumn.fixedColumn(Column.ACTOR, 120, new DefaultTableCellRenderer()));
		columnModel.configureColumn(
				ContributionTableColumn.gapColumn(Column.TO_VALUE, 100, new DefaultTableCellRenderer()));

		Arrays.stream(Column.values()).forEach(c -> {
			stepsJTable.getColumn(c).setCellRenderer(new StepsCellRenderer(summaryVisitor));
			stepsJTable.getColumn(c).setCellEditor(new StepsCellEditor(tc));
		});

		stepsJTable.getColumn(Column.BREAKPOINT).setCellRenderer(new BreakpointRenderer(testControl));
		stepsJTable.getColumn(Column.BREAKPOINT).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		stepsJTable.getColumn(Column.TO_VALUE).setCellEditor(new StepsTextEditor());

		add(new JScrollPane(stepsJTable), BorderLayout.CENTER);
	}

}
