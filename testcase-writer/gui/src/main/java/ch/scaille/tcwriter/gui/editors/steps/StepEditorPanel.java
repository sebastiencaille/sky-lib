package ch.scaille.tcwriter.gui.editors.steps;

import static ch.scaille.gui.mvc.factories.BindingDependencies.preserveOnUpdateOf;
import static ch.scaille.gui.mvc.factories.Converters.listConverter;
import static ch.scaille.gui.mvc.factories.Converters.listen;
import static ch.scaille.gui.swing.factories.SwingBindings.selection;
import static ch.scaille.gui.swing.factories.SwingBindings.values;

import java.awt.BorderLayout;
import java.util.Objects;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.scaille.gui.mvc.converters.IConverter;
import ch.scaille.gui.mvc.factories.Converters;
import ch.scaille.gui.mvc.factories.ObjectTextView;
import ch.scaille.gui.mvc.properties.AbstractTypedProperty;
import ch.scaille.gui.mvc.properties.ObjectProperty;
import ch.scaille.gui.swing.factories.SwingBindings;
import ch.scaille.tcwriter.generators.model.NamedObject;
import ch.scaille.tcwriter.generators.model.testapi.StepClassifier;
import ch.scaille.tcwriter.generators.model.testapi.TestAction;
import ch.scaille.tcwriter.generators.model.testapi.TestActor;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.scaille.tcwriter.generators.model.testcase.TestStep;

public class StepEditorPanel extends JPanel {

	private final StepEditorModel model;

	public static <T extends NamedObject> IConverter<T, ObjectTextView<T>> object2Text(final TestDictionary tm) {
		return ObjectTextView.converter(o -> tm.descriptionOf(o).getDescription());
	}

	public StepEditorPanel(final StepEditorController controller) {
		final TestDictionary td = controller.getGuiModel().getTestDictionary();

		this.model = controller.getModel();
		final ObjectProperty<TestStep> selectedStep = controller.getGuiModel().getSelectedStep();

		final JButton apply = withEnabler(selectedStep, new JButton("Apply"));
		apply.setName("ApplyStep");
		apply.addActionListener(l -> controller.applyChanges());

		final JButton cancel = withEnabler(selectedStep, new JButton("Cancel"));
		cancel.addActionListener(l -> controller.cancelChanges());

		JComboBox<StepClassifier> classifiers = withEnabler(selectedStep, new JComboBox<>(StepClassifier.values()));
		classifiers.setMaximumSize(classifiers.getPreferredSize());
		selectedStep.listen(s -> model.getStepClassifier().setValue(this, (s != null)? s.getClassifier():StepClassifier.ACTION));
		model.getStepClassifier().bind(selection(classifiers)).addDependency(preserveOnUpdateOf(model.getAction()));
		model.getStepClassifier().listen(c -> selectedStep.getValue().setClassifier(c));
		model.getAction().listen(a -> {
			if (a == null) {
				return;
			}
			StepClassifier[] availableClassifiers = a.getAllowedClassifiers();
			if (availableClassifiers.length == 0) {
				availableClassifiers = StepClassifier.values();
			}
			DefaultComboBoxModel<StepClassifier> classifierModel = new DefaultComboBoxModel<>(availableClassifiers);
			classifiers.setModel(classifierModel);
		});

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.add(apply);
		topPanel.add(cancel);
		topPanel.add(classifiers);

		final JPanel stepEditors = new JPanel();
		stepEditors.setLayout(new BoxLayout(stepEditors, BoxLayout.X_AXIS));

		// Actors
		final JList<ObjectTextView<TestActor>> actorsList = withEnabler(selectedStep, new JList<>());
		actorsList.setName("Actors");
		model.getPossibleActors().bind(listConverter(object2Text(td))).bind(values(actorsList));
		model.getActor().bind(object2Text(td)).bind(selection(actorsList))
				.addDependency(preserveOnUpdateOf(model.getPossibleActors()));
		stepEditors.add(new JScrollPane(actorsList));

		// Actions
		final JList<ObjectTextView<TestAction>> actionsList = withEnabler(selectedStep, new JList<>());
		actionsList.setName("Actions");
		model.getPossibleActions().bind(listConverter(object2Text(td))).bind(values(actionsList));
		model.getAction().bind(object2Text(td)).bind(selection(actionsList))
				.addDependency(preserveOnUpdateOf(model.getPossibleActions()));
		stepEditors.add(new JScrollPane(actionsList));

		// Selectors
		final JList<ObjectTextView<TestParameterFactory>> selectorList = withEnabler(selectedStep, new JList<>());
		selectorList.setName("Selectors");
		model.getPossibleSelectors().bind(Converters.listConverter(object2Text(td)))
				.bind(SwingBindings.values(selectorList));
		model.getSelector().bind(object2Text(td)).bind(selection(selectorList))
				.addDependency(preserveOnUpdateOf(model.getPossibleSelectors()));
		stepEditors.add(new JScrollPane(selectorList));

		// Action Parameter
		final JList<ObjectTextView<TestParameterFactory>> actionParameterList = withEnabler(selectedStep,
				new JList<>());
		actionParameterList.setName("Parameters0");
		model.getPossibleActionParameters().bind(listConverter(object2Text(td))).bind(values(actionParameterList));
		model.getActionParameter().bind(object2Text(td)).bind(selection(actionParameterList))
				.addDependency(preserveOnUpdateOf(model.getPossibleActionParameters()));
		stepEditors.add(new JScrollPane(actionParameterList));

		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		add(stepEditors, BorderLayout.CENTER);
	}

	private <T extends JComponent> T withEnabler(AbstractTypedProperty<TestStep> selectedStep, T comp) {
		comp.setEnabled(false);
		selectedStep.bind(listen(Objects::nonNull)).listen(comp::setEnabled);
		return comp;
	}

}
