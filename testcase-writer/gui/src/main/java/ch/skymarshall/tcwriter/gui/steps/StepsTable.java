package ch.skymarshall.tcwriter.gui.steps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.CellRendererPane;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import ch.skymarshall.gui.model.RootListModel;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.ContributionTableColumn;
import ch.skymarshall.gui.swing.ContributionTableColumnModel;
import ch.skymarshall.gui.swing.bindings.SwingBindings;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.gui.TestRemoteControl;
import ch.skymarshall.tcwriter.gui.editors.steps.StepEditorController;
import ch.skymarshall.tcwriter.gui.steps.StepsTableModel.Column;

public class StepsTable extends JPanel {

	static final Color HUMAN_READABLE_BG_COLOR = new Color(0xDDDDDD);

	private static final TestCase NO_TC = new TestCase();

	static {
		NO_TC.setModel(new TestModel());
	}

	private final StepsTableModel stepsTableModel;

	private final JTable stepsJTable;

	public StepsTable(final ObjectProperty<TestCase> testCaseProperty, final ObjectProperty<TestStep> selectedStep,
			final TestRemoteControl testControl) {

		final ch.skymarshall.gui.model.ListModel<TestStep> steps = new RootListModel<>(
				ListViews.sorted((s1, s2) -> s1.getOrdinal() - s2.getOrdinal()));

		setLayout(new BorderLayout());
		stepsTableModel = new StepsTableModel(testCaseProperty, steps, testControl);

		testControl.setStepListener(stepsTableModel::stepExecutionUpdated);

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
				for (int i = 0; i < getRowCount(); i += 2) {
					final Rectangle toPaint0 = getCellRect(i, 0, true);
					toPaint0.width = getColumnModel().getTotalColumnWidth();
					final Rectangle toPaint = getCellRect(i, 2, true);
					toPaint.width = getColumnModel().getTotalColumnWidth();

					g.setColor(HUMAN_READABLE_BG_COLOR);
					g.setClip(toPaint0.x, toPaint0.y, toPaint0.width, toPaint0.height);
					g.fillRect(toPaint0.x, toPaint0.y, toPaint0.width, toPaint0.height);

					final TableCellRenderer renderer = new DefaultTableCellRenderer() {
						@Override
						public Component getTableCellRendererComponent(final JTable table, final Object value,
								final boolean isSelected, final boolean hasFocus, final int row, final int column) {
							super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
							setBackground(HUMAN_READABLE_BG_COLOR);
							setFont(getFont().deriveFont(Font.ITALIC));
							return this;

						}

					};
					final Component component = prepareRenderer(renderer, i, 2);
					component.setSize(toPaint.getSize());
					component.setPreferredSize(toPaint.getSize());
					toDraw.paintComponent(g, component, this, toPaint.x, toPaint.y, toPaint.width, toPaint.height,
							true);
				}

			}
		};
		final ContributionTableColumnModel<StepsTableModel.Column> columnModel = new ContributionTableColumnModel<>(
				stepsJTable);
		columnModel.install();
		columnModel.configureColumn(
				ContributionTableColumn.fixedColumn(Column.BREAKPOINT, 20, new DefaultTableCellRenderer()));
		columnModel
				.configureColumn(ContributionTableColumn.fixedColumn(Column.STEP, 20, new DefaultTableCellRenderer()));
		columnModel.configureColumn(
				ContributionTableColumn.fixedColumn(Column.ACTOR, 120, new DefaultTableCellRenderer()));
		columnModel
				.configureColumn(ContributionTableColumn.gapColumn(Column.PARAM0, 100, new DefaultTableCellRenderer()));
		columnModel.configureColumn(
				ContributionTableColumn.fixedColumn(Column.TO_VALUE, 150, new DefaultTableCellRenderer()));

		Arrays.stream(Column.values())
				.forEach(c -> stepsJTable.getColumn(c).setCellRenderer(new StepsCellRenderer(testCaseProperty)));

		stepsJTable.getColumn(Column.BREAKPOINT).setCellRenderer(new StepStatusRenderer());
		stepsJTable.getColumn(Column.BREAKPOINT).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		stepsJTable.getColumn(Column.TO_VALUE).setCellEditor(new StepsTextEditor());

		selectedStep.bind(SwingBindings.selection(stepsJTable, stepsTableModel));

		add(new JScrollPane(stepsJTable), BorderLayout.CENTER);

		testCaseProperty.addListener(e -> {
			steps.clear();
			steps.setValues(testCaseProperty.getObjectValue().getSteps());
		});

		stepsJTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					final int row = stepsJTable.rowAtPoint(e.getPoint());
					final StepEditorController editor = new StepEditorController(testCaseProperty.getValue(),
							stepsTableModel.getObjectAtRow(row));
					editor.load();
					editor.activate();
				}
			}
		});
	}

}
