package ch.scaille.generators.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public abstract class AbstractGenerator<R> {

	protected static <A extends AbstractGenerator<R>, R> Collector<Class<?>, A, R> toDictionary(
			Supplier<A> generatorSupplier) {
		return new Collector<>() {

			@Override
			public BiConsumer<A, Class<?>> accumulator() {
				return AbstractGenerator::addClass;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return Collections.emptySet();
			}

			@Override
			public BinaryOperator<A> combiner() {
				return (t1, t2) -> {
					t1.classes.addAll(t2.classes);
					return t1;
				};
			}

			@Override
			public Function<A, R> finisher() {
				return AbstractGenerator::generate;
			}

			@Override
			public Supplier<A> supplier() {
				return generatorSupplier;
			}
		};
	}

	protected final Collection<Class<?>> classes;

	protected abstract R generate();

	protected AbstractGenerator() {
		classes = new HashSet<>();
	}

	protected AbstractGenerator(final Class<?>... tcClasses) {
		this.classes = Arrays.asList(tcClasses);
	}

	protected void addClass(Class<?> clazz) {
		classes.add(clazz);
	}

}
