package ch.scaille.testing.testpilot.swing;

import static ch.scaille.testing.testpilot.factories.Reporting.checkingValue;
import static ch.scaille.testing.testpilot.factories.Reporting.settingValue;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import ch.scaille.testing.testpilot.builder.AssertionResult;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Assertions;

import ch.scaille.testing.testpilot.PolledComponent;

public class JTextFieldAssertionBuilder extends
		SwingAssertionBuilder<JTextComponent, JTextFieldAssertionBuilder, JTextFieldAssertionBuilder.SwingAssertion> {

	public static class SwingAssertion extends SwingAssertionBuilder.SwingAssertion<JTextComponent> {

		protected SwingAssertion(JTextFieldAssertionBuilder builder) {
			super(builder);
		}

		/**
		 * Select a value in a list, according to its String representation
		 */
		public AssertionResult<JTextComponent> setText(final @Nullable String value) {
			if (value == null) {
				return new AssertionResult<>(this, false);
			}
			return configure(polling -> polling.reporting(settingValue(value))).applied(t -> {
				t.setText(value);
				if (t instanceof JTextField) {
					SwingHelper.doPressReturn(t);
				}
			});
		}

		public AssertionResult<JTextComponent> assertTextEquals(final @Nullable String expected) {
			if (expected == null) {
				return new AssertionResult<>(this, false);
			}
			return configure(polling -> polling.reporting(checkingValue(expected)))
					.assertedCtxt(pc -> Assertions.assertEquals(expected, pc.component().getText(), pc.description()));
		}

	}

	public JTextFieldAssertionBuilder(final SwingPilot pilot, final String name) {
		super(new SwingComponentPilot<>(pilot, JTextComponent.class, name) {

			@Override
			protected boolean canEdit(final PolledComponent<JTextComponent> ctxt) {
				return super.canEdit(ctxt) && ctxt.component().isEditable();
			}
		});
	}

	@Override
	protected SwingAssertion createPoller() {
		return new SwingAssertion(this);
	}

}
