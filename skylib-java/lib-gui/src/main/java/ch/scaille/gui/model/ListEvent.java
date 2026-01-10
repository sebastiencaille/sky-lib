package ch.scaille.gui.model;

import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Event on dynamic list.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class ListEvent<T> {

	@Getter
    private final ListModelContent<T> source;

	private final List<T> objects;

	public ListEvent(final ListModelContent<T> source) {
		this.source = source;
		objects = null;
	}

	public ListEvent(final ListModelContent<T> source, final T object) {
		this.source = source;
		this.objects = Collections.singletonList(object);
	}

	public ListEvent(final ListModelContent<T> source, final List<T> objects) {
		this.source = source;
		this.objects = objects;
	}

    public Collection<T> getObjects() {
		return objects;
	}

	public T getObject() {
		if (objects.size() > 1) {
			throw new IllegalStateException("Event has more than one object");
		}
		return objects.getFirst();
	}

}
