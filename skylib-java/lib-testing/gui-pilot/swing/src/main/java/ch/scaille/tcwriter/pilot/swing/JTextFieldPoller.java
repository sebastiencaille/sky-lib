package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingValue;
import static ch.scaille.tcwriter.pilot.factories.Reporting.settingValue;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.pilot.PollingContext;

@SuppressWarnings("java:S5960")
public class JTextFieldPoller
		extends SwingPollingBuilder<JTextComponent, JTextFieldPoller, JTextFieldPoller.SwingPoller> {

	public static class SwingPoller
			extends ch.scaille.tcwriter.pilot.swing.SwingPollingBuilder.SwingPoller<JTextComponent> {

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

		public void assertTextValue(final String expected) {
			if (expected == null) {
				return;
			}
			configure(polling -> polling.withReportText(checkingValue(expected))).asserted(
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
	public SwingPoller ifNot() {
		return new SwingPoller(this);
	}
	
	/**
	 * Select a value in a list, according to its String representation
	 */
	public void setText(final String value) {
		fail().ifNot().setText(value);
	}

	public void assertTextValue(final String expected) {
		fail().ifNot().assertTextValue(expected);
	}

}
