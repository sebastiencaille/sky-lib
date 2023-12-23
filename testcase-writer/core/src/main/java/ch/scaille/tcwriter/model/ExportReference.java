package ch.scaille.tcwriter.model;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ch.scaille.tcwriter.model.testcase.TestCase;

public class ExportReference extends IdObject {

	private Consumer<TestCase> restoreAction;

	protected ExportReference() {
		super(null);
	}

	public ExportReference(final IdObject idObject) {
		this(idObject.getId());
	}

	public ExportReference(final String exportedId) {
		super(exportedId);
	}

	public void setRestoreAction(final BiConsumer<TestCase, String> restoreAction) {
		this.restoreAction = testCase -> restoreAction.accept(testCase, getId());
	}

	public void restore(final TestCase testCase) {
		if (restoreAction != null) {
			restoreAction.accept(testCase);
		}
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
