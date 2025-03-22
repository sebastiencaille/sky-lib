package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * Event fired when the property is modified.
 * <p>
 *
 * @author Sebastien Caille
 */
public record PropertyEvent(ch.scaille.javabeans.PropertyEvent.EventKind kind, AbstractProperty property) {

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


}
