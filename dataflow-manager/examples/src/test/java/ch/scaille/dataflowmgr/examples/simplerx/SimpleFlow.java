// File generated from template 2022/02/13 10:15:47
package ch.scaille.dataflowmgr.examples.simplerx;

import ch.scaille.dataflowmgr.examples.simple.FlowReport;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



class SimpleFlow extends ch.scaille.dataflowmgr.examples.simple.AbstractFlow {

	public enum DataPointState {
		NOT_TRIGGERED, TRIGGERING, TRIGGERED, SKIPPED
	}

	public class FlowExecution {
	
	    private final java.lang.String inputDataPoint;
		
		public FlowExecution(java.lang.String inputDataPoint) {
			this.inputDataPoint = inputDataPoint;
		}

		private DataPointState state_binding_simpleService_init_simpleService_init = DataPointState.NOT_TRIGGERED;
		private synchronized void setStateBindingSimpleServiceInitSimpleServiceInit(DataPointState state_binding_simpleService_init_simpleService_init) {
		    this.state_binding_simpleService_init_simpleService_init = state_binding_simpleService_init_simpleService_init;
		}
		private synchronized boolean canTriggerBindingSimpleServiceInitSimpleServiceInit() {
		    if (this.state_binding_simpleService_init_simpleService_init == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_simpleService_init_simpleService_init = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_simpleService_init = DataPointState.NOT_TRIGGERED;
		private void setStateSimpleServiceInit(DataPointState state_simpleService_init) {
		    this.state_simpleService_init = state_simpleService_init;
		}
		private ch.scaille.dataflowmgr.examples.simple.dto.MyData simpleService_init;
		private void setSimpleServiceInit(ch.scaille.dataflowmgr.examples.simple.dto.MyData simpleService_init) {
		    this.simpleService_init = simpleService_init;
		    this.state_simpleService_init = DataPointState.TRIGGERED;
		}
		
		private DataPointState state_binding_simpleService_complete_complete = DataPointState.NOT_TRIGGERED;
		private synchronized void setStateBindingSimpleServiceCompleteComplete(DataPointState state_binding_simpleService_complete_complete) {
		    this.state_binding_simpleService_complete_complete = state_binding_simpleService_complete_complete;
		}
		private synchronized boolean canTriggerBindingSimpleServiceCompleteComplete() {
		    if (this.state_binding_simpleService_complete_complete == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_simpleService_complete_complete = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_complete = DataPointState.NOT_TRIGGERED;
		private void setStateComplete(DataPointState state_complete) {
		    this.state_complete = state_complete;
		}
		private ch.scaille.dataflowmgr.examples.simple.dto.MyData complete;
		private void setComplete(ch.scaille.dataflowmgr.examples.simple.dto.MyData complete) {
		    this.complete = complete;
		    this.state_complete = DataPointState.TRIGGERED;
		}
		
		private java.lang.String simpleExternalAdapter_getCompletionsimpleService_complete_complete;
		private void setSimpleExternalAdapterGetCompletionsimpleServiceCompleteComplete(java.lang.String simpleExternalAdapter_getCompletionsimpleService_complete_complete) {
		    this.simpleExternalAdapter_getCompletionsimpleService_complete_complete = simpleExternalAdapter_getCompletionsimpleService_complete_complete;
		}
		
		private DataPointState state_binding_simpleService_keepAsIs_complete = DataPointState.NOT_TRIGGERED;
		private synchronized void setStateBindingSimpleServiceKeepAsIsComplete(DataPointState state_binding_simpleService_keepAsIs_complete) {
		    this.state_binding_simpleService_keepAsIs_complete = state_binding_simpleService_keepAsIs_complete;
		}
		private synchronized boolean canTriggerBindingSimpleServiceKeepAsIsComplete() {
		    if (this.state_binding_simpleService_keepAsIs_complete == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_simpleService_keepAsIs_complete = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_binding_exit_exit = DataPointState.NOT_TRIGGERED;
		private synchronized void setStateBindingExitExit(DataPointState state_binding_exit_exit) {
		    this.state_binding_exit_exit = state_binding_exit_exit;
		}
		private synchronized boolean canTriggerBindingExitExit() {
		    if (this.state_binding_exit_exit == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_exit_exit = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		
	}

	// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
	private Maybe<FlowExecution> simpleService_init_simpleService_init_svcCall(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingSimpleServiceInitSimpleServiceInit(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingSimpleServiceInitSimpleServiceInit(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setSimpleServiceInit(this.simpleService.init(f.inputDataPoint)))
	        .doOnSuccess(r -> info("inputDataPoint -> simpleService.init -> simpleService_init: Call success"))
	        .doOnComplete(() -> info("inputDataPoint -> simpleService.init -> simpleService_init: Call skipped"))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService = callService.subscribeOn(Schedulers.computation());
	    return callService;
	}
	
	private Maybe<FlowExecution> binding_simpleService_init_simpleService_init(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<FlowExecution> topCall = simpleService_init_simpleService_init_svcCall(execution, callModifier, callbacks);
	    return Maybe.just(execution)
	        .mapOptional(f -> f.canTriggerBindingSimpleServiceInitSimpleServiceInit()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> info("inputDataPoint -> simpleService.init -> simpleService_init: Deps success"))
	        .doOnComplete(() -> info("inputDataPoint -> simpleService.init -> simpleService_init: Deps skipping"))
	        .doOnSuccess(r -> topCall.subscribe());
	}
	
	// ------------------------- simpleService_init -> simpleService.complete -> complete -------------------------
	private Maybe<FlowExecution> simpleService_complete_complete_svcCall(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_getCompletion = Maybe.just(execution)
	        .map(f -> this.simpleExternalAdapter.getCompletion(f.simpleService_init))
	        .doOnSuccess(execution::setSimpleExternalAdapterGetCompletionsimpleServiceCompleteComplete)
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingSimpleServiceCompleteComplete(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingSimpleServiceCompleteComplete(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setComplete(this.simpleService.complete(f.simpleService_init,f.simpleExternalAdapter_getCompletionsimpleService_complete_complete)))
	        .doOnSuccess(r -> info("simpleService_init -> simpleService.complete -> complete: Call success"))
	        .doOnComplete(() -> info("simpleService_init -> simpleService.complete -> complete: Call skipped"))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService = callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    return Maybe.just(execution)
	        .zipWith(adapter_getCompletion, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	}
	
	private Maybe<FlowExecution> simpleService_complete_complete_conditional(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<FlowExecution> topCall = simpleService_complete_complete_svcCall(execution, callModifier, callbacks);
	    final Maybe<Boolean> activator_ch_scaille_dataflowmgr_examples_simple_SimpleFlowConditions_mustComplete = Maybe.just(execution)
	        .map(f -> this.simpleFlowConditions.mustComplete(f.simpleService_init))
	        .subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> activators = Maybe.just(true)
	        .zipWith(activator_ch_scaille_dataflowmgr_examples_simple_SimpleFlowConditions_mustComplete, (u, r) -> u.booleanValue() && r.booleanValue())
	        .mapOptional(b -> b ? Optional.of(execution) : Optional.empty())
	        .flatMap(e -> topCall)
	        .doOnComplete(() -> { execution.setStateBindingSimpleServiceCompleteComplete(DataPointState.TRIGGERED); execution.setStateComplete(DataPointState.SKIPPED); })
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	
	    return activators;
	}
	private Maybe<FlowExecution> binding_simpleService_complete_complete(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<FlowExecution> topCall = simpleService_complete_complete_conditional(execution, callModifier, callbacks);
	    return Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init || DataPointState.SKIPPED == f.state_simpleService_init))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingSimpleServiceCompleteComplete()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> info("simpleService_init -> simpleService.complete -> complete: Deps success"))
	        .doOnComplete(() -> info("simpleService_init -> simpleService.complete -> complete: Deps skipping"))
	        .doOnSuccess(r -> topCall.subscribe());
	}
	
	// ------------------------- simpleService_init -> simpleService.keepAsIs -> complete -------------------------
	private Maybe<FlowExecution> simpleService_keepAsIs_complete_svcCall(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingSimpleServiceKeepAsIsComplete(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingSimpleServiceKeepAsIsComplete(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setComplete(this.simpleService.keepAsIs(f.simpleService_init)))
	        .doOnSuccess(r -> info("simpleService_init -> simpleService.keepAsIs -> complete: Call success"))
	        .doOnComplete(() -> info("simpleService_init -> simpleService.keepAsIs -> complete: Call skipped"))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService = callService.subscribeOn(Schedulers.computation());
	    return callService;
	}
	
	private Maybe<FlowExecution> simpleService_keepAsIs_complete_conditional(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<FlowExecution> topCall = simpleService_keepAsIs_complete_svcCall(execution, callModifier, callbacks);
	
	    return Maybe.just(execution).mapOptional(f -> DataPointState.SKIPPED == f.state_complete?Optional.of(f):Optional.empty()).doOnComplete(() -> info("simpleService_keepAsIs_complete: Call skipped")).doOnSuccess(f -> topCall.subscribe());
	}
	private Maybe<FlowExecution> binding_simpleService_keepAsIs_complete(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<FlowExecution> topCall = simpleService_keepAsIs_complete_conditional(execution, callModifier, callbacks);
	    return Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init || DataPointState.SKIPPED == f.state_simpleService_init)
	                  && (DataPointState.TRIGGERED == f.state_complete || DataPointState.SKIPPED == f.state_complete))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingSimpleServiceKeepAsIsComplete()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> info("simpleService_init -> simpleService.keepAsIs -> complete: Deps success"))
	        .doOnComplete(() -> info("simpleService_init -> simpleService.keepAsIs -> complete: Deps skipping"))
	        .doOnSuccess(r -> topCall.subscribe());
	}
	
	// ------------------------- complete -> exit -> exit -------------------------
	private Maybe<FlowExecution> exit_exit_svcCall(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_display = Maybe.just(execution)
	        .doOnSuccess(f -> this.simpleExternalAdapter.display(f.complete))
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingExitExit(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingExitExit(DataPointState.SKIPPED))
	        .doOnSuccess(r -> info("complete -> exit -> exit: Call success"))
	        .doOnComplete(() -> info("complete -> exit -> exit: Call skipped"))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService = callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    return Maybe.just(execution)
	        .zipWith(adapter_display, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	}
	
	private Maybe<FlowExecution> binding_exit_exit(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<FlowExecution> topCall = exit_exit_svcCall(execution, callModifier, callbacks);
	    return Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_complete || DataPointState.SKIPPED == f.state_complete))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingExitExit()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> info("complete -> exit -> exit: Deps success"))
	        .doOnComplete(() -> info("complete -> exit -> exit: Deps skipping"))
	        .doOnSuccess(r -> topCall.subscribe());
	}
	
	

	public Maybe<FlowExecution> execute(java.lang.String inputDataPoint,	
		final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> exitModifier) {
		
		final FlowExecution execution = new FlowExecution(inputDataPoint);
		// ------------------------- complete -> exit -> exit -------------------------
		final Maybe<FlowExecution> binding_exit_exit = binding_exit_exit(execution, exitModifier);
		// ------------------------- simpleService_init -> simpleService.keepAsIs -> complete -------------------------
		final Maybe<FlowExecution> binding_simpleService_keepAsIs_complete = binding_simpleService_keepAsIs_complete(execution, null, () -> binding_exit_exit.subscribe());
		// ------------------------- simpleService_init -> simpleService.complete -> complete -------------------------
		final Maybe<FlowExecution> binding_simpleService_complete_complete = binding_simpleService_complete_complete(execution, null, () -> binding_simpleService_keepAsIs_complete.subscribe(), () -> binding_exit_exit.subscribe());
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		final Maybe<FlowExecution> binding_simpleService_init_simpleService_init = binding_simpleService_init_simpleService_init(execution, null, () -> binding_simpleService_complete_complete.subscribe(), () -> binding_simpleService_keepAsIs_complete.subscribe());
		
		return binding_simpleService_init_simpleService_init;
	}

	private void runTest(final String in, final String out) throws InterruptedException {
		final Semaphore finished = new Semaphore(0);
		FlowReport.report.clear();
		simpleExternalAdapter.reset();
		final FlowExecution result = execute(in, e -> e.doOnSuccess(r -> finished.release())).blockingGet();
		Assertions.assertTrue(finished.tryAcquire(100, TimeUnit.MILLISECONDS), () -> "Promise executed");
		Assertions.assertEquals(out, simpleExternalAdapter.getOutput());
		info(FlowReport.report.toString());
	}

	@Test
	void testFlow() throws InterruptedException {
		runTest("Hello", "Hello -> complete with World");
		runTest("Hi", "Hi -> complete with There");
		runTest("Huh", "Huh -> keep as is");
	}
}
