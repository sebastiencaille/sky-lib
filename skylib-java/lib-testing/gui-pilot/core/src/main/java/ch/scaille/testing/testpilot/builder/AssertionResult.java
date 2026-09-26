package ch.scaille.testing.testpilot.builder;

public record AssertionResult<C>(AssertionChain<C> then, boolean success) {
}
