package ch.scaille.testing.testpilot.swing;

import org.assertj.core.api.ObjectAssert;

import javax.swing.AbstractButton;
import java.util.function.Consumer;
import java.util.function.Function;

public class AssertjAsserts {

    public static <C> Consumer<ObjectAssert<C>> textEquals(String text, Function<C, String> textExtractor) {
        return assertj -> assertj
                .as("text equals '" + text + "'")
                .returns(text, textExtractor);
    }

    public static Consumer<ObjectAssert<AbstractButton>> clicked() {
        return assertj -> assertj
                .as("clicked")
                .satisfies(AbstractButton::doClick);


    }
}
