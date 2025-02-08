package ch.scaille.tcwriter.gui.frame;

import static ch.scaille.gui.mvc.factories.ComponentBindings.listen;

import java.awt.BorderLayout;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import ch.scaille.gui.swing.SwingExt;
import ch.scaille.javabeans.DependenciesBuildingReport;
import ch.scaille.tcwriter.gui.editors.params.TestParameterModel;
import ch.scaille.tcwriter.gui.editors.params.TestParameterValueEditorPanel;
import ch.scaille.tcwriter.gui.editors.steps.StepEditorController;
import ch.scaille.tcwriter.gui.editors.steps.StepEditorPanel;
import ch.scaille.tcwriter.gui.steps.StepsTable;
import ch.scaille.util.helpers.LambdaExt;
import ch.scaille.util.helpers.LambdaExt.RunnableWithException;
import ch.scaille.util.helpers.Logs;

public class TCWriterGui extends JFrame {

	private static final Logger LOGGER = Logs.of(TCWriterGui.class);

	private final TCWriterController controller;

	private TestParameterModel selectorModel;

	private TestParameterModel param0Model;

	public TCWriterGui(final TCWriterController controller) {
		this.controller = controller;
		setName("TCWriterGui");
	}

	private JButton button(final String name, final ImageIcon icon, final String toolTip,
			RunnableWithException<?> action) {
		final var newButton = new JButton(icon);
		newButton.setToolTipText(toolTip);
		newButton.addActionListener(SwingExt.action(LambdaExt.uncheckedR(action, this::handleException)));
		newButton.setName(name);
		return newButton;
	}

	private JButton button(final ImageIcon icon, final String toolTip, final RunnableWithException<?> action) {
		return button(null, icon, toolTip, action);
	}

	public void build() {

		final var screenBuildingReport = new DependenciesBuildingReport();
		DependenciesBuildingReport.setScreenBuildingReport(screenBuildingReport);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.getContentPane().setLayout(new BorderLayout());

		final var editConfigButton = button(icon("general/Information24"), "Edit configuration",
				controller::editConfig);

		final var newTCButton = button("NewTC", icon("general/New24"), "New test case", controller::newTestCase);
		final var loadButton = button("LoadTC", icon("general/Open24"), "Open test case", controller::loadTestCase);
		final var saveButton = button("SaveTC", icon("general/Save24"), "Save test case", controller::save);

		final var importDictionaryButton = button(icon("general/Import24"), "Import dictionary",
				controller::importDictionary);

		final var generateButton = button(icon("general/Export24"), "Export to Java", controller::generateCode);

		final var runButton = button(icon("media/Play24"), "Start execution", controller::startTestCase);
		controller.getModel().getExecutionState().bind(s -> s == TCWriterModel.TestExecutionState.STOPPED)
				.bind(listen(runButton::setEnabled));

		final var continueButton = button(icon("media/StepForward24"), "Continue execution",
				controller::resumeTestCase);
		continueButton.setEnabled(false);
		controller.getModel().getExecutionState().bind(s -> s == TCWriterModel.TestExecutionState.PAUSED)
				.bind(listen(continueButton::setEnabled));

		final var sep = new JSeparator(SwingConstants.VERTICAL);

		final var addStepButton = button("AddStep", icon("table/RowInsertAfter24"), "Add step", controller::addStep);

		final var removeStepButton = button(icon("table/RowDelete24"), "Remove step", controller::removeStep);

		final var stepsTable = new StepsTable(controller);

		final var buttonsBar = new JToolBar();
		buttonsBar.add(editConfigButton);
		buttonsBar.add(newTCButton);
		buttonsBar.add(loadButton);
		buttonsBar.add(saveButton);
		buttonsBar.add(importDictionaryButton);
		buttonsBar.add(generateButton);
		buttonsBar.add(runButton);
		buttonsBar.add(continueButton);
		buttonsBar.add(sep);
		buttonsBar.add(addStepButton);
		buttonsBar.add(removeStepButton);
		this.getContentPane().add(buttonsBar, BorderLayout.NORTH);

		final var stepEditorController = new StepEditorController(controller, controller.getModel().getTestDictionary());
		final var stepEditorModel = stepEditorController.getModel();
		final var stepEditor = new StepEditorPanel(stepEditorController);
		stepEditorController.build();

		selectorModel = new TestParameterModel("selector", controller, stepEditorModel.getSelector(),
				stepEditorModel.getSelectorValue());
		final var selectorEditor = new TestParameterValueEditorPanel(controller, selectorModel);

		param0Model = new TestParameterModel("param0", controller, stepEditorModel.getActionParameter(),
				stepEditorModel.getActionParameterValue());
		final var param0Editor = new TestParameterValueEditorPanel(controller, param0Model);

		final var paramsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(selectorEditor),
				new JScrollPane(param0Editor));

		final var stepsPane = new JScrollPane(stepsTable);
		final var stepPane = new JScrollPane(stepEditor);
		final var paramPane = new JScrollPane(paramsPane);

		final int height = 1200;

		final var topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, stepsPane, stepPane);
		final var bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, paramPane);
		topSplit.setDividerLocation(height / 3);
		bottomSplit.setDividerLocation(height / 2);

		this.getContentPane().add(bottomSplit, BorderLayout.CENTER);
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine(screenBuildingReport.toString());
		}
		this.validate();
		this.pack();
		this.setSize(1600, height);
	}

	public void start() {
		selectorModel.activate();
		param0Model.activate();

		this.setVisible(true);
	}

	private ImageIcon icon(final String name) {
		final var iconRsrcName = "toolbarButtonGraphics/" + name + ".gif";
		final var iconRsrc = Thread.currentThread().getContextClassLoader().getResource(iconRsrcName);
		if (iconRsrc == null) {
			throw new IllegalStateException("Unable to find resource " + iconRsrcName);
		}
		return new ImageIcon(iconRsrc);
	}

	protected void handleException(final Exception ex) {
		ExceptionHelper.handleException(this, ex);
	}

}
