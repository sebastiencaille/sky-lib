package ch.scaille.testing.testpilot.swing;

import static ch.scaille.testing.testpilot.factories.Reporting.checkingValue;
import static ch.scaille.testing.testpilot.factories.Reporting.settingValue;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.junit.jupiter.api.Assertions;

import ch.scaille.testing.testpilot.PollingContext;

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
		public void setText(final String value) {
			if (value == null) {
				return;
			}
			configure(polling -> polling.withReportText(settingValue(value))).applied(t -> {
				t.setText(value);
				if (t instanceof JTextField) {
					SwingHelper.doPressReturn(t);
				}
			});
		}

		public void assertTextEquals(final String expected) {
			if (expected == null) {
				return;
			}
			configure(polling -> polling.withReportText(checkingValue(expected))).assertedCtxt(
					pc -> Assertions.assertEquals(expected, pc.getComponent().getText(), pc.getDescription()));
		}

	}

	public JTextFieldPoller(final SwingPilot pilot, final String name) {
		super(new SwingComponentPilot<>(pilot, JTextComponent.class, name) {

			@Override
			protected boolean canEdit(final PollingContext<JTextComponent> ctxt) {
				return super.canEdit(ctxt) && ctxt.getComponent().isEditable();
			}
		});
	}

	@Override
	protected SwingPoller createPoller() {
		return new SwingPoller(this);
	}
	
}
