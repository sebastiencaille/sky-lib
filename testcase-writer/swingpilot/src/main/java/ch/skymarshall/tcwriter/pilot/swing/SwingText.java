package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.text.JTextComponent;

public class SwingText extends AbstractSwingComponent<JTextComponent> {

	public SwingText(final GuiPilot pilot, final String name) {
		super(pilot, JTextComponent.class, name);
	}

	@Override
	protected boolean canEdit(final JTextComponent component) {
		return super.canEdit(component) && component.isEditable();
	}

	/**
	 * Select a value in a list, according to it's String representation
	 *
	 * @param componentName
	 * @param value
	 */
	public void setText(final String value) {
		if (value == null) {
			return;
		}
		addReporting(r -> "Set text \'" + value + "\' in " + clazz.getSimpleName() + " " + name);
		waitComponentEditSuccess(action(t -> t.setText(value)), assertFail());
	}

	public void checkTextValue(final String value) {
		if (value == null) {
			return;
		}
		addReporting(r -> "Check text \'" + value + "\' in " + clazz.getSimpleName() + " " + name);
		waitComponentEditSuccess(action(t -> value.equals(t.getText())), assertFail());
	}

}
