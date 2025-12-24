package ch.scaille.testing.testpilot.swing;

import static ch.scaille.testing.testpilot.factories.Reporting.checkingValue;
import static ch.scaille.testing.testpilot.factories.Reporting.settingValue;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Assertions;

import ch.scaille.testing.testpilot.PolledComponent;

@NullMarked
public class JTextFieldPoller
		extends SwingPollingBuilder<JTextComponent, JTextFieldPoller, JTextFieldPoller.SwingPoller> {

	public static class SwingPoller
			extends ch.scaille.testing.testpilot.swing.SwingPollingBuilder.SwingPoller<JTextComponent> {

		protected SwingPoller(JTextFieldPoller builder) {
			super(builder);
		}

		/**
		 * Select a value in a list, according to its String representation
		 */
		public void setText(@Nullable final String value) {
			if (value == null) {
				return;
			}
			configure(polling -> polling.reportText(settingValue(value))).applied(t -> {
				t.setText(value);
				if (t instanceof JTextField) {
					SwingHelper.doPressReturn(t);
				}
			});
		}

		public void assertTextEquals(@Nullable final String expected) {
			if (expected == null) {
				return;
			}
			configure(polling -> polling.reportText(checkingValue(expected))).assertedCtxt(
					pc -> Assertions.assertEquals(expected, pc.component().getText(), pc.description()));
		}

	}

	public JTextFieldPoller(final SwingPilot pilot, final String name) {
		super(new SwingComponentPilot<>(pilot, JTextComponent.class, name) {

			@Override
			protected boolean canEdit(final PolledComponent<JTextComponent> ctxt) {
				return super.canEdit(ctxt) && ctxt.component().isEditable();
			}
		});
	}

	@Override
	protected SwingPoller createPoller() {
		return new SwingPoller(this);
	}
	
}
