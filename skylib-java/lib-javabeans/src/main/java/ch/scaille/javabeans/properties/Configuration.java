package ch.scaille.javabeans.properties;

import static ch.scaille.javabeans.persisters.Persisters.of;
import static ch.scaille.javabeans.persisters.Persisters.persister;

import java.util.function.Consumer;

import ch.scaille.javabeans.AutoCommitListener;
import ch.scaille.javabeans.persisters.IPersisterFactory;
import ch.scaille.javabeans.persisters.IPersisterFactory.IObjectProvider;
import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;

/**
 * To tune the properties.
 * <p>
 * Can be used through static calls, or through instance (such as
 * Properties.of(property).persistent(...)...
 *
 * @author Sebastien Caille
 */
public class Configuration {

	private Configuration() {
	}

	public static <A, P extends AbstractTypedProperty<A>> Consumer<P> persistent(final IPersister<A> persister) {
		return property -> property.setPersister(persister);
	}

	/**
	 * 
	 * @param <T> the type of the persisted bean
	 * @param <A> the type of the persisted attribute
	 * @param <P> the type of the attribute Property
	 */
	public static <T, A, P extends AbstractTypedProperty<A>> Consumer<P> persistent(IObjectProvider<T> object,
			final IPersisterFactory<T, A> persisterFactory) {
		return persistent(persister(object, persisterFactory));
	}

	public static <T, A, P extends AbstractTypedProperty<A>> Consumer<P> persistent(T object,
			final IPersisterFactory<T, A> persisterFactory) {
		return persistent(of(object), persisterFactory);
	}

	public static <U extends AbstractProperty> Consumer<U> errorNotifier(final ErrorNotifier notifier) {
		return property -> property.setErrorNotifier(notifier);
	}

	public static <U extends AbstractProperty> void autoCommit(final U property) {
		property.addListener(new AutoCommitListener());
	}

}
