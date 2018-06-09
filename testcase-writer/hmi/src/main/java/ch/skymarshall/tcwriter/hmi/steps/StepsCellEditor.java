package ch.skymarshall.tcwriter.hmi.steps;

import static ch.skymarshall.tcwriter.generators.Helper.toReference;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.skymarshall.hmi.swing.model.ListModelTableModel;

import ch.skymarshall.tcwriter.generators.Helper.Reference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestParameter.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.hmi.editors.ReferenceEditor;
import ch.skymarshall.tcwriter.hmi.steps.StepsTableModel.Column;

public class StepsCellEditor extends DefaultCellEditor {

	private final TestCase tc;

	public StepsCellEditor(final TestCase tc) {
		super(new JComboBox<>());
		this.tc = tc;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
			final int row, final int column) {
		final StepsTableModel stepsTableModel = (StepsTableModel) table.getModel();
		final TestStep step = ((ListModelTableModel<TestStep, ?>) table.getModel()).getObjectAtRow(row);
		final Column columnEnum = Column.valueOf(table.getColumnName(column));

		Collection<? extends IdObject> values;
		switch (columnEnum) {
		case ACTOR:
			values = tc.getModel().getActors().values();
			editorComponent = prepareFastListEditor(value,
					toReference(tc.getModel(), values, ParameterNature.TEST_API_TYPE));
			break;
		case ACTION:
			values = step.getRole().getApis();
			editorComponent = prepareFastListEditor(value,
					toReference(tc.getModel(), values, ParameterNature.TEST_API_TYPE));
			break;
		case NAVIGATOR:
			editorComponent = getParamEditor(step, 0, (Reference) value);
			break;
		case PARAM0:
			editorComponent = getParamEditor(step, stepsTableModel.paramIndexOf(step, 0), (Reference) value);
			break;
		default:
			throw new IllegalStateException("Column not handled:" + columnEnum);
		}

		return editorComponent;
	}

	private JComponent getParamEditor(final TestStep step, final int index, final Reference value) {
		final TestParameterType parameter = step.getAction().getParameter(index);
		final List<Reference> refsReferences = toReference(tc.getModel(), tc.getReferences(parameter.getType()),
				ParameterNature.REFERENCE);

		if (!parameter.isSimpleType()) {
			final List<Reference> apiReferences = toReference(tc.getModel(),
					tc.getModel().getParameterFactories().get(parameter.getType()), ParameterNature.TEST_API_TYPE);
			return StepsCellEditor.prepareFastListEditor(value, refsReferences, apiReferences);
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
