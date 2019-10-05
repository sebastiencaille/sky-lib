package ch.skymarshall.tcwriter.gui.editors;

import static ch.skymarshall.tcwriter.generators.Helper.toReference;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.bindings.SwingBindings;

import ch.skymarshall.tcwriter.generators.Helper.VerbatimValue;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.gui.editors.TestParameterValueTableModel.ParameterValue;
import ch.skymarshall.tcwriter.gui.steps.StepsCellEditor;

public class TestParameterValueEditor extends JDialog {

	private final ControllerPropertyChangeSupport support = new ControllerPropertyChangeSupport(this);
	private final JButton ok;
	private TestParameterValue editedParameterValue;
	private final ObjectProperty<VerbatimValue> selectedReferenceProperty;
	private ListModel<ParameterValue> editedParameters;

	public TestParameterValueEditor(final TestCase tc, final TestParameterType parameterType, final VerbatimValue current,
			final TestParameterValue parameterValue) {

		editedParameterValue = parameterValue;

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());

		final List<VerbatimValue> apiReferences = toReference(tc,
				tc.getModel().getParameterFactories().get(parameterType.getType()), ParameterNature.TEST_API_TYPE);
		final JComboBox<VerbatimValue> referenceEditor = StepsCellEditor.prepareFastListEditor(apiReferences);
		selectedReferenceProperty = new ObjectProperty<>("selectedReference", support);
		selectedReferenceProperty.bind(SwingBindings.selection(referenceEditor));
		getContentPane().add(referenceEditor, BorderLayout.NORTH);

		selectedReferenceProperty.addListener(new PropertyChangeListener() {

			private JScrollPane currentTable;

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (currentTable != null) {
					getContentPane().remove(currentTable);
				}

				final VerbatimValue reference = selectedReferenceProperty.getValue();
				if (IdObject.ID_NOT_SET.equals(reference.getId())) {
					return;
				}

				final TestParameterValue testParameterValue = new TestParameterValue(parameterType.getId(),
						tc.getModel().getTestParameterFactory(reference.getId()));
				testParameterValue.getComplexTypeValues().putAll(editedParameterValue.getComplexTypeValues());
				editedParameterValue = testParameterValue;

				final TestParameterDefinition parameter = tc.getModel().getTestParameterFactory(reference.getId());
				editedParameters = TestParameterValueTableModel.toListModel(tc, parameter, editedParameterValue);
				final TestParameterValueTable view = new TestParameterValueTable(
						new TestParameterValueTableModel(editedParameters));
				currentTable = new JScrollPane(view);
				getContentPane().add(currentTable, BorderLayout.CENTER);
				doLayout();
				pack();
			}
		});

		support.attachAll();

		ok = new JButton("OK");
		add(ok, BorderLayout.SOUTH);

		if (current != null) {
			selectedReferenceProperty.setValue(this, current);
		}

		setMinimumSize(new Dimension(300, 100));
		pack();
		setModal(true);

		SwingUtilities.invokeLater(() -> setVisible(true));
	}

	public TestParameterValue committedEditedParameterValue() {
		editedParameterValue.getComplexTypeValues().clear();
		for (final ParameterValue param : editedParameters) {
			if (param.enabled) {
				editedParameterValue.getComplexTypeValues().put(param.id,
						new TestParameterValue(param.id, param.parameterDefinition, param.value));
			}
		}
		return editedParameterValue;
	}

	public void close() {
		setVisible(false);
		dispose();
	}

	public void setOkAction(final ActionListener delegate) {
		ok.addActionListener(delegate);
	}

	public VerbatimValue getCurrentReference() {
		return selectedReferenceProperty.getValue();
	}
}
