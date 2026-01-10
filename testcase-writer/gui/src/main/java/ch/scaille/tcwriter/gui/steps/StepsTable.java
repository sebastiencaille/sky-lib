package ch.scaille.tcwriter.gui.steps;

import static ch.scaille.gui.swing.factories.SwingBindings.selection;
import static ch.scaille.gui.swing.jtable.TableColumnWithPolicy.fixedTextLength;
import static ch.scaille.gui.swing.jtable.TableColumnWithPolicy.percentOfAvailableSpace;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serial;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import javax.swing.CellRendererPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import ch.scaille.gui.model.ListModel;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.gui.swing.SwingExt;
import ch.scaille.gui.swing.jtable.PolicyTableColumnModel;
import ch.scaille.gui.swing.jtable.TableColumnWithPolicy;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.gui.steps.StepsTableModel.Column;
import ch.scaille.tcwriter.model.testcase.TestStep;

public class StepsTable extends JPanel {

	@Serial
    private static final long serialVersionUID = -8912254280325934892L;

	private final StepsTableModel stepsTableModel;

	private final JTable stepsJTable;

	public StepsTable(final TCWriterController controller) {
		final var model = controller.getModel();

		final var steps = new ListModel<>(ListViews.sorted(Comparator.comparingInt(TestStep::getOrdinal)));

		setLayout(new BorderLayout());
		stepsTableModel = new StepsTableModel(model.getTestCase(), steps, controller.getTestRemoteControl());

		controller.getTestRemoteControl().setStepListener(
				(f, t) -> SwingUtilities.invokeLater(() -> stepsTableModel.stepExecutionUpdated(f - 1, t - 1)));

		stepsJTable = new JTable(stepsTableModel) {

			@Serial
            private static final long serialVersionUID = -3114512815335033791L;

			@Override
			protected void paintChildren(final Graphics g) {
				super.paintChildren(g);
				CellRendererPane toDraw = null;
				for (final Component component : getComponents()) {
					if (component instanceof CellRendererPane pane) {
						toDraw = pane;
					}
				}
				Objects.requireNonNull(toDraw, "No renderer pane");
				for (int i = 0; i < getRowCount(); i++) {
					final var toPaintHR = getCellRect(i, 2, true);
					toPaintHR.width = getColumnModel().getTotalColumnWidth();
					toPaintHR.height = toPaintHR.height / 2;

					g.setColor(getBackground());
					g.setClip(toPaintHR.x, toPaintHR.y, toPaintHR.width - toPaintHR.x, toPaintHR.height);
					g.fillRect(toPaintHR.x, toPaintHR.y, toPaintHR.width - toPaintHR.x, toPaintHR.height);
					g.setColor(getGridColor().brighter());
					g.drawLine(toPaintHR.x, toPaintHR.y + toPaintHR.height - 1, toPaintHR.width,
							toPaintHR.y + toPaintHR.height - 1);

					final var renderer = new DefaultTableCellRenderer() {
						
						@Serial
                        private static final long serialVersionUID = 2857485822815100155L;

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
				fixedTextLength(Column.ORDINAL, 2, TableColumnWithPolicy.SAMPLE_NUMBERS,
						TableColumnWithPolicy.DEFAULT_MARGIN).with(new DefaultTableCellRenderer() {
							@Serial
                            private static final long serialVersionUID = -8917034957019159554L;

							@Override
							public Component getTableCellRendererComponent(JTable table, Object obj, boolean var3,
									boolean var4, int row, int col) {
								super.getTableCellRendererComponent(table, obj, var3, var4, row, col);
								final var classifier = steps.getElementAt(row).getClassifier();
								if (classifier == null) {
									return this;
								}
								setBackground(switch (classifier) {
									case PREPARATION -> Color.CYAN;
									case ACTION -> Color.ORANGE;
									case CHECK -> Color.GREEN;
								});
								return this;
							}
						}));

		columnModel
				.configureColumn(fixedTextLength(Column.ACTOR, 15).with(new StepsCellRenderer()));
		columnModel.configureColumn(
				percentOfAvailableSpace(Column.SELECTOR, 50).with(new StepsCellRenderer()));
		columnModel.configureColumn(
				percentOfAvailableSpace(Column.PARAM0, 50).with(new StepsCellRenderer()));
		columnModel
				.configureColumn(fixedTextLength(Column.TO_VAR, 30).with(new StepsCellRenderer()));

		SwingExt.configureTableHeaders(stepsJTable);
		
		// Refresh table when step is updated
		final var selectedStepCtrl = model.getSelectedStep().bind(selection(stepsJTable, stepsTableModel));
		model.getSelectedStep().addListener(l -> {
			if (l.getOldValue() != null) {
				return;
			}
			selectedStepCtrl.pauseBinding();
			final int row = stepsTableModel.getRowOf(model.getSelectedStep().getValue());
			stepsTableModel.fireTableRowsUpdated(row, row + 1);
			selectedStepCtrl.resumeBinding();
		});

		add(new JScrollPane(stepsJTable), BorderLayout.CENTER);

		model.getTestCase().listenActive(tc -> {
			steps.clear();
			steps.setValues(tc.getSteps());
		});

	}

}
