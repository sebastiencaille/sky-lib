// File generated from template
package ch.skymarshall.dataflowmgr.examples.simplerx;

import ch.skymarshall.dataflowmgr.examples.simple.FlowReport;

import ch.skymarshall.util.helpers.Log;
import org.junit.Test;
import org.junit.Assert;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;


public class SimpleFlow extends ch.skymarshall.dataflowmgr.examples.simple.AbstractFlow {

	public enum DataPointState {
		NOT_TRIGGERED, TRIGGERING, TRIGGERED, SKIPPED
	}

	public class FlowExecution {
	
	    private final java.lang.String inputDataPoint;
		
		public FlowExecution(java.lang.String inputDataPoint) {
			this.inputDataPoint = inputDataPoint;
		}

		private DataPointState state_binding_simpleService_init = DataPointState.NOT_TRIGGERED;
		private void setStateBindingSimpleServiceInit(DataPointState state_binding_simpleService_init) {
		    this.state_binding_simpleService_init = state_binding_simpleService_init;
		}
		private synchronized boolean canTriggerBindingSimpleServiceInit() {
		    if (this.state_binding_simpleService_init == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_simpleService_init = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_simpleService_init = DataPointState.NOT_TRIGGERED;
		private void setStateSimpleServiceInit(DataPointState state_simpleService_init) {
		    this.state_simpleService_init = state_simpleService_init;
		}
		private ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init;
		private void setSimpleServiceInit(ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init) {
		    this.simpleService_init = simpleService_init;
		    this.state_simpleService_init = DataPointState.TRIGGERED;
		}
		
		private DataPointState state_binding_complete_mustComplete = DataPointState.NOT_TRIGGERED;
		private void setStateBindingCompleteMustComplete(DataPointState state_binding_complete_mustComplete) {
		    this.state_binding_complete_mustComplete = state_binding_complete_mustComplete;
		}
		private synchronized boolean canTriggerBindingCompleteMustComplete() {
		    if (this.state_binding_complete_mustComplete == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_complete_mustComplete = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_complete = DataPointState.NOT_TRIGGERED;
		private void setStateComplete(DataPointState state_complete) {
		    this.state_complete = state_complete;
		}
		private ch.skymarshall.dataflowmgr.examples.simple.dto.MyData complete;
		private void setComplete(ch.skymarshall.dataflowmgr.examples.simple.dto.MyData complete) {
		    this.complete = complete;
		    this.state_complete = DataPointState.TRIGGERED;
		}
		
		private java.lang.String simpleExternalAdapter_getCompletioncomplete_mustComplete;
		private void setSimpleExternalAdapterGetCompletioncompleteMustComplete(java.lang.String simpleExternalAdapter_getCompletioncomplete_mustComplete) {
		    this.simpleExternalAdapter_getCompletioncomplete_mustComplete = simpleExternalAdapter_getCompletioncomplete_mustComplete;
		}
		
		private DataPointState state_binding_complete = DataPointState.NOT_TRIGGERED;
		private void setStateBindingComplete(DataPointState state_binding_complete) {
		    this.state_binding_complete = state_binding_complete;
		}
		private synchronized boolean canTriggerBindingComplete() {
		    if (this.state_binding_complete == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_complete = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_binding_exit = DataPointState.NOT_TRIGGERED;
		private void setStateBindingExit(DataPointState state_binding_exit) {
		    this.state_binding_exit = state_binding_exit;
		}
		private synchronized boolean canTriggerBindingExit() {
		    if (this.state_binding_exit == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_exit = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		
	}

	// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
	private Maybe<FlowExecution> binding_simpleService_init(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingSimpleServiceInit(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingSimpleServiceInit(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setSimpleServiceInit(this.simpleService.init(f.inputDataPoint)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = callService;
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> f.canTriggerBindingSimpleServiceInit()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.complete -> complete -------------------------
	private Maybe<FlowExecution> binding_complete_mustComplete(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_getCompletion = Maybe.just(execution)
	        .map(f -> this.simpleExternalAdapter.getCompletion(f.simpleService_init))
	        .doOnSuccess(execution::setSimpleExternalAdapterGetCompletioncompleteMustComplete)
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingCompleteMustComplete(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingCompleteMustComplete(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setComplete(this.simpleService.complete(f.simpleService_init,f.simpleExternalAdapter_getCompletioncomplete_mustComplete)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_getCompletion, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    final Maybe<Boolean> activator_34c774d5_82e4_452a_bfd7_58ddcf867f7d = Maybe.just(execution)
	        .map(f -> this.simpleFlowConditions.mustComplete(f.simpleService_init))
	        .subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> activationCheck = Maybe.just(true)
	        .zipWith(activator_34c774d5_82e4_452a_bfd7_58ddcf867f7d, (u, r) -> u.booleanValue() && r.booleanValue())
	        .mapOptional(b -> b ? Optional.of(execution) : Optional.empty())
	        .flatMap(e -> callAdaptersAndServiceConst)
	        .doOnComplete(() -> { execution.setStateBindingCompleteMustComplete(DataPointState.TRIGGERED); execution.setStateComplete(DataPointState.SKIPPED); })
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingCompleteMustComplete()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> activationCheck.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.keepAsIs -> complete -------------------------
	private Maybe<FlowExecution> binding_complete(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingComplete(DataPointState.TRIGGERED))
	        .mapOptional(f -> DataPointState.SKIPPED == f.state_complete?Optional.of(f):Optional.empty())
	        .doOnComplete(() -> execution.setStateBindingComplete(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setComplete(this.simpleService.keepAsIs(f.simpleService_init)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = callService;
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init)
	          && (DataPointState.TRIGGERED == f.state_complete || DataPointState.SKIPPED == f.state_complete))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingComplete()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- complete -> exit -> exit -------------------------
	private Maybe<FlowExecution> binding_exit(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_display = Maybe.just(execution)
	        .doOnSuccess(f -> this.simpleExternalAdapter.display(f.complete))
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingExit(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingExit(DataPointState.SKIPPED))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_display, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_complete))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingExit()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	

	public Maybe<FlowExecution> execute(java.lang.String inputDataPoint,	
		final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> exitModifier) {
		
		final FlowExecution execution = new FlowExecution(inputDataPoint);
		// ------------------------- complete -> exit -> exit -------------------------
		final Maybe<FlowExecution> binding_exit = binding_exit(execution, exitModifier);
		// ------------------------- simpleService_init -> simpleService.keepAsIs -> complete -------------------------
		final Maybe<FlowExecution> binding_complete = binding_complete(execution, null, () -> binding_exit.subscribe());
		// ------------------------- simpleService_init -> simpleService.complete -> complete -------------------------
		final Maybe<FlowExecution> binding_complete_mustComplete = binding_complete_mustComplete(execution, null, () -> binding_complete.subscribe(), () -> binding_exit.subscribe());
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		final Maybe<FlowExecution> binding_simpleService_init = binding_simpleService_init(execution, null, () -> binding_complete_mustComplete.subscribe(), () -> binding_complete.subscribe());
		
		return binding_simpleService_init;
	}

	private void runTest(final String in, final String out) throws InterruptedException {
		final Semaphore finished = new Semaphore(0);
		FlowReport.report.clear();
		simpleExternalAdapter.reset();
		final FlowExecution result = execute(in, e -> e.doOnSuccess(r -> finished.release())).blockingGet();
		Assert.assertTrue(finished.tryAcquire(100, TimeUnit.MILLISECONDS));
		Assert.assertEquals(out, simpleExternalAdapter.getOutput());
		Log.of(this).info(FlowReport.report.toString());
	}

	@Test
	public void testFlow() throws InterruptedException {
		runTest("Hello", "Hello -> complete with World");
		runTest("Hi", "Hi -> complete with There");
		runTest("Huh", "Huh -> keep as is");
	}
}