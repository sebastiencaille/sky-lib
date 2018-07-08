package ch.skymarshall.tcwriter.hmi.steps;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.skymarshall.hmi.model.RootListModel;
import org.skymarshall.hmi.model.views.ListViews;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.swing.ContributionTableColumn;
import org.skymarshall.hmi.swing.ContributionTableColumnModel;

import ch.skymarshall.tcwriter.generators.TestSummaryVisitor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.hmi.TestRemoteControl;
import ch.skymarshall.tcwriter.hmi.steps.StepsTableModel.Column;

public class StepsTable extends JPanel {

	private static final TestCase NO_TC = new TestCase();

	static {
		NO_TC.setModel(new TestModel());
	}

	private final StepsTableModel stepsTableModel;

	private TestSummaryVisitor summaryVisitor = new TestSummaryVisitor(NO_TC);

	private final JTable stepsJTable;

	public StepsTable(final ObjectProperty<TestCase> testCaseProperty, final TestRemoteControl testControl) {

		final org.skymarshall.hmi.model.ListModel<TestStep> steps = new RootListModel<>(
				ListViews.sorted((s1, s2) -> s1.getOrdinal() - s2.getOrdinal()));

		testCaseProperty.addListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				summaryVisitor = new TestSummaryVisitor(testCaseProperty.getValue());
				steps.setValues(testCaseProperty.getValue().getSteps());

			}
		});

		setLayout(new BorderLayout());
		stepsTableModel = new StepsTableModel(testCaseProperty, steps, testControl);

		testControl.setStepListener(stepsTableModel::stepExecutionUpdated);

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
			stepsJTable.getColumn(c).setCellEditor(new StepsCellEditor(testCaseProperty));
		});

		stepsJTable.getColumn(Column.BREAKPOINT).setCellRenderer(new StepStatusRenderer());
		stepsJTable.getColumn(Column.BREAKPOINT).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		stepsJTable.getColumn(Column.TO_VALUE).setCellEditor(new StepsTextEditor());

		add(new JScrollPane(stepsJTable), BorderLayout.CENTER);
	}

}
