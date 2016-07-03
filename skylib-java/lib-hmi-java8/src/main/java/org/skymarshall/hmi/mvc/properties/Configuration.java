package org.skymarshall.hmi.mvc.properties;

import java.util.function.Consumer;

import org.skymarshall.hmi.mvc.AutoCommitListener;
import org.skymarshall.hmi.mvc.persisters.FieldAccess;
import org.skymarshall.hmi.mvc.persisters.Persisters;
import org.skymarshall.hmi.mvc.properties.AbstractProperty.ErrorNotifier;

/**
 * To tune the properties.
 * <p>
 * Can be used through static calls, or through instance (such as
 * Properties.of(property).persistent(...)...
 *
 * @author Sebastien Caille
 *
 * @param <T>
 * @param <U>
 */
public class Configuration {

	public static <T, U extends AbstractTypedProperty<T>> Consumer<U> persistent(final IPersister<T> persister) {
		return new Consumer<U>() {
			@Override
			public void accept(final U property) {
				property.setPersister(persister);
			}
		};
	}

	public static <T, U extends AbstractTypedProperty<T>> Consumer<U> persistent(final Object object,
			final FieldAccess<T> fieldAccess) {
		return persistent(Persisters.from(object, fieldAccess));
	}

	public static <T, U extends AbstractProperty> Consumer<U> errorNotifier(final ErrorNotifier notifier) {
		return new Consumer<U>() {
			@Override
			public void accept(final U property) {
				property.setErrorNotifier(notifier);
			}
		};

	}

	public static <T, U extends AbstractProperty> void autoCommit(final U property) {
		property.addListener(new AutoCommitListener());
	}

}
