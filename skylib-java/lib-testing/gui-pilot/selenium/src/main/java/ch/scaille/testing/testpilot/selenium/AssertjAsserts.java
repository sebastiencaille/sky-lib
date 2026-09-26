package ch.scaille.testing.testpilot.selenium;

import org.assertj.core.api.ObjectAssert;
import org.openqa.selenium.WebElement;

import java.util.function.Consumer;

public class AssertjAsserts {

    public static Consumer<ObjectAssert<WebElement>> textEquals(String text) {
        return assertj -> assertj
                .as("text equals '" + text + "'")
                .returns(text, WebElement::getText);
    }

    public static Consumer<ObjectAssert<WebElement>> clicked() {
        return assertj -> assertj
                .as("clicked")
                .satisfies(WebElement::click);


    }
}
