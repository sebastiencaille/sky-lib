package ch.scaille.tcwriter.gui.steps;

import static ch.scaille.gui.swing.factories.SwingBindings.selection;

import java.awt.BorderLayout;
import java.awt.Color;
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

import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.swing.jtable.PolicyTableColumnModel;
import ch.scaille.gui.swing.jtable.TableColumnWithPolicy;
import ch.scaille.tcwriter.generators.model.testapi.StepClassifier;
import ch.scaille.tcwriter.generators.model.testcase.TestStep;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.gui.steps.StepsTableModel.Column;

public class StepsTable extends JPanel {

	private final StepsTableModel stepsTableModel;

	private final JTable stepsJTable;

	public StepsTable(final TCWriterController controller) {
		final var model = controller.getModel();

		final ch.scaille.gui.model.ListModel<TestStep> steps = new ListModel<>(
				ListViews.sorted((s1, s2) -> s1.getOrdinal() - s2.getOrdinal()));

		setLayout(new BorderLayout());
		stepsTableModel = new StepsTableModel(model.getTestCase(), steps, controller.getTestRemoteControl());

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
		final var columnModel = new PolicyTableColumnModel<StepsTableModel.Column>(stepsJTable);
		columnModel.install();
		Arrays.stream(Column.values()).forEach(c -> stepsJTable.getColumn(c).setCellRenderer(new StepsCellRenderer()));
		columnModel.configureColumn(TableColumnWithPolicy.fixedWidth(Column.BREAKPOINT, 20)
				.with(new StepStatusRenderer(), new StepStatusEditor()));
		columnModel.configureColumn(
				TableColumnWithPolicy.fixedWidth(Column.ORDINAL, 20).with(new DefaultTableCellRenderer() {
					@Override
					public Component getTableCellRendererComponent(JTable var1, Object obj, boolean var3, boolean var4,
							int row, int col) {
						super.getTableCellRendererComponent(var1, obj, var3, var4, row, col);
						StepClassifier classifier = steps.getElementAt(row).getClassifier();
						if (classifier == null) {
							return this;
						}
						switch (classifier) {
						case PREPARATION:
							setBackground(Color.CYAN);
							break;
						case ACTION:
							setBackground(Color.ORANGE);
							break;
						case CHECK:
							setBackground(Color.GREEN);
							break;
						default:
						}
						return this;
					}
				}));

		columnModel.configureColumn(TableColumnWithPolicy.fixedWidth(Column.ACTOR, 120).with(new StepsCellRenderer()));
		columnModel.configureColumn(
				TableColumnWithPolicy.percentOfAvailableSpace(Column.SELECTOR, 50).with(new StepsCellRenderer()));
		columnModel.configureColumn(
				TableColumnWithPolicy.percentOfAvailableSpace(Column.PARAM0, 50).with(new StepsCellRenderer()));
		columnModel.configureColumn(TableColumnWithPolicy.fixedWidth(Column.TO_VAR, 250).with(new StepsCellRenderer()));

		// Refresh table when step is updated
		final var selectedStepCtrl = model.getSelectedStep().bind(selection(stepsJTable, stepsTableModel));
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

		model.getTestCase().listenActive(tc -> {
			steps.clear();
			steps.setValues(tc.getSteps());
		});

	}

}
