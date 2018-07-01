package ch.skymarshall.tcwriter.hmi.steps;

import static ch.skymarshall.tcwriter.generators.Helper.toReference;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.Helper.Reference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameter.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.hmi.editors.ReferenceEditor;
import ch.skymarshall.tcwriter.hmi.steps.StepsTableModel.Column;

public class StepsCellEditor extends DefaultCellEditor {

	private final ObjectProperty<TestCase> testCaseProperty;

	public StepsCellEditor(final ObjectProperty<TestCase> testCaseProperty) {
		super(new JComboBox<>());
		this.testCaseProperty = testCaseProperty;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
			final int row, final int column) {
		final StepsTableModel stepsTableModel = (StepsTableModel) table.getModel();
		final TestStep step = ((ListModelTableModel<TestStep, ?>) table.getModel()).getObjectAtRow(row);
		final Column columnEnum = Column.valueOf(table.getColumnName(column));

		final TestCase tc = testCaseProperty.getValue();

		Collection<? extends IdObject> values;
		switch (columnEnum) {
		case ACTOR:
			values = tc.getModel().getActors().values();
			editorComponent = prepareFastListEditor(value, toReference(tc, values, ParameterNature.TEST_API_TYPE));
			break;
		case ACTION:
			values = step.getRole().getApis();
			editorComponent = prepareFastListEditor(value, toReference(tc, values, ParameterNature.TEST_API_TYPE));
			break;
		case NAVIGATOR:
			editorComponent = getParamEditor(tc, step, 0, (Reference) value);
			break;
		case PARAM0:
			editorComponent = getParamEditor(tc, step, stepsTableModel.paramIndexOf(tc, step, 0), (Reference) value);
			break;
		default:
			throw new IllegalStateException("Column not handled:" + columnEnum);
		}

		return editorComponent;
	}

	private static final Set<String> SIMPLE_TYPES = new HashSet<>(
			Arrays.asList(Integer.TYPE.getName(), Integer.class.getName(), String.class.getName()));

	private JComponent getParamEditor(final TestCase tc, final TestStep step, final int index, final Reference value) {
		final TestParameterType parameter = step.getAction().getParameter(index);
		final List<Reference> refsReferences = toReference(tc, tc.getReferences(parameter.getType()),
				ParameterNature.REFERENCE);

		if (!SIMPLE_TYPES.contains(parameter.getType())) {
			final List<Reference> apiReferences = toReference(tc,
					tc.getModel().getParameterFactories().get(parameter.getType()), ParameterNature.TEST_API_TYPE);
			final JComboBox<Reference> fastListEditor = StepsCellEditor.prepareFastListEditor(value, refsReferences,
					apiReferences);
			fastListEditor.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
			delegate = new EditorDelegate() {
				@Override
				public void setValue(final Object value) {
					fastListEditor.setSelectedItem(value);
				}

				@Override
				public Object getCellEditorValue() {
					return fastListEditor.getSelectedItem();
				}

				@Override
				public boolean shouldSelectCell(final EventObject anEvent) {
					if (anEvent instanceof MouseEvent) {
						final MouseEvent e = (MouseEvent) anEvent;
						return e.getID() != MouseEvent.MOUSE_DRAGGED;
					}
					return true;
				}

				@Override
				public boolean stopCellEditing() {
					if (fastListEditor.isEditable()) {
						// Commit edited value.
						fastListEditor.actionPerformed(new ActionEvent(this, 0, ""));
					}
					return super.stopCellEditing();
				}
			};
			fastListEditor.addActionListener(delegate);
			return fastListEditor;
		}
		final ReferenceEditor editor = new ReferenceEditor(refsReferences, value);
		delegate = new EditorDelegate() {

			@Override
			public Object getCellEditorValue() {
				return editor.getValue();
			}

			@Override
			public void actionPerformed(final ActionEvent e) {
				super.actionPerformed(e);
				editor.close();
			}

		};
		editor.setOkAction(delegate);
		return new JPanel();
	}

	public static JComboBox<Reference> prepareFastListEditor(final Object value, final List<Reference>... references) {
		final JComboBox<Reference> cb = new JComboBox<>(Arrays.stream(references).flatMap(Collection::stream)
				.collect(Collectors.toList()).toArray(new Reference[0]));
		cb.setSelectedItem(value);
		return cb;
	}

}
