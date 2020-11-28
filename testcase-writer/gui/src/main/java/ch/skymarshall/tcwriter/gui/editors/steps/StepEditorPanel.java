package ch.skymarshall.tcwriter.gui.editors.steps;

import static ch.skymarshall.gui.mvc.factories.BindingDependencies.preserveOnUpdateOf;
import static ch.skymarshall.gui.mvc.factories.Converters.listConverter;
import static ch.skymarshall.gui.swing.factories.SwingBindings.selection;
import static ch.skymarshall.gui.swing.factories.SwingBindings.values;

import java.awt.BorderLayout;
import java.util.Objects;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.mvc.factories.Converters;
import ch.skymarshall.gui.mvc.factories.ObjectTextView;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.factories.SwingBindings;
import ch.skymarshall.tcwriter.generators.model.NamedObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public class StepEditorPanel extends JPanel {

	private final StepEditorModel model;

	public static <T extends NamedObject> IConverter<T, ObjectTextView<T>> converter(final TestDictionary tm) {
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
		model.getPossibleActors().bind(listConverter(converter(td))).bind(values(actorsList));
		model.getActor().bind(converter(td)).bind(selection(actorsList))
				.addDependency(preserveOnUpdateOf(model.getPossibleActors()));
		selectedStep.bind(Converters.wo(Objects::nonNull)).listen(actorsList::setEnabled);
		stepEditors.add(new JScrollPane(actorsList));

		// Actions
		final JList<ObjectTextView<TestAction>> actionsList = new JList<>();
		actionsList.setName("Actions");
		actorsList.setEnabled(false);
		model.getPossibleActions().bind(listConverter(converter(td))).bind(values(actionsList));
		model.getAction().bind(converter(td)).bind(selection(actionsList))
				.addDependency(preserveOnUpdateOf(model.getPossibleActions()));
		selectedStep.bind(Converters.wo(Objects::nonNull)).listen(actionsList::setEnabled);
		stepEditors.add(new JScrollPane(actionsList));

		// Selectors
		final JList<ObjectTextView<TestParameterFactory>> selectorList = new JList<>();
		selectorList.setName("Selectors");
		selectorList.setEnabled(false);
		model.getPossibleSelectors().bind(Converters.listConverter(converter(td)))
				.bind(SwingBindings.values(selectorList));
		model.getSelector().bind(converter(td)).bind(selection(selectorList))
				.addDependency(preserveOnUpdateOf(model.getPossibleSelectors()));
		selectedStep.bind(Converters.wo(Objects::nonNull)).listen(selectorList::setEnabled);
		stepEditors.add(new JScrollPane(selectorList));

		// Action Parameter
		final JList<ObjectTextView<TestParameterFactory>> actionParameterList = new JList<>();
		actionParameterList.setName("Parameters0");
		actionParameterList.setEnabled(false);
		model.getPossibleActionParameters().bind(listConverter(converter(td))).bind(values(actionParameterList));
		model.getActionParameter().bind(converter(td)).bind(selection(actionParameterList))
				.addDependency(preserveOnUpdateOf(model.getPossibleActionParameters()));
		selectedStep.bind(Converters.wo(Objects::nonNull)).listen(actionParameterList::setEnabled);
		stepEditors.add(new JScrollPane(actionParameterList));

		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		add(stepEditors, BorderLayout.CENTER);
	}

}
