package ch.skymarshall.tcwriter.gui.steps;

import static ch.skymarshall.gui.swing.factories.SwingBindings.selection;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Arrays;

import javax.swing.CellRendererPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.swing.jtable.PolicyTableColumnModel;
import ch.skymarshall.gui.swing.jtable.TableColumnWithPolicy;
import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.gui.frame.TCWriterController;
import ch.skymarshall.tcwriter.gui.frame.TCWriterModel;
import ch.skymarshall.tcwriter.gui.steps.StepsTableModel.Column;

public class StepsTable extends JPanel {

	private static final TestCase NO_TC = new TestCase();

	static {
		NO_TC.setDictionary(new TestDictionary());
	}

	private final StepsTableModel stepsTableModel;

	private final JTable stepsJTable;

	public StepsTable(final TCWriterController controller) {
		final TCWriterModel model = controller.getModel();

		final ch.skymarshall.gui.model.ListModel<TestStep> steps = new ListModel<>(
				ListViews.sorted((s1, s2) -> s1.getOrdinal() - s2.getOrdinal()));

		setLayout(new BorderLayout());
		stepsTableModel = new StepsTableModel(model.getTc(), steps, controller.getTestRemoteControl());

		controller.getTestRemoteControl().setStepListener(stepsTableModel::stepExecutionUpdated);

		stepsJTable = new JTable(stepsTableModel) {

			@Override
			protected void paintChildren(final Graphics g) {
				super.paintChildren(g);
				CellRendererPane toDraw = null;
				for (final Component component : getComponents()) {
					if (component instanceof CellRendererPane) {
						toDraw = (CellRendererPane) component;
					}
				}
				if (toDraw == null) {
					throw new IllegalStateException("No renderer pane");
				}
				for (int i = 0; i < getRowCount(); i++) {
					final Rectangle toPaintHR = getCellRect(i, 2, true);
					toPaintHR.width = getColumnModel().getTotalColumnWidth();
					toPaintHR.height = toPaintHR.height / 2;

					g.setColor(getBackground());
					g.setClip(toPaintHR.x, toPaintHR.y, toPaintHR.width - toPaintHR.x, toPaintHR.height);
					g.fillRect(toPaintHR.x, toPaintHR.y, toPaintHR.width - toPaintHR.x, toPaintHR.height);
					g.setColor(getGridColor().brighter());
					g.drawLine(toPaintHR.x, toPaintHR.y + toPaintHR.height - 1, toPaintHR.width,
							toPaintHR.y + toPaintHR.height - 1);

					final TableCellRenderer renderer = new DefaultTableCellRenderer() {
						@Override
						public Component getTableCellRendererComponent(final JTable table, final Object value,
								final boolean isSelected, final boolean hasFocus, final int row, final int column) {
							super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
							setText(stepsTableModel.getHumanReadable(row));
							setFont(getFont().deriveFont(Font.BOLD));
							return this;
						}

					};
					final Component component = prepareRenderer(renderer, i, 2);
					component.setSize(toPaintHR.getSize());
					g.setColor(getGridColor().brighter());

					toDraw.paintComponent(g, component, this, toPaintHR.x, toPaintHR.y, toPaintHR.width,
							toPaintHR.height, true);
				}

			}
		};
		stepsJTable.setName("StepsTable");
		stepsJTable.setRowHeight(stepsJTable.getRowHeight() * 2);

		// Setup columns
		final PolicyTableColumnModel<StepsTableModel.Column> columnModel = new PolicyTableColumnModel<>(stepsJTable);
		columnModel.install();
		Arrays.stream(Column.values()).forEach(c -> stepsJTable.getColumn(c).setCellRenderer(new StepsCellRenderer()));
		columnModel.configureColumn(TableColumnWithPolicy.fixedWidth(Column.BREAKPOINT, 20)
				.apply(new StepStatusRenderer(), new StepStatusEditor()));
		columnModel.configureColumn(TableColumnWithPolicy.fixedWidth(Column.STEP, 20));
		columnModel.configureColumn(TableColumnWithPolicy.fixedWidth(Column.ACTOR, 120).apply(new StepsCellRenderer()));
		columnModel.configureColumn(
				TableColumnWithPolicy.percentOfAvailableSpace(Column.SELECTOR, 50).apply(new StepsCellRenderer()));
		columnModel.configureColumn(
				TableColumnWithPolicy.percentOfAvailableSpace(Column.PARAM0, 50).apply(new StepsCellRenderer()));
		columnModel
				.configureColumn(TableColumnWithPolicy.fixedWidth(Column.TO_VAR, 250).apply(new StepsCellRenderer()));

		// Refresh table when step is updated
		final IBindingController selectedStepCtrl = model.getSelectedStep()
				.bind(selection(stepsJTable, stepsTableModel));
		model.getSelectedStep().addListener(l -> {
			if (l.getOldValue() != null) {
				return;
			}
			selectedStepCtrl.getVeto().detach();
			final int row = stepsTableModel.getRowOf(model.getSelectedStep().getValue());
			stepsTableModel.fireTableRowsUpdated(row, row + 1);
			selectedStepCtrl.getVeto().attach();
		});

		add(new JScrollPane(stepsJTable), BorderLayout.CENTER);

		model.getTc().listen(tc -> {
			steps.clear();
			steps.setValues(tc.getSteps());
		});

	}

}
