package ch.scaille.tcwriter.gui.editors.steps;

import static ch.scaille.gui.mvc.factories.BindingDependencies.preserveOnUpdateOf;
import static ch.scaille.gui.mvc.factories.Converters.listConverter;
import static ch.scaille.gui.swing.factories.SwingBindings.selection;
import static ch.scaille.gui.swing.factories.SwingBindings.values;

import java.awt.BorderLayout;
import java.util.Objects;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.scaille.gui.mvc.converters.IConverter;
import ch.scaille.gui.mvc.factories.Converters;
import ch.scaille.gui.mvc.factories.ObjectTextView;
import ch.scaille.gui.mvc.properties.ObjectProperty;
import ch.scaille.gui.swing.factories.SwingBindings;
import ch.scaille.tcwriter.generators.model.NamedObject;
import ch.scaille.tcwriter.generators.model.testapi.TestAction;
import ch.scaille.tcwriter.generators.model.testapi.TestActor;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.scaille.tcwriter.generators.model.testcase.TestStep;

public class StepEditorPanel extends JPanel {

	private final StepEditorModel model;

	public static <T extends NamedObject> IConverter<T, ObjectTextView<T>> objectTextConverter(final TestDictionary tm) {
		return ObjectTextView.converter(o -> tm.descriptionOf(o).getDescription());
	}

	public StepEditorPanel(final StepEditorController controller) {
		final TestDictionary td = controller.getGuiModel().getTestDictionary();

		this.model = controller.getModel();
		final ObjectProperty<TestStep> selectedStep = controller.getGuiModel().getSelectedStep();

		final JButton apply = new JButton("Apply");
		apply.setName("ApplyStep");
		apply.addActionListener(l -> controller.applyChanges());
		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(l -> controller.cancelChanges());

		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.add(apply);
		topPanel.add(cancel);

		final JPanel stepEditors = new JPanel();
		stepEditors.setLayout(new BoxLayout(stepEditors, BoxLayout.X_AXIS));

		// Actors
		final JList<ObjectTextView<TestActor>> actorsList = new JList<>();
		actorsList.setName("Actors");
		actorsList.setEnabled(false);
		model.getPossibleActors().bind(listConverter(objectTextConverter(td))).bind(values(actorsList));
		model.getActor().bind(objectTextConverter(td)).bind(selection(actorsList))
				.addDependency(preserveOnUpdateOf(model.getPossibleActors()));
		selectedStep.bind(Converters.wo(Objects::nonNull)).listen(actorsList::setEnabled);
		stepEditors.add(new JScrollPane(actorsList));

		// Actions
		final JList<ObjectTextView<TestAction>> actionsList = new JList<>();
		actionsList.setName("Actions");
		actorsList.setEnabled(false);
		model.getPossibleActions().bind(listConverter(objectTextConverter(td))).bind(values(actionsList));
		model.getAction().bind(objectTextConverter(td)).bind(selection(actionsList))
				.addDependency(preserveOnUpdateOf(model.getPossibleActions()));
		selectedStep.bind(Converters.wo(Objects::nonNull)).listen(actionsList::setEnabled);
		stepEditors.add(new JScrollPane(actionsList));

		// Selectors
		final JList<ObjectTextView<TestParameterFactory>> selectorList = new JList<>();
		selectorList.setName("Selectors");
		selectorList.setEnabled(false);
		model.getPossibleSelectors().bind(Converters.listConverter(objectTextConverter(td)))
				.bind(SwingBindings.values(selectorList));
		model.getSelector().bind(objectTextConverter(td)).bind(selection(selectorList))
				.addDependency(preserveOnUpdateOf(model.getPossibleSelectors()));
		selectedStep.bind(Converters.wo(Objects::nonNull)).listen(selectorList::setEnabled);
		stepEditors.add(new JScrollPane(selectorList));

		// Action Parameter
		final JList<ObjectTextView<TestParameterFactory>> actionParameterList = new JList<>();
		actionParameterList.setName("Parameters0");
		actionParameterList.setEnabled(false);
		model.getPossibleActionParameters().bind(listConverter(objectTextConverter(td))).bind(values(actionParameterList));
		model.getActionParameter().bind(objectTextConverter(td)).bind(selection(actionParameterList))
				.addDependency(preserveOnUpdateOf(model.getPossibleActionParameters()));
		selectedStep.bind(Converters.wo(Objects::nonNull)).listen(actionParameterList::setEnabled);
		stepEditors.add(new JScrollPane(actionParameterList));

		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		add(stepEditors, BorderLayout.CENTER);
	}

}
