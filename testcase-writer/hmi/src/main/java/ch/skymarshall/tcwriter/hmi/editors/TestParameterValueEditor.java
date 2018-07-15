package ch.skymarshall.tcwriter.hmi.editors;

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

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.swing.bindings.SwingBindings;

import ch.skymarshall.tcwriter.generators.Helper.Reference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameter.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.hmi.editors.TestParameterValueTableModel.ParameterValue;
import ch.skymarshall.tcwriter.hmi.steps.StepsCellEditor;

public class TestParameterValueEditor extends JDialog {

	private final ControllerPropertyChangeSupport support = new ControllerPropertyChangeSupport(this);
	private final JButton ok;
	private TestParameterValue editedParameterValue;
	private final ObjectProperty<Reference> selectedReferenceProperty;
	private ListModel<ParameterValue> editedParameters;

	public TestParameterValueEditor(final TestCase tc, final TestParameterType parameterType, final Reference current,
			final TestParameterValue parameterValue) {

		editedParameterValue = parameterValue;

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());

		final List<Reference> apiReferences = toReference(tc,
				tc.getModel().getParameterFactories().get(parameterType.getType()), ParameterNature.TEST_API_TYPE);
		final JComboBox<Reference> referenceEditor = StepsCellEditor.prepareFastListEditor(apiReferences);
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

				final Reference reference = selectedReferenceProperty.getValue();
				if (IdObject.ID_NOT_SET.equals(reference.getId())) {
					return;
				}

				final TestParameterValue testParameterValue = new TestParameterValue(reference.getId(),
						tc.getModel().getTestParameterFactory(reference.getId()));
				testParameterValue.getComplexTypeValues().putAll(editedParameterValue.getComplexTypeValues());
				editedParameterValue = testParameterValue;

				final TestParameter parameter = tc.getModel().getTestParameterFactory(reference.getId());
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

		selectedReferenceProperty.setValue(this, current);

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

	public Reference getCurrentReference() {
		return selectedReferenceProperty.getValue();
	}
}
