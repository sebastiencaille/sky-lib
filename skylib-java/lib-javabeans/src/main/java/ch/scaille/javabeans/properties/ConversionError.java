package ch.scaille.javabeans.properties;

import org.jspecify.annotations.NullMarked;

/**
 * To report a conversion error
 */
@NullMarked
public record ConversionError(AbstractProperty property, String message, Object content) {

    @Override
    public String toString() {
        return property.getName() + ": " + message;
    }

}