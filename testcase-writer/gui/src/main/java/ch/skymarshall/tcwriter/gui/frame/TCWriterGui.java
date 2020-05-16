package ch.skymarshall.tcwriter.gui.frame;

import static ch.skymarshall.gui.mvc.factories.ComponentBindings.wo;
import static ch.skymarshall.gui.swing.SwingHelper.withException;

import java.awt.BorderLayout;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import ch.skymarshall.gui.mvc.ScreenBuildingReport;
import ch.skymarshall.gui.swing.SwingHelper.ActionWithException;
import ch.skymarshall.tcwriter.gui.editors.params.TestParameterModel;
import ch.skymarshall.tcwriter.gui.editors.params.TestParameterValueEditorPanel;
import ch.skymarshall.tcwriter.gui.editors.steps.StepEditorController;
import ch.skymarshall.tcwriter.gui.editors.steps.StepEditorModel;
import ch.skymarshall.tcwriter.gui.editors.steps.StepEditorPanel;
import ch.skymarshall.tcwriter.gui.steps.StepsTable;

public class TCWriterGui extends JFrame {

	private static final Logger LOGGER = Logger.getLogger(TCWriterGui.class.getName());

	private final TCWriterController controller;

	public TCWriterGui(final TCWriterController controller) {
		this.controller = controller;
		setName("TCWriterGui");
	}

	private JButton button(final String name, final ImageIcon icon, final String toolTip,
			final ActionWithException<?> action) {
		final JButton createdButton = new JButton(icon);
		createdButton.setToolTipText(toolTip);
		createdButton.addActionListener(e -> withException(action, this::handleException));
		createdButton.setName(name);
		return createdButton;
	}

	private JButton button(final ImageIcon icon, final String toolTip, final ActionWithException<?> action) {
		return button(null, icon, toolTip, action);
	}

	public void run() {

		final ScreenBuildingReport screenBuildingReport = new ScreenBuildingReport();
		ScreenBuildingReport.setScreenBuildingReport(screenBuildingReport);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.getContentPane().setLayout(new BorderLayout());

		final JButton editConfigButton = button(icon("general/Information24"), "Edit configuration",
				controller::editConfig);

		final JButton newTCButton = button("NewTC", icon("general/New24"), "New test case", controller::newTestCase);

		final JButton loadButton = button("LoadTC", icon("general/Open24"), "Open test case", controller::loadTestCase);

		final JButton saveButton = button("SaveTC", icon("general/Save24"), "Save test case", controller::save);

		final JButton generateButton = button(icon("general/Export24"), "Export to Java", controller::generateCode);

		final JButton runButton = button(icon("media/Play24"), "Start execution", controller::startTestCase);
		controller.getModel().getExecutionState().bind(s -> s == TCWriterModel.TestExecutionState.STOPPED)
				.bind(wo(runButton::setEnabled));

		final JButton continueButton = button(icon("media/StepForward24"), "Continue execution",
				controller::resumeTestCase);
		continueButton.setEnabled(false);
		controller.getModel().getExecutionState().bind(s -> s == TCWriterModel.TestExecutionState.PAUSED)
				.bind(wo(continueButton::setEnabled));

		final JSeparator sep = new JSeparator(SwingConstants.VERTICAL);

		final JButton addStepButton = button("AddStep", icon("table/RowInsertAfter24"), "Add step",
				controller::addStep);

		final JButton removeStepButton = button(icon("table/RowDelete24"), "Remove step", controller::removeStep);

		final StepsTable stepsTable = new StepsTable(controller);

		final JToolBar buttons = new JToolBar();
		buttons.add(editConfigButton);
		buttons.add(newTCButton);
		buttons.add(loadButton);
		buttons.add(saveButton);
		buttons.add(generateButton);
		buttons.add(runButton);
		buttons.add(continueButton);
		buttons.add(sep);
		buttons.add(addStepButton);
		buttons.add(removeStepButton);
		this.getContentPane().add(buttons, BorderLayout.NORTH);

		final StepEditorController stepEditorController = new StepEditorController(controller);
		final StepEditorModel stepEditorModel = stepEditorController.getModel();
		final StepEditorPanel stepEditor = new StepEditorPanel(stepEditorController);
		stepEditorController.load();

		final TestParameterModel selectorModel = new TestParameterModel("selector", controller,
				stepEditorModel.getSelector(), stepEditorModel.getSelectorValue());
		final JComponent selectorEditor = new TestParameterValueEditorPanel(controller, controller.getModel(),
				selectorModel);

		final TestParameterModel param0Model = new TestParameterModel("param0", controller,
				stepEditorModel.getActionParameter(), stepEditorModel.getActionParameterValue());
		final JComponent param0Editor = new TestParameterValueEditorPanel(controller, controller.getModel(),
				param0Model);

		final JSplitPane paramsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(selectorEditor),
				new JScrollPane(param0Editor));

		final JScrollPane stepsPane = new JScrollPane(stepsTable);
		final JScrollPane stepPane = new JScrollPane(stepEditor);
		final JScrollPane paramPane = new JScrollPane(paramsPane);

		final int height = 1200;

		final JSplitPane topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, stepsPane, stepPane);
		final JSplitPane bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, paramPane);
		topSplit.setDividerLocation(height / 3);
		bottomSplit.setDividerLocation(height / 2);

		this.getContentPane().add(bottomSplit, BorderLayout.CENTER);
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine(screenBuildingReport.toString());
		}

		this.validate();
		this.pack();
		this.setSize(1600, height);
		this.setVisible(true);
	}

	private ImageIcon icon(final String name) {
		final String resourceName = "toolbarButtonGraphics/" + name + ".gif";
		final URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
		if (resource == null) {
			throw new IllegalStateException("Unable to find resource " + resourceName);
		}
		return new ImageIcon(resource);
	}

	public void handleException(final Exception ex) {
		LOGGER.log(Level.WARNING, "Unable to start testcase", ex);
		JOptionPane.showMessageDialog(this, "Unable to execution action: " + ex.getMessage());
	}
}
