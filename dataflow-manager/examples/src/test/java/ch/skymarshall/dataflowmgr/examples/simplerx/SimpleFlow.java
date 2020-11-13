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

		private DataPointState state_binding_90ab3f95_2826_4c20_b596_b2d76ea599d4 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding90ab3f9528264c20B596B2d76ea599d4(DataPointState state_binding_90ab3f95_2826_4c20_b596_b2d76ea599d4) {
		    this.state_binding_90ab3f95_2826_4c20_b596_b2d76ea599d4 = state_binding_90ab3f95_2826_4c20_b596_b2d76ea599d4;
		}
		private synchronized boolean canTriggerBinding90ab3f9528264c20B596B2d76ea599d4() {
		    if (this.state_binding_90ab3f95_2826_4c20_b596_b2d76ea599d4 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_90ab3f95_2826_4c20_b596_b2d76ea599d4 = DataPointState.TRIGGERING;
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
		
		private DataPointState state_binding_481a956b_43f6_4d34_884c_5b89346d2e3b = DataPointState.NOT_TRIGGERED;
		private void setStateBinding481a956b43f64d34884c5b89346d2e3b(DataPointState state_binding_481a956b_43f6_4d34_884c_5b89346d2e3b) {
		    this.state_binding_481a956b_43f6_4d34_884c_5b89346d2e3b = state_binding_481a956b_43f6_4d34_884c_5b89346d2e3b;
		}
		private synchronized boolean canTriggerBinding481a956b43f64d34884c5b89346d2e3b() {
		    if (this.state_binding_481a956b_43f6_4d34_884c_5b89346d2e3b == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_481a956b_43f6_4d34_884c_5b89346d2e3b = DataPointState.TRIGGERING;
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
		
		private java.lang.String simpleExternalAdapter_enhancement481a956b_43f6_4d34_884c_5b89346d2e3b;
		private void setSimpleExternalAdapterEnhancement481a956b43f64d34884c5b89346d2e3b(java.lang.String simpleExternalAdapter_enhancement481a956b_43f6_4d34_884c_5b89346d2e3b) {
		    this.simpleExternalAdapter_enhancement481a956b_43f6_4d34_884c_5b89346d2e3b = simpleExternalAdapter_enhancement481a956b_43f6_4d34_884c_5b89346d2e3b;
		}
		
		private DataPointState state_binding_a7003a0b_b777_4b3b_b4db_11cb8718dc49 = DataPointState.NOT_TRIGGERED;
		private void setStateBindingA7003a0bB7774b3bB4db11cb8718dc49(DataPointState state_binding_a7003a0b_b777_4b3b_b4db_11cb8718dc49) {
		    this.state_binding_a7003a0b_b777_4b3b_b4db_11cb8718dc49 = state_binding_a7003a0b_b777_4b3b_b4db_11cb8718dc49;
		}
		private synchronized boolean canTriggerBindingA7003a0bB7774b3bB4db11cb8718dc49() {
		    if (this.state_binding_a7003a0b_b777_4b3b_b4db_11cb8718dc49 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_a7003a0b_b777_4b3b_b4db_11cb8718dc49 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_binding_a4befba0_9509_4161_8f47_61fee39e7aed = DataPointState.NOT_TRIGGERED;
		private void setStateBindingA4befba0950941618f4761fee39e7aed(DataPointState state_binding_a4befba0_9509_4161_8f47_61fee39e7aed) {
		    this.state_binding_a4befba0_9509_4161_8f47_61fee39e7aed = state_binding_a4befba0_9509_4161_8f47_61fee39e7aed;
		}
		private synchronized boolean canTriggerBindingA4befba0950941618f4761fee39e7aed() {
		    if (this.state_binding_a4befba0_9509_4161_8f47_61fee39e7aed == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_a4befba0_9509_4161_8f47_61fee39e7aed = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		
	}

	// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
	private Maybe<FlowExecution> binding_90ab3f95_2826_4c20_b596_b2d76ea599d4(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding90ab3f9528264c20B596B2d76ea599d4(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding90ab3f9528264c20B596B2d76ea599d4(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setSimpleServiceInit(this.simpleService.init(f.inputDataPoint)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = callService;
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> f.canTriggerBinding90ab3f9528264c20B596B2d76ea599d4()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_481a956b_43f6_4d34_884c_5b89346d2e3b(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_3c8d4370_6559_464a_8191_7cf1e2560626 = Maybe.just(execution)        .map(f -> this.simpleExternalAdapter.enhancement(f.simpleService_init))
	        .doOnSuccess(execution::setSimpleExternalAdapterEnhancement481a956b43f64d34884c5b89346d2e3b)
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding481a956b43f64d34884c5b89346d2e3b(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding481a956b43f64d34884c5b89346d2e3b(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setEnhanced(this.simpleService.enhance(f.simpleService_init,f.simpleExternalAdapter_enhancement481a956b_43f6_4d34_884c_5b89346d2e3b)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_3c8d4370_6559_464a_8191_7cf1e2560626, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    final Maybe<Boolean> activator_c251031a_d7df_4791_a3d0_2f4c7738929b = Maybe.just(execution)
	        .map(f -> this.simpleServiceConditions.isEnhanceEnabled(f.simpleService_init))
	        .subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> activationCheck = Maybe.just(true)
	        .zipWith(activator_c251031a_d7df_4791_a3d0_2f4c7738929b, (u, r) -> u.booleanValue() && r.booleanValue())
	        .mapOptional(b -> b ? Optional.of(execution) : Optional.empty())
	        .flatMap(e -> callAdaptersAndServiceConst)
	        .doOnComplete(() -> { execution.setStateBinding481a956b43f64d34884c5b89346d2e3b(DataPointState.TRIGGERED); execution.setStateEnhanced(DataPointState.SKIPPED); })
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init || DataPointState.SKIPPED == f.state_simpleService_init))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBinding481a956b43f64d34884c5b89346d2e3b()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> activationCheck.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_a7003a0b_b777_4b3b_b4db_11cb8718dc49(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingA7003a0bB7774b3bB4db11cb8718dc49(DataPointState.TRIGGERED))
	        .mapOptional(f -> DataPointState.SKIPPED == f.state_enhanced?Optional.of(f):Optional.empty())
	        .doOnComplete(() -> execution.setStateBindingA7003a0bB7774b3bB4db11cb8718dc49(DataPointState.SKIPPED))
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
	        .mapOptional(f -> f.canTriggerBindingA7003a0bB7774b3bB4db11cb8718dc49()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- enhanced -> exit -> exit -------------------------
	private Maybe<FlowExecution> binding_a4befba0_9509_4161_8f47_61fee39e7aed(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_9ea1fc6c_2669_427f_a1a1_29dbdeaa9a85 = Maybe.just(execution)        .doOnSuccess(f -> this.simpleExternalAdapter.display(f.enhanced))
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingA4befba0950941618f4761fee39e7aed(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingA4befba0950941618f4761fee39e7aed(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setStateBindingA4befba0950941618f4761fee39e7aed(DataPointState.TRIGGERED))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_9ea1fc6c_2669_427f_a1a1_29dbdeaa9a85, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_enhanced || DataPointState.SKIPPED == f.state_enhanced))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingA4befba0950941618f4761fee39e7aed()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	

	public Maybe<FlowExecution> execute(java.lang.String inputDataPoint,	
		final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> exitModifier) {
		
		final FlowExecution execution = new FlowExecution(inputDataPoint);
		// ------------------------- enhanced -> exit -> exit -------------------------
		final Maybe<FlowExecution> binding_a4befba0_9509_4161_8f47_61fee39e7aed = binding_a4befba0_9509_4161_8f47_61fee39e7aed(execution, exitModifier);
		// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_a7003a0b_b777_4b3b_b4db_11cb8718dc49 = binding_a7003a0b_b777_4b3b_b4db_11cb8718dc49(execution, null, () -> binding_a4befba0_9509_4161_8f47_61fee39e7aed.subscribe());
		// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_481a956b_43f6_4d34_884c_5b89346d2e3b = binding_481a956b_43f6_4d34_884c_5b89346d2e3b(execution, null, () -> binding_a7003a0b_b777_4b3b_b4db_11cb8718dc49.subscribe(), () -> binding_a4befba0_9509_4161_8f47_61fee39e7aed.subscribe());
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		final Maybe<FlowExecution> binding_90ab3f95_2826_4c20_b596_b2d76ea599d4 = binding_90ab3f95_2826_4c20_b596_b2d76ea599d4(execution, null, () -> binding_481a956b_43f6_4d34_884c_5b89346d2e3b.subscribe(), () -> binding_a7003a0b_b777_4b3b_b4db_11cb8718dc49.subscribe());
		
		return Maybe.zipArray(a -> execution, binding_90ab3f95_2826_4c20_b596_b2d76ea599d4);
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