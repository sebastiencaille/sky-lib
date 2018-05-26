package ch.skymarshall.tcwriter.hmi;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.RootListModel;
import org.skymarshall.hmi.model.views.ListViews;
import org.skymarshall.hmi.swing.ContributionTableColumnModel;

import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.hmi.steps.StepsCellEditor;
import ch.skymarshall.tcwriter.hmi.steps.StepsTableModel;
import ch.skymarshall.tcwriter.hmi.steps.StepsTableModel.Column;

public class TCWriter extends JFrame {

	private final TestCase testCase;
	private final ListModel<TestStep> steps = new RootListModel<>(ListViews.sorted(TestStep::getOrdinal));
	private final StepsTableModel stepsTableModel;

	public TCWriter(final TestCase tc) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		for (int i = 0; i < tc.getSteps().size(); i++) {
			tc.getSteps().get(i).setOrdinal(i);
		}

		this.getContentPane().setLayout(new BorderLayout());
		this.testCase = tc;
		this.steps.addValues(tc.getSteps());
		this.stepsTableModel = new StepsTableModel(steps, tc.getModel());

		final JTable stepsTable = new JTable(this.stepsTableModel);
		new ContributionTableColumnModel(stepsTable) {
			@Override
			public int getColumnIndex(final Object identifier) {
				return ((Column) identifier).ordinal();
			}
		}.install();
		stepsTable.createDefaultColumnsFromModel();
		stepsTable.setAutoCreateColumnsFromModel(false);
		Arrays.stream(Column.values()).forEach(c -> stepsTable.getColumn(c).setCellEditor(new StepsCellEditor(tc)));
		this.getContentPane().add(new JScrollPane(stepsTable), BorderLayout.CENTER);

		this.pack();
	}

}
