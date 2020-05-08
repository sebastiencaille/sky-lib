package ch.skymarshall.tcwriter.pilot.swing;

import java.util.function.Function;

import javax.swing.JComponent;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;

public class AbstractSwingComponent<T extends JComponent> extends AbstractGuiComponent<T> {

	private final GuiPilot pilot;
	protected final String name;
	protected final Class<T> clazz;

	public AbstractSwingComponent(final GuiPilot pilot, final Class<T> clazz, final String name) {
		super(pilot);
		this.pilot = pilot;
		this.name = name;
		this.clazz = clazz;
	}

	@Override
	protected T loadElement() {
		return pilot.getComponent(name, clazz);
	}

	protected boolean canRead(final T component) {
		return component.isVisible();
	}

	protected boolean canEdit(final T component) {
		return component.isEnabled() && component.isVisible();
	}

	protected <U> U waitComponentEditSuccess(final Function<T, PollingResult<U>> applier,
			final Function<PollingResult<U>, U> onFail) {
		return waitActionSuccess(this::canEdit, applier, pilot.getDefaultActionTimeout(), onFail);
	}

	protected <U> U waitComponentReadActionSuccess(final Function<T, PollingResult<U>> applier,
			final Function<PollingResult<U>, U> onFail) {
		return waitActionSuccess(this::canEdit, applier, pilot.getDefaultActionTimeout(), onFail);
	}

}
