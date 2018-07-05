package ch.skymarshall.tcwriter.generators.model;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;

public class ExportReference extends IdObject {

	private transient Consumer<TestCase> restoreAction;

	protected ExportReference() {
		super(null);
	}

	public ExportReference(final IdObject idObject) {
		super(idObject.getId());
	}

	public ExportReference(final String exportedId) {
		super(exportedId);
	}

	public void setRestoreAction(final BiConsumer<TestCase, String> restoreAction) {
		this.restoreAction = testCase -> restoreAction.accept(testCase, getId());
	}

	public boolean restore(final TestCase testCase) {
		if (restoreAction != null) {
			restoreAction.accept(testCase);
		}
		return restoreAction != null;
	}
}
