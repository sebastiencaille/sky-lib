package ch.skymarshall.tcwriter.hmi;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.model.RootListModel;
import org.skymarshall.hmi.model.views.ListViews;

import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.hmi.steps.StepTable;

public class TCWriter extends JFrame {

	private final TestCase testCase;
	private final ListModel<TestStep> steps = new RootListModel<>(ListViews.sorted(TestStep::getOrdinal));

	public TCWriter(final TestCase tc) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		for (int i = 0; i < tc.getSteps().size(); i++) {
			tc.getSteps().get(i).setOrdinal(i);
		}

		this.getContentPane().setLayout(new BorderLayout());
		this.testCase = tc;
		this.steps.addValues(tc.getSteps());

		this.getContentPane().add(new StepTable(steps, tc), BorderLayout.CENTER);

		this.pack();
		this.setSize(1600, 1200);
	}

}
