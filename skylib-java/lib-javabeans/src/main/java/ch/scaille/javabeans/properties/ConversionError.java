package ch.scaille.javabeans.properties;

/**
 * To report a conversion error
 */

public record ConversionError(AbstractProperty property, String message, Object content) {

    @Override
    public String toString() {
        return property.getName() + ": " + message;
    }

}