package ch.scaille.javabeans.properties;

public interface ErrorNotifier {
    void notifyError(Object source, ConversionError error);

    void clearError(Object source, IProperty property);

    static ErrorNotifier emptyErrorNotifier() {
        return new ErrorNotifier() {

            @Override
            public void notifyError(final Object source, final ConversionError error) {
                // noop
            }

            @Override
            public void clearError(final Object source, IProperty property) {
                // noop
            }

        };
    }
}