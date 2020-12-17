// File generated from template
package ch.skymarshall.dataflowmgr.examples.simplerx;

import ch.skymarshall.dataflowmgr.examples.simple.FlowReport;

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

		private DataPointState state_binding_608ba292_83d5_4bc7_85e9_1d59ec56187d = DataPointState.NOT_TRIGGERED;
		private void setStateBinding608ba29283d54bc785e91d59ec56187d(DataPointState state_binding_608ba292_83d5_4bc7_85e9_1d59ec56187d) {
		    this.state_binding_608ba292_83d5_4bc7_85e9_1d59ec56187d = state_binding_608ba292_83d5_4bc7_85e9_1d59ec56187d;
		}
		private synchronized boolean canTriggerBinding608ba29283d54bc785e91d59ec56187d() {
		    if (this.state_binding_608ba292_83d5_4bc7_85e9_1d59ec56187d == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_608ba292_83d5_4bc7_85e9_1d59ec56187d = DataPointState.TRIGGERING;
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
		
		private DataPointState state_binding_a62388d9_376e_4e14_83af_8cc77899841c = DataPointState.NOT_TRIGGERED;
		private void setStateBindingA62388d9376e4e1483af8cc77899841c(DataPointState state_binding_a62388d9_376e_4e14_83af_8cc77899841c) {
		    this.state_binding_a62388d9_376e_4e14_83af_8cc77899841c = state_binding_a62388d9_376e_4e14_83af_8cc77899841c;
		}
		private synchronized boolean canTriggerBindingA62388d9376e4e1483af8cc77899841c() {
		    if (this.state_binding_a62388d9_376e_4e14_83af_8cc77899841c == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_a62388d9_376e_4e14_83af_8cc77899841c = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_enhanced = DataPointState.NOT_TRIGGERED;
		private void setStateEnhanced(DataPointState state_enhanced) {
		    this.state_enhanced = state_enhanced;
		}
		private ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced;
		private void setEnhanced(ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced) {
		    this.enhanced = enhanced;
		    this.state_enhanced = DataPointState.TRIGGERED;
		}
		
		private java.lang.String simpleExternalAdapter_enhancementa62388d9_376e_4e14_83af_8cc77899841c;
		private void setSimpleExternalAdapterEnhancementa62388d9376e4e1483af8cc77899841c(java.lang.String simpleExternalAdapter_enhancementa62388d9_376e_4e14_83af_8cc77899841c) {
		    this.simpleExternalAdapter_enhancementa62388d9_376e_4e14_83af_8cc77899841c = simpleExternalAdapter_enhancementa62388d9_376e_4e14_83af_8cc77899841c;
		}
		
		private DataPointState state_binding_441cfc3d_d543_4cb2_bce1_e3c35da91e4e = DataPointState.NOT_TRIGGERED;
		private void setStateBinding441cfc3dD5434cb2Bce1E3c35da91e4e(DataPointState state_binding_441cfc3d_d543_4cb2_bce1_e3c35da91e4e) {
		    this.state_binding_441cfc3d_d543_4cb2_bce1_e3c35da91e4e = state_binding_441cfc3d_d543_4cb2_bce1_e3c35da91e4e;
		}
		private synchronized boolean canTriggerBinding441cfc3dD5434cb2Bce1E3c35da91e4e() {
		    if (this.state_binding_441cfc3d_d543_4cb2_bce1_e3c35da91e4e == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_441cfc3d_d543_4cb2_bce1_e3c35da91e4e = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_binding_9c606289_6a60_4b3a_8ce4_02fd90d62b24 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding9c6062896a604b3a8ce402fd90d62b24(DataPointState state_binding_9c606289_6a60_4b3a_8ce4_02fd90d62b24) {
		    this.state_binding_9c606289_6a60_4b3a_8ce4_02fd90d62b24 = state_binding_9c606289_6a60_4b3a_8ce4_02fd90d62b24;
		}
		private synchronized boolean canTriggerBinding9c6062896a604b3a8ce402fd90d62b24() {
		    if (this.state_binding_9c606289_6a60_4b3a_8ce4_02fd90d62b24 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_9c606289_6a60_4b3a_8ce4_02fd90d62b24 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		
	}

	// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
	private Maybe<FlowExecution> binding_608ba292_83d5_4bc7_85e9_1d59ec56187d(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding608ba29283d54bc785e91d59ec56187d(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding608ba29283d54bc785e91d59ec56187d(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setSimpleServiceInit(this.simpleService.init(f.inputDataPoint)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = callService;
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> f.canTriggerBinding608ba29283d54bc785e91d59ec56187d()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_a62388d9_376e_4e14_83af_8cc77899841c(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_b6549c5d_4e2f_46f3_b013_373b719197f9 = Maybe.just(execution)        .map(f -> this.simpleExternalAdapter.enhancement(f.simpleService_init))
	        .doOnSuccess(execution::setSimpleExternalAdapterEnhancementa62388d9376e4e1483af8cc77899841c)
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingA62388d9376e4e1483af8cc77899841c(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingA62388d9376e4e1483af8cc77899841c(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setEnhanced(this.simpleService.enhance(f.simpleService_init,f.simpleExternalAdapter_enhancementa62388d9_376e_4e14_83af_8cc77899841c)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_b6549c5d_4e2f_46f3_b013_373b719197f9, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    final Maybe<Boolean> activator_686c84fe_8ce5_4b67_a441_ba2013df8d26 = Maybe.just(execution)
	        .map(f -> this.simpleServiceConditions.isEnhanceEnabled(f.simpleService_init))
	        .subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> activationCheck = Maybe.just(true)
	        .zipWith(activator_686c84fe_8ce5_4b67_a441_ba2013df8d26, (u, r) -> u.booleanValue() && r.booleanValue())
	        .mapOptional(b -> b ? Optional.of(execution) : Optional.empty())
	        .flatMap(e -> callAdaptersAndServiceConst)
	        .doOnComplete(() -> { execution.setStateBindingA62388d9376e4e1483af8cc77899841c(DataPointState.TRIGGERED); execution.setStateEnhanced(DataPointState.SKIPPED); })
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init || DataPointState.SKIPPED == f.state_simpleService_init))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingA62388d9376e4e1483af8cc77899841c()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> activationCheck.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_441cfc3d_d543_4cb2_bce1_e3c35da91e4e(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding441cfc3dD5434cb2Bce1E3c35da91e4e(DataPointState.TRIGGERED))
	        .mapOptional(f -> DataPointState.SKIPPED == f.state_enhanced?Optional.of(f):Optional.empty())
	        .doOnComplete(() -> execution.setStateBinding441cfc3dD5434cb2Bce1E3c35da91e4e(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setEnhanced(this.simpleService.noEnhance(f.simpleService_init)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = callService;
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init || DataPointState.SKIPPED == f.state_simpleService_init) && (DataPointState.TRIGGERED == f.state_enhanced || DataPointState.SKIPPED == f.state_enhanced))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBinding441cfc3dD5434cb2Bce1E3c35da91e4e()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- enhanced -> exit -> exit -------------------------
	private Maybe<FlowExecution> binding_9c606289_6a60_4b3a_8ce4_02fd90d62b24(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_0f2d680e_f186_4dc7_bfa8_9ee4baf2531c = Maybe.just(execution)        .doOnSuccess(f -> this.simpleExternalAdapter.display(f.enhanced))
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding9c6062896a604b3a8ce402fd90d62b24(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding9c6062896a604b3a8ce402fd90d62b24(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setStateBinding9c6062896a604b3a8ce402fd90d62b24(DataPointState.TRIGGERED))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_0f2d680e_f186_4dc7_bfa8_9ee4baf2531c, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_enhanced || DataPointState.SKIPPED == f.state_enhanced))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBinding9c6062896a604b3a8ce402fd90d62b24()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	

	public Maybe<FlowExecution> execute(java.lang.String inputDataPoint,	
		final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> exitModifier) {
		
		final FlowExecution execution = new FlowExecution(inputDataPoint);
		// ------------------------- enhanced -> exit -> exit -------------------------
		final Maybe<FlowExecution> binding_9c606289_6a60_4b3a_8ce4_02fd90d62b24 = binding_9c606289_6a60_4b3a_8ce4_02fd90d62b24(execution, exitModifier);
		// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_441cfc3d_d543_4cb2_bce1_e3c35da91e4e = binding_441cfc3d_d543_4cb2_bce1_e3c35da91e4e(execution, null, () -> binding_9c606289_6a60_4b3a_8ce4_02fd90d62b24.subscribe());
		// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_a62388d9_376e_4e14_83af_8cc77899841c = binding_a62388d9_376e_4e14_83af_8cc77899841c(execution, null, () -> binding_441cfc3d_d543_4cb2_bce1_e3c35da91e4e.subscribe(), () -> binding_9c606289_6a60_4b3a_8ce4_02fd90d62b24.subscribe());
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		final Maybe<FlowExecution> binding_608ba292_83d5_4bc7_85e9_1d59ec56187d = binding_608ba292_83d5_4bc7_85e9_1d59ec56187d(execution, null, () -> binding_a62388d9_376e_4e14_83af_8cc77899841c.subscribe(), () -> binding_441cfc3d_d543_4cb2_bce1_e3c35da91e4e.subscribe());
		
		return Maybe.zipArray(a -> execution, binding_608ba292_83d5_4bc7_85e9_1d59ec56187d);
	}

	private void runTest(final String in, final String out) throws InterruptedException {
		final Semaphore finished = new Semaphore(0);
		FlowReport.report.clear();
		simpleExternalAdapter.reset();
		final FlowExecution result = execute(in, e -> e.doOnSuccess(r -> finished.release())).blockingGet();
		Assert.assertTrue(finished.tryAcquire(500, TimeUnit.MILLISECONDS));
		Assert.assertFalse(finished.tryAcquire(500, TimeUnit.MILLISECONDS));
		Assert.assertEquals(out, simpleExternalAdapter.getOutput());
		System.out.println(FlowReport.report);
	}

	@Test
	public void testFlow() throws InterruptedException {
		runTest("Hello", "Hello -> enhanced with World");
		runTest("Hi", "Hi -> enhanced with There");
		runTest("Huh", "Huh -> not enhanced");
	}
}