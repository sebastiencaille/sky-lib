package ch.scaille.tcwriter.model.dictionary;

public enum ParameterNature {
    SIMPLE_TYPE(true), TEST_API(false), REFERENCE(true), NOT_SET(false);

    private final boolean requiresSimpleValue;

    ParameterNature(final boolean requiresSimpleValue) {
        this.requiresSimpleValue = requiresSimpleValue;
    }

    public boolean isSimpleValue() {
        return requiresSimpleValue;
    }
}