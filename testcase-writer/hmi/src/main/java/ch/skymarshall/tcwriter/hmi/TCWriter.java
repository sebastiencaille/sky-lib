package ch.skymarshall.tcwriter.hmi;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.RootListModel;
import org.skymarshall.hmi.model.views.ListViews;

import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.hmi.steps.StepTable;

public abstract class TCWriter extends JFrame {

	private final ListModel<TestStep> steps = new RootListModel<>(ListViews.sorted(TestStep::getOrdinal));

	public abstract void generateCode(TestCase tc) throws TestCaseException;

	public TCWriter(final TestCase tc) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		for (int i = 0; i < tc.getSteps().size(); i++) {
			tc.getSteps().get(i).setOrdinal(i);
		}

		this.getContentPane().setLayout(new BorderLayout());
		this.steps.addValues(tc.getSteps());

		final JButton generateButton = new JButton("Generate");
		generateButton.addActionListener(e -> {
			try {
				generateCode(tc);
			} catch (final TestCaseException e1) {
				throw new IllegalStateException("Unable to generate test case", e1);
			}
		});

		this.getContentPane().add(new StepTable(steps, tc), BorderLayout.CENTER);
		this.getContentPane().add(generateButton, BorderLayout.SOUTH);

		this.pack();
		this.setSize(1600, 1200);
	}

}
