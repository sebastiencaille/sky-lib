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

		private DataPointState state_binding_c7a56689_0b10_4280_9647_babfddd69c06 = DataPointState.NOT_TRIGGERED;
		private void setStateBindingC7a566890b1042809647Babfddd69c06(DataPointState state_binding_c7a56689_0b10_4280_9647_babfddd69c06) {
		    this.state_binding_c7a56689_0b10_4280_9647_babfddd69c06 = state_binding_c7a56689_0b10_4280_9647_babfddd69c06;
		}
		private synchronized boolean canTriggerBindingC7a566890b1042809647Babfddd69c06() {
		    if (this.state_binding_c7a56689_0b10_4280_9647_babfddd69c06 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_c7a56689_0b10_4280_9647_babfddd69c06 = DataPointState.TRIGGERING;
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
		
		private DataPointState state_binding_22599780_f1d3_4727_9d45_4f15a317416d = DataPointState.NOT_TRIGGERED;
		private void setStateBinding22599780F1d347279d454f15a317416d(DataPointState state_binding_22599780_f1d3_4727_9d45_4f15a317416d) {
		    this.state_binding_22599780_f1d3_4727_9d45_4f15a317416d = state_binding_22599780_f1d3_4727_9d45_4f15a317416d;
		}
		private synchronized boolean canTriggerBinding22599780F1d347279d454f15a317416d() {
		    if (this.state_binding_22599780_f1d3_4727_9d45_4f15a317416d == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_22599780_f1d3_4727_9d45_4f15a317416d = DataPointState.TRIGGERING;
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
		
		private java.lang.String simpleExternalAdapter_enhancement22599780_f1d3_4727_9d45_4f15a317416d;
		private void setSimpleExternalAdapterEnhancement22599780F1d347279d454f15a317416d(java.lang.String simpleExternalAdapter_enhancement22599780_f1d3_4727_9d45_4f15a317416d) {
		    this.simpleExternalAdapter_enhancement22599780_f1d3_4727_9d45_4f15a317416d = simpleExternalAdapter_enhancement22599780_f1d3_4727_9d45_4f15a317416d;
		}
		
		private DataPointState state_binding_7d30ea32_b69b_45bb_b1fe_9cc8463e7d0e = DataPointState.NOT_TRIGGERED;
		private void setStateBinding7d30ea32B69b45bbB1fe9cc8463e7d0e(DataPointState state_binding_7d30ea32_b69b_45bb_b1fe_9cc8463e7d0e) {
		    this.state_binding_7d30ea32_b69b_45bb_b1fe_9cc8463e7d0e = state_binding_7d30ea32_b69b_45bb_b1fe_9cc8463e7d0e;
		}
		private synchronized boolean canTriggerBinding7d30ea32B69b45bbB1fe9cc8463e7d0e() {
		    if (this.state_binding_7d30ea32_b69b_45bb_b1fe_9cc8463e7d0e == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_7d30ea32_b69b_45bb_b1fe_9cc8463e7d0e = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_binding_d571e0df_8edd_4bd9_8bf2_34474e530e27 = DataPointState.NOT_TRIGGERED;
		private void setStateBindingD571e0df8edd4bd98bf234474e530e27(DataPointState state_binding_d571e0df_8edd_4bd9_8bf2_34474e530e27) {
		    this.state_binding_d571e0df_8edd_4bd9_8bf2_34474e530e27 = state_binding_d571e0df_8edd_4bd9_8bf2_34474e530e27;
		}
		private synchronized boolean canTriggerBindingD571e0df8edd4bd98bf234474e530e27() {
		    if (this.state_binding_d571e0df_8edd_4bd9_8bf2_34474e530e27 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_d571e0df_8edd_4bd9_8bf2_34474e530e27 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		
	}

	// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
	private Maybe<FlowExecution> binding_c7a56689_0b10_4280_9647_babfddd69c06(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingC7a566890b1042809647Babfddd69c06(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingC7a566890b1042809647Babfddd69c06(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setSimpleServiceInit(this.simpleService.init(f.inputDataPoint)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = callService;
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> f.canTriggerBindingC7a566890b1042809647Babfddd69c06()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_22599780_f1d3_4727_9d45_4f15a317416d(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_9c4abf8e_1b66_43e2_81be_d216779ac9a9 = Maybe.just(execution)        .map(f -> this.simpleExternalAdapter.enhancement(f.simpleService_init))
	        .doOnSuccess(execution::setSimpleExternalAdapterEnhancement22599780F1d347279d454f15a317416d)
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding22599780F1d347279d454f15a317416d(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding22599780F1d347279d454f15a317416d(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setEnhanced(this.simpleService.enhance(f.simpleService_init,f.simpleExternalAdapter_enhancement22599780_f1d3_4727_9d45_4f15a317416d)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_9c4abf8e_1b66_43e2_81be_d216779ac9a9, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    final Maybe<Boolean> activator_23567051_39a8_444b_9525_34e0ac183468 = Maybe.just(execution)
	        .map(f -> this.simpleServiceConditions.isEnhanceEnabled(f.simpleService_init))
	        .subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> activationCheck = Maybe.just(true)
	        .zipWith(activator_23567051_39a8_444b_9525_34e0ac183468, (u, r) -> u.booleanValue() && r.booleanValue())
	        .mapOptional(b -> b ? Optional.of(execution) : Optional.empty())
	        .flatMap(e -> callAdaptersAndServiceConst)
	        .doOnComplete(() -> { execution.setStateBinding22599780F1d347279d454f15a317416d(DataPointState.TRIGGERED); execution.setStateEnhanced(DataPointState.SKIPPED); })
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init || DataPointState.SKIPPED == f.state_simpleService_init))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBinding22599780F1d347279d454f15a317416d()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> activationCheck.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_7d30ea32_b69b_45bb_b1fe_9cc8463e7d0e(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding7d30ea32B69b45bbB1fe9cc8463e7d0e(DataPointState.TRIGGERED))
	        .mapOptional(f -> DataPointState.SKIPPED == f.state_enhanced?Optional.of(f):Optional.empty())
	        .doOnComplete(() -> execution.setStateBinding7d30ea32B69b45bbB1fe9cc8463e7d0e(DataPointState.SKIPPED))
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
	        .mapOptional(f -> f.canTriggerBinding7d30ea32B69b45bbB1fe9cc8463e7d0e()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- enhanced -> exit -> exit -------------------------
	private Maybe<FlowExecution> binding_d571e0df_8edd_4bd9_8bf2_34474e530e27(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_695b3602_3ac8_46d3_8a78_5952f721ebbd = Maybe.just(execution)        .doOnSuccess(f -> this.simpleExternalAdapter.display(f.enhanced))
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingD571e0df8edd4bd98bf234474e530e27(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingD571e0df8edd4bd98bf234474e530e27(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setStateBindingD571e0df8edd4bd98bf234474e530e27(DataPointState.TRIGGERED))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_695b3602_3ac8_46d3_8a78_5952f721ebbd, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_enhanced || DataPointState.SKIPPED == f.state_enhanced))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingD571e0df8edd4bd98bf234474e530e27()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	

	public Maybe<FlowExecution> execute(java.lang.String inputDataPoint,	
		final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> exitModifier) {
		
		final FlowExecution execution = new FlowExecution(inputDataPoint);
		// ------------------------- enhanced -> exit -> exit -------------------------
		final Maybe<FlowExecution> binding_d571e0df_8edd_4bd9_8bf2_34474e530e27 = binding_d571e0df_8edd_4bd9_8bf2_34474e530e27(execution, exitModifier);
		// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_7d30ea32_b69b_45bb_b1fe_9cc8463e7d0e = binding_7d30ea32_b69b_45bb_b1fe_9cc8463e7d0e(execution, null, () -> binding_d571e0df_8edd_4bd9_8bf2_34474e530e27.subscribe());
		// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_22599780_f1d3_4727_9d45_4f15a317416d = binding_22599780_f1d3_4727_9d45_4f15a317416d(execution, null, () -> binding_7d30ea32_b69b_45bb_b1fe_9cc8463e7d0e.subscribe(), () -> binding_d571e0df_8edd_4bd9_8bf2_34474e530e27.subscribe());
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		final Maybe<FlowExecution> binding_c7a56689_0b10_4280_9647_babfddd69c06 = binding_c7a56689_0b10_4280_9647_babfddd69c06(execution, null, () -> binding_22599780_f1d3_4727_9d45_4f15a317416d.subscribe(), () -> binding_7d30ea32_b69b_45bb_b1fe_9cc8463e7d0e.subscribe());
		
		return Maybe.zipArray(a -> execution, binding_c7a56689_0b10_4280_9647_babfddd69c06);
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