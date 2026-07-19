package ch.scaille.dataflowmgr.runtime.rx;

import ch.scaille.util.helpers.Logs;
import io.reactivex.rxjava3.core.Maybe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AbstractFlow {

    protected void info(String message) {
        Logs.of(this.getClass().getName()).info(message);
    }

    protected static Predicate<CallExecution<?>> always(){
        return _ -> true;
    }

    protected static Predicate<CallExecution<?>> allActivationNoExclusions() {
        return execution ->
                execution.activations.stream().allMatch(BooleanSupplier::getAsBoolean) &&
                execution.exclusions.stream().noneMatch(BooleanSupplier::getAsBoolean);
    }

    public static class DataPoint<D> {
        protected D output = null;

        public D getOutput() {
            return output;
        }

        public void setOutput(D output) {
            this.output = output;
        }
    }

    protected static class CallExecution<D> extends DataPoint<D> {
        private final String description;
        private final Supplier<D> callExecution;
        private final AtomicReference<DataPointState> state = new AtomicReference<>(DataPointState.IDLE);
        private final Collection<CallExecution<?>> pendingDeps = Collections.synchronizedCollection(new LinkedList<>());
        private final Collection<BooleanSupplier> activations = new ArrayList<>();
        private final Collection<BooleanSupplier> exclusions = new ArrayList<>();
        private final Set<String> qualifiers = new HashSet<>();
        private final Predicate<CallExecution<?>> conditionEvaluation;
        private final List<Consumer<D>> onSuccesses = new ArrayList<>();

        public CallExecution(String description, Set<String> qualifiers, Predicate<CallExecution<?>> conditionEvaluation, Supplier<D> callExecution) {
            this.description = description;
            this.qualifiers.addAll(qualifiers);
            this.conditionEvaluation = conditionEvaluation;
            this.callExecution = callExecution;
        }

        public void addActivations(BooleanSupplier... calls) {
            activations.addAll(List.of(calls));
        }

        public void addExclusions(BooleanSupplier... calls) {
            exclusions.addAll(List.of(calls));
        }

        public void addDependency(CallExecution<?> dep) {
            pendingDeps.add(Objects.requireNonNull(dep));
        }

        public void addOnSuccess(Consumer<D> onSuccess) {
            onSuccesses.add(onSuccess);
        }

        public boolean triggerProcess() {
            if (state.get() != DataPointState.IDLE) {
                return false;
            }
            pendingDeps.removeIf(dep -> dep.state.get() == DataPointState.COMPLETE);
            if (!pendingDeps.isEmpty()) {
                return false;
            }
            return state.compareAndSet(DataPointState.IDLE, DataPointState.TRIGGERING);
        }

        protected void executeCall() {
            output = callExecution.get();
            state.set(DataPointState.COMPLETE);
            onSuccesses.forEach(onSuccess -> onSuccess.accept(output));
        }

        public boolean evaluateCondition() {
            return conditionEvaluation.test(this);
        }

        @Override
        public String toString() {
            return description + qualifiers;
        }

    }

    protected <D> Maybe<CallExecution<D>> triggerProcess(CallExecution<D> execution,
                                                         Runnable... onSuccess) {
       info("triggering " + execution.description);
       return Maybe.just(execution)
               .mapOptional(f -> f.triggerProcess() ? Optional.of(execution):Optional.empty())
               .doOnComplete(() -> onComplete(execution))
               .doOnComplete(() -> onMissingDependencies(execution, execution.evaluateCondition()))
               .doOnSuccess(r -> {
                    if (!r.evaluateCondition()) {
                        execution.state.set(DataPointState.COMPLETE);
                        info(r.description + ": skipped");
                        return;
                    }
                    r.executeCall();
                    info(r.description + ": success");
                })
               .doOnTerminate(() ->
                       Arrays.stream(onSuccess).forEach(Runnable::run));
    }

    private <D> void onComplete(CallExecution<D> execution) {
        if (execution.state.get() == DataPointState.COMPLETE) {
            return;
        }
        info("%s: Cannot execute: deps: %s, trigger: %s, condition: %s"
                .formatted(execution.description, execution.pendingDeps, execution.state, execution.evaluateCondition()));
    }

    protected <D> void onMissingDependencies(CallExecution<D> execution, boolean evaluatedCondition) {
        final var filter = evaluatedCondition ? "ADAPTER" : "CONTROL";

        List.copyOf(execution.pendingDeps).stream()
                .filter(dep -> dep.state.get() == DataPointState.IDLE && dep.qualifiers.contains(filter))
                .forEach(exc -> triggerProcess(exc).subscribe());
    }

}
