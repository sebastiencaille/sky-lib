package ch.scaille.tcwriter.server.services;

import java.util.List;

import ch.scaille.tcwriter.generators.visitors.HumanReadableVisitor;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestStep;

public class TestCaseService {

	public List<String> computeHumanReadableTexts(TestCase tc, List<TestStep> steps) {
		final var humanReadableVisitor = new HumanReadableVisitor(tc, false);
		return steps.stream().map(humanReadableVisitor::process).toList();
	}

}
