package ch.scaille.testing.bdd.definition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ch.scaille.testing.bdd.definition.Steps.Step;
import lombok.Getter;

public class Scenario<A extends AbstractAppTestApi<?>> {

    public static class ExecutionContext<A> {

        private final List<String> report = new ArrayList<>();
		@Getter
        private final A appTestApi;

        public ExecutionContext(A appTestApi) {
            this.appTestApi = appTestApi;
        }

        private void add(String verb, Step<?> step) {
        	var reportLine = verb + step.description.replace("|", "\n  And ");
            if (step.isAutomationStep()) {
                reportLine = Arrays.stream(reportLine.split("\n")).map(s -> "[Automation: " + s + "]").collect(Collectors.joining("\n"));
            }
            report.add(reportLine);
        }

        public void addGiven(Step<?> step) {
            if (report.isEmpty()) {
                add("Given ", step);
            } else {
                add("  And ", step);
            }
        }

        public void addWhen(Step<?> step) {
            add(" When ", step);
        }

        public void addThen(Step<?> step) {
            add(" Then ", step);
        }

        @Override
        public String toString() {
            return String.join("\n", report);
        }

    }

    private final Steps<A>[] steps;

    private Consumer<A> executionConfigurer;

    @SafeVarargs
    public Scenario(Steps<A>... steps) {
        this.steps = steps;
    }

    public Scenario<A> withConfigurer(Consumer<A> executionConfigurer) {
        this.executionConfigurer = executionConfigurer;
        return this;
    }

    @SafeVarargs
    public final Scenario<A> followedBy(Steps<A>... addedSteps) {
    	final var newSteps = Arrays.copyOf(steps, steps.length + addedSteps.length);
        System.arraycopy(addedSteps, 0, newSteps, steps.length, addedSteps.length);
        return new Scenario<>(newSteps);
    }

    public ExecutionContext<A> validate(A appTestApi) {
    	var executionContext = new ExecutionContext<>(appTestApi);
        appTestApi.resetContext();
        if (executionConfigurer != null) {
            executionConfigurer.accept(appTestApi);
        }

        final var lastStep = steps[steps.length - 1];
        for (var step : steps) {
            step.run(executionContext, step == lastStep);
        }
        return executionContext;
    }

}
