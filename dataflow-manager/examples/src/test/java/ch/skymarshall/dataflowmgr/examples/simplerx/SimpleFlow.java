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

		private DataPointState state_binding_2d50caba_191f_4e6f_a883_574daf48afd4 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding2d50caba191f4e6fA883574daf48afd4(DataPointState state_binding_2d50caba_191f_4e6f_a883_574daf48afd4) {
		    this.state_binding_2d50caba_191f_4e6f_a883_574daf48afd4 = state_binding_2d50caba_191f_4e6f_a883_574daf48afd4;
		}
		private synchronized boolean canTriggerBinding2d50caba191f4e6fA883574daf48afd4() {
		    if (this.state_binding_2d50caba_191f_4e6f_a883_574daf48afd4 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_2d50caba_191f_4e6f_a883_574daf48afd4 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_simpleService_init = DataPointState.NOT_TRIGGERED;
		private ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init;
		private void setStateSimpleServiceInit(DataPointState state_simpleService_init) {
		    this.state_simpleService_init = state_simpleService_init;
		}
		private void setSimpleServiceInit(ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init) {
		    this.simpleService_init = simpleService_init;
		    this.state_simpleService_init = DataPointState.TRIGGERED;
		}
		
		private DataPointState state_binding_6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding6e98941dF9ae4a9e8df55ba70cbdcd92(DataPointState state_binding_6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92) {
		    this.state_binding_6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92 = state_binding_6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92;
		}
		private synchronized boolean canTriggerBinding6e98941dF9ae4a9e8df55ba70cbdcd92() {
		    if (this.state_binding_6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_enhanced = DataPointState.NOT_TRIGGERED;
		private ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced;
		private void setStateEnhanced(DataPointState state_enhanced) {
		    this.state_enhanced = state_enhanced;
		}
		private void setEnhanced(ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced) {
		    this.enhanced = enhanced;
		    this.state_enhanced = DataPointState.TRIGGERED;
		}
		
		private DataPointState state_simpleExternalAdapter_enhancement6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92 = DataPointState.NOT_TRIGGERED;
		private java.lang.String simpleExternalAdapter_enhancement6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92;
		private void setStateSimpleExternalAdapterEnhancement6e98941dF9ae4a9e8df55ba70cbdcd92(DataPointState state_simpleExternalAdapter_enhancement6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92) {
		    this.state_simpleExternalAdapter_enhancement6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92 = state_simpleExternalAdapter_enhancement6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92;
		}
		private void setSimpleExternalAdapterEnhancement6e98941dF9ae4a9e8df55ba70cbdcd92(java.lang.String simpleExternalAdapter_enhancement6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92) {
		    this.simpleExternalAdapter_enhancement6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92 = simpleExternalAdapter_enhancement6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92;
		    this.state_simpleExternalAdapter_enhancement6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92 = DataPointState.TRIGGERED;
		}
		
		private DataPointState state_binding_33d7d269_dad8_48e5_8b02_f31e716e74a3 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding33d7d269Dad848e58b02F31e716e74a3(DataPointState state_binding_33d7d269_dad8_48e5_8b02_f31e716e74a3) {
		    this.state_binding_33d7d269_dad8_48e5_8b02_f31e716e74a3 = state_binding_33d7d269_dad8_48e5_8b02_f31e716e74a3;
		}
		private synchronized boolean canTriggerBinding33d7d269Dad848e58b02F31e716e74a3() {
		    if (this.state_binding_33d7d269_dad8_48e5_8b02_f31e716e74a3 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_33d7d269_dad8_48e5_8b02_f31e716e74a3 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_binding_25512c6b_34b5_4639_9a69_e7311b98181d = DataPointState.NOT_TRIGGERED;
		private void setStateBinding25512c6b34b546399a69E7311b98181d(DataPointState state_binding_25512c6b_34b5_4639_9a69_e7311b98181d) {
		    this.state_binding_25512c6b_34b5_4639_9a69_e7311b98181d = state_binding_25512c6b_34b5_4639_9a69_e7311b98181d;
		}
		private synchronized boolean canTriggerBinding25512c6b34b546399a69E7311b98181d() {
		    if (this.state_binding_25512c6b_34b5_4639_9a69_e7311b98181d == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_25512c6b_34b5_4639_9a69_e7311b98181d = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		
	}

	// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
	private Maybe<FlowExecution> binding_2d50caba_191f_4e6f_a883_574daf48afd4(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding2d50caba191f4e6fA883574daf48afd4(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding2d50caba191f4e6fA883574daf48afd4(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setSimpleServiceInit(this.simpleService.init(f.inputDataPoint)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = callService;
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> f.canTriggerBinding2d50caba191f4e6fA883574daf48afd4()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_d813ac8f_9b25_4dea_914e_325df653f99e = Maybe.just(execution)        .map(f -> this.simpleExternalAdapter.enhancement(f.simpleService_init))
	        .doOnSuccess(execution::setSimpleExternalAdapterEnhancement6e98941dF9ae4a9e8df55ba70cbdcd92)
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding6e98941dF9ae4a9e8df55ba70cbdcd92(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding6e98941dF9ae4a9e8df55ba70cbdcd92(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setEnhanced(this.simpleService.enhance(f.simpleService_init,f.simpleExternalAdapter_enhancement6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_d813ac8f_9b25_4dea_914e_325df653f99e, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    final Maybe<Boolean> activator_96cc5269_7ef4_4939_8fb3_dd175b52eed3 = Maybe.just(execution)
	        .map(f -> this.simpleServiceConditions.isEnhanceEnabled(f.simpleService_init))
	        .subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> activationCheck = Maybe.just(true)
	        .zipWith(activator_96cc5269_7ef4_4939_8fb3_dd175b52eed3, (u, r) -> u.booleanValue() && r.booleanValue())
	        .mapOptional(b -> b ? Optional.of(execution) : Optional.empty())
	        .flatMap(e -> callAdaptersAndServiceConst)
	        .doOnComplete(() -> { execution.setStateBinding6e98941dF9ae4a9e8df55ba70cbdcd92(DataPointState.TRIGGERED); execution.setStateEnhanced(DataPointState.SKIPPED); })
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init || DataPointState.SKIPPED == f.state_simpleService_init))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBinding6e98941dF9ae4a9e8df55ba70cbdcd92()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> activationCheck.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_33d7d269_dad8_48e5_8b02_f31e716e74a3(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding33d7d269Dad848e58b02F31e716e74a3(DataPointState.TRIGGERED))
	        .mapOptional(f -> DataPointState.SKIPPED == f.state_enhanced?Optional.of(f):Optional.empty())
	        .doOnComplete(() -> execution.setStateBinding33d7d269Dad848e58b02F31e716e74a3(DataPointState.SKIPPED))
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
	        .mapOptional(f -> f.canTriggerBinding33d7d269Dad848e58b02F31e716e74a3()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- enhanced -> exit -> exit -------------------------
	private Maybe<FlowExecution> binding_25512c6b_34b5_4639_9a69_e7311b98181d(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_1d53ecc3_791b_45a1_9a90_0ae1f65512b5 = Maybe.just(execution)        .doOnSuccess(f -> this.simpleExternalAdapter.display(f.enhanced))
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding25512c6b34b546399a69E7311b98181d(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding25512c6b34b546399a69E7311b98181d(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setStateBinding25512c6b34b546399a69E7311b98181d(DataPointState.TRIGGERED))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_1d53ecc3_791b_45a1_9a90_0ae1f65512b5, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_enhanced || DataPointState.SKIPPED == f.state_enhanced))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBinding25512c6b34b546399a69E7311b98181d()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	

	public Maybe<FlowExecution> execute(java.lang.String inputDataPoint,	
		final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> exitModifier) {
		
		final FlowExecution execution = new FlowExecution(inputDataPoint);
		// ------------------------- enhanced -> exit -> exit -------------------------
		final Maybe<FlowExecution> binding_25512c6b_34b5_4639_9a69_e7311b98181d = binding_25512c6b_34b5_4639_9a69_e7311b98181d(execution, exitModifier);
		// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_33d7d269_dad8_48e5_8b02_f31e716e74a3 = binding_33d7d269_dad8_48e5_8b02_f31e716e74a3(execution, null, () -> binding_25512c6b_34b5_4639_9a69_e7311b98181d.subscribe());
		// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92 = binding_6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92(execution, null, () -> binding_33d7d269_dad8_48e5_8b02_f31e716e74a3.subscribe(), () -> binding_25512c6b_34b5_4639_9a69_e7311b98181d.subscribe());
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		final Maybe<FlowExecution> binding_2d50caba_191f_4e6f_a883_574daf48afd4 = binding_2d50caba_191f_4e6f_a883_574daf48afd4(execution, null, () -> binding_6e98941d_f9ae_4a9e_8df5_5ba70cbdcd92.subscribe(), () -> binding_33d7d269_dad8_48e5_8b02_f31e716e74a3.subscribe());
		
		return Maybe.zipArray(a -> execution, binding_2d50caba_191f_4e6f_a883_574daf48afd4);
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