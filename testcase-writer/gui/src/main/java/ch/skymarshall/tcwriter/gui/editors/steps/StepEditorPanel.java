package ch.skymarshall.tcwriter.gui.editors.steps;

import static ch.skymarshall.gui.mvc.BindingDependencies.detachOnUpdateOf;
import static ch.skymarshall.gui.mvc.converters.Converters.listConverter;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.selection;
import static ch.skymarshall.gui.swing.bindings.SwingBindings.values;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.skymarshall.gui.mvc.converters.Converters;
import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.swing.bindings.ObjectTextView;
import ch.skymarshall.gui.swing.bindings.SwingBindings;
import ch.skymarshall.tcwriter.generators.model.NamedObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;

public class StepEditorPanel extends JPanel {

	private final StepEditorModel model;

	public static <T extends NamedObject> IConverter<T, ObjectTextView<T>> converter(final TestModel tm) {
		return ObjectTextView.converter(o -> tm.descriptionOf(o).getDescription());
	}

	public StepEditorPanel(final StepEditorController controller) {
		final TestModel tm = controller.getGuiModel().getTestModel();

		this.model = controller.getModel();

		final JButton apply = new JButton("Apply");
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
		model.getPossibleActors().bind(listConverter(converter(tm))).bind(values(actorsList));
		model.getActor().bind(converter(tm)).bind(selection(actorsList))
				.addDependency(detachOnUpdateOf(model.getPossibleActors()));
		stepEditors.add(new JScrollPane(actorsList));

		// Actions
		final JList<ObjectTextView<TestAction>> actionsList = new JList<>();
		model.getPossibleActions().bind(listConverter(converter(tm))).bind(values(actionsList));
		model.getAction().bind(converter(tm)).bind(selection(actionsList))
				.addDependency(detachOnUpdateOf(model.getPossibleActions()));
		stepEditors.add(new JScrollPane(actionsList));

		// Selectors
		final JList<ObjectTextView<TestParameterFactory>> selectorList = new JList<>();
		model.getPossibleSelectors().bind(Converters.listConverter(converter(tm)))
				.bind(SwingBindings.values(selectorList));
		model.getSelector().bind(converter(tm)).bind(selection(selectorList))
				.addDependency(detachOnUpdateOf(model.getPossibleSelectors()));
		stepEditors.add(new JScrollPane(selectorList));

		// Action Parameter
		final JList<ObjectTextView<TestParameterFactory>> actionParameterList = new JList<>();
		model.getPossibleActionParameters().bind(listConverter(converter(tm))).bind(values(actionParameterList));
		model.getActionParameter().bind(converter(tm)).bind(selection(actionParameterList))
				.addDependency(detachOnUpdateOf(model.getPossibleActionParameters()));
		stepEditors.add(new JScrollPane(actionParameterList));

		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		add(stepEditors, BorderLayout.CENTER);
	}

}
