package org.skymarshall.hmi.mvc.persisters;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.skymarshall.hmi.mvc.properties.IPersister;

public class GetSetAccess<Container, Attrib> {

	private final Function<Container, Supplier<Attrib>> getter;
	private final Function<Container, Consumer<Attrib>> setter;

	public GetSetAccess(final Function<Container, Supplier<Attrib>> getter,
			final Function<Container, Consumer<Attrib>> setter) {
		this.getter = getter;
		this.setter = setter;

	}

	public IPersister<Attrib> asPersister(final Object object) {
		return new IPersister<Attrib>() {
			@Override
			public Attrib get() {
				return getter.apply((Container) object).get();
			}

			@Override
			public void set(final Attrib value) {
				setter.apply((Container) object).accept(value);
			}
		};
	}

	public static <Container, Attrib> GetSetAccess<Container, Attrib> access(
			final Function<Container, Supplier<Attrib>> getter, final Function<Container, Consumer<Attrib>> setter) {
		return new GetSetAccess<>(getter, setter);
	}

}
