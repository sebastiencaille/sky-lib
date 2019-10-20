package ch.skymarshall.tcwriter.gui.editors.steps;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import ch.skymarshall.gui.mvc.ChainDependencies;
import ch.skymarshall.gui.mvc.converters.Converters;
import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.swing.bindings.SwingBindings;
import ch.skymarshall.tcwriter.generators.model.NamedObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.gui.editors.params.TestParameterValueEditorPanel;

public class StepEditorFrame extends JDialog {

	private final StepEditorController controller;
	private final StepEditorModel model;

	private static class NamedObjectRenderer<T extends NamedObject> {

		private final TestModel tm;
		private final T testObject;

		public NamedObjectRenderer(final TestModel tm, final T testObject) {
			this.tm = tm;
			this.testObject = testObject;
		}

		@Override
		public String toString() {
			if (testObject == null) {
				return "";
			}
			return tm.descriptionOf(testObject).getDescription();
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof NamedObjectRenderer && ((NamedObjectRenderer<?>) obj).testObject.equals(testObject);
		}

		@Override
		public int hashCode() {
			return testObject.getId().hashCode();
		}

	}

	public static <T extends NamedObject> IConverter<T, NamedObjectRenderer<T>> converter(final TestModel tm) {
		return Converters.converter(p -> new NamedObjectRenderer<>(tm, p), c -> StepEditorFrame.<T>from(c));
	}

	public static <U extends NamedObject> U from(final NamedObjectRenderer<U> renderer) {
		if (renderer == null) {
			return null;
		}
		return renderer.testObject;
	}

	public StepEditorFrame(final StepEditorController controller, final TestCase tc) {
		this.controller = controller;
		this.model = controller.getModel();

		final JPanel stepEditors = new JPanel();
		stepEditors.setLayout(new FlowLayout(FlowLayout.LEFT));

		final TestModel tm = tc.getModel();

		final JList<NamedObjectRenderer<TestActor>> actorsList = new JList<>();
		model.getPossibleActors().bind(Converters.listConverter(c -> new NamedObjectRenderer<>(tm, c)))
				.bind(SwingBindings.values(actorsList));
		model.getActor().bind(converter(tm)).bind(SwingBindings.selection(actorsList))
				.addDependency(ChainDependencies.detachOnUpdateOf(model.getPossibleActors()));
		stepEditors.add(new JScrollPane(actorsList));

		final JList<NamedObjectRenderer<TestAction>> actionsList = new JList<>();
		model.getPossibleActions().bind(Converters.listConverter(c -> new NamedObjectRenderer<>(tm, c)))
				.bind(SwingBindings.values(actionsList));
		model.getAction().bind(converter(tm)).bind(SwingBindings.selection(actionsList))
				.addDependency(ChainDependencies.detachOnUpdateOf(model.getPossibleActionParameters()));
		stepEditors.add(new JScrollPane(actionsList));

		final JList<NamedObjectRenderer<TestParameterDefinition>> selectorList = new JList<>();
		model.getPossibleSelectors().bind(Converters.listConverter(c -> new NamedObjectRenderer<>(tm, c)))
				.bind(SwingBindings.values(selectorList));
		model.getSelector().bind(converter(tm)).bind(SwingBindings.selection(selectorList))
				.addDependency(ChainDependencies.detachOnUpdateOf(model.getPossibleSelectors()));
		stepEditors.add(new JScrollPane(selectorList));

		final JList<NamedObjectRenderer<TestParameterDefinition>> actionParameterList = new JList<>();
		model.getPossibleActionParameters().bind(Converters.listConverter(c -> new NamedObjectRenderer<>(tm, c)))
				.bind(SwingBindings.values(actionParameterList));
		model.getActionParameter().bind(converter(tm)).bind(SwingBindings.selection(actionParameterList))
				.addDependency(ChainDependencies.detachOnUpdateOf(model.getPossibleSelectors()));
		stepEditors.add(new JScrollPane(actionParameterList));

		final TestParameterValueEditorPanel selectorEditor = new TestParameterValueEditorPanel(tc, controller,
				model.getSelectorValue());
		final TestParameterValueEditorPanel param0Editor = new TestParameterValueEditorPanel(tc, controller,
				model.getActionParameterValue());

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(stepEditors, BorderLayout.CENTER);

		final JTabbedPane editors = new JTabbedPane();
		editors.add(selectorEditor, "Selector");
		editors.add(param0Editor, "Parameter 0");

		getContentPane().add(editors, BorderLayout.SOUTH);

		validate();
		pack();

	}

}
