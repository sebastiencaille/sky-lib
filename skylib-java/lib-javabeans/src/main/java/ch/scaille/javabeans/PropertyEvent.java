package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * Event fired when the property is modified.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 */
public class PropertyEvent {

	public enum EventKind {
		BEFORE(98), AFTER(99);

		private final int tableModelId;

		EventKind(final int tableModelId) {
			this.tableModelId = tableModelId;
		}

		public int getTableModelId() {
			return tableModelId;
		}
	}

	private final EventKind kind;
	private final AbstractProperty property;

	public PropertyEvent(final EventKind kind, final AbstractProperty property) {
		this.kind = kind;
		this.property = property;
	}

	public EventKind getKind() {
		return kind;
	}

	public AbstractProperty getProperty() {
		return property;
	}

}
