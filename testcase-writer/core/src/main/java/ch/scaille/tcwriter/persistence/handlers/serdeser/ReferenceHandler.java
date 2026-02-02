package ch.scaille.tcwriter.persistence.handlers.serdeser;

import java.util.function.Function;

/**
 * @param <M> the model's type (dictionary, testcase, ...)
 * @param <T> the target type
 */
public record ReferenceHandler<M, T>(Class<T> clazz, String propName, Function<T, String> exporter,
                                     Importer<M, T> importer) {


    public boolean matches(Object beanOrClass, String propertyName) {
        return propName.equals(propertyName) &&
                (clazz.isInstance(beanOrClass) ||
                        (beanOrClass instanceof Class<?> otherClazz && clazz.isAssignableFrom(otherClazz)));
    }

    public ExportReference<M, ?> of(Object beanOrClass, String ref) {
        return new ExportReference<>(clazz.cast(beanOrClass), ref, this);
    }

    public interface Importer<M, T> {

        void apply(M model, T object, String reference);

    }

}
