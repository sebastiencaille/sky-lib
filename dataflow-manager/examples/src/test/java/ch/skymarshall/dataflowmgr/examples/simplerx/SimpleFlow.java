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

		private DataPointState state_binding_2baa2ec0_4402_403e_847d_0ee809bd3d67 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding2baa2ec04402403e847d0ee809bd3d67(DataPointState state_binding_2baa2ec0_4402_403e_847d_0ee809bd3d67) {
		    this.state_binding_2baa2ec0_4402_403e_847d_0ee809bd3d67 = state_binding_2baa2ec0_4402_403e_847d_0ee809bd3d67;
		}
		private synchronized boolean canTriggerBinding2baa2ec04402403e847d0ee809bd3d67() {
		    if (this.state_binding_2baa2ec0_4402_403e_847d_0ee809bd3d67 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_2baa2ec0_4402_403e_847d_0ee809bd3d67 = DataPointState.TRIGGERING;
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
		
		private DataPointState state_binding_b4e5f882_84c0_476d_beaf_dd5a740ec0ec = DataPointState.NOT_TRIGGERED;
		private void setStateBindingB4e5f88284c0476dBeafDd5a740ec0ec(DataPointState state_binding_b4e5f882_84c0_476d_beaf_dd5a740ec0ec) {
		    this.state_binding_b4e5f882_84c0_476d_beaf_dd5a740ec0ec = state_binding_b4e5f882_84c0_476d_beaf_dd5a740ec0ec;
		}
		private synchronized boolean canTriggerBindingB4e5f88284c0476dBeafDd5a740ec0ec() {
		    if (this.state_binding_b4e5f882_84c0_476d_beaf_dd5a740ec0ec == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_b4e5f882_84c0_476d_beaf_dd5a740ec0ec = DataPointState.TRIGGERING;
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
		
		private java.lang.String simpleExternalAdapter_enhancementb4e5f882_84c0_476d_beaf_dd5a740ec0ec;
		private void setSimpleExternalAdapterEnhancementb4e5f88284c0476dBeafDd5a740ec0ec(java.lang.String simpleExternalAdapter_enhancementb4e5f882_84c0_476d_beaf_dd5a740ec0ec) {
		    this.simpleExternalAdapter_enhancementb4e5f882_84c0_476d_beaf_dd5a740ec0ec = simpleExternalAdapter_enhancementb4e5f882_84c0_476d_beaf_dd5a740ec0ec;
		}
		
		private DataPointState state_binding_2c80b64f_0227_4e95_a8b7_a7f9ba1f2db9 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding2c80b64f02274e95A8b7A7f9ba1f2db9(DataPointState state_binding_2c80b64f_0227_4e95_a8b7_a7f9ba1f2db9) {
		    this.state_binding_2c80b64f_0227_4e95_a8b7_a7f9ba1f2db9 = state_binding_2c80b64f_0227_4e95_a8b7_a7f9ba1f2db9;
		}
		private synchronized boolean canTriggerBinding2c80b64f02274e95A8b7A7f9ba1f2db9() {
		    if (this.state_binding_2c80b64f_0227_4e95_a8b7_a7f9ba1f2db9 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_2c80b64f_0227_4e95_a8b7_a7f9ba1f2db9 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_binding_81767e8d_b71b_4b8e_bd10_77f958088c44 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding81767e8dB71b4b8eBd1077f958088c44(DataPointState state_binding_81767e8d_b71b_4b8e_bd10_77f958088c44) {
		    this.state_binding_81767e8d_b71b_4b8e_bd10_77f958088c44 = state_binding_81767e8d_b71b_4b8e_bd10_77f958088c44;
		}
		private synchronized boolean canTriggerBinding81767e8dB71b4b8eBd1077f958088c44() {
		    if (this.state_binding_81767e8d_b71b_4b8e_bd10_77f958088c44 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_81767e8d_b71b_4b8e_bd10_77f958088c44 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		
	}

	// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
	private Maybe<FlowExecution> binding_2baa2ec0_4402_403e_847d_0ee809bd3d67(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding2baa2ec04402403e847d0ee809bd3d67(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding2baa2ec04402403e847d0ee809bd3d67(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setSimpleServiceInit(this.simpleService.init(f.inputDataPoint)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = callService;
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> f.canTriggerBinding2baa2ec04402403e847d0ee809bd3d67()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_b4e5f882_84c0_476d_beaf_dd5a740ec0ec(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_627777ba_f602_41bf_a77c_6ebe3ec45ead = Maybe.just(execution)        .map(f -> this.simpleExternalAdapter.enhancement(f.simpleService_init))
	        .doOnSuccess(execution::setSimpleExternalAdapterEnhancementb4e5f88284c0476dBeafDd5a740ec0ec)
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingB4e5f88284c0476dBeafDd5a740ec0ec(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingB4e5f88284c0476dBeafDd5a740ec0ec(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setEnhanced(this.simpleService.enhance(f.simpleService_init,f.simpleExternalAdapter_enhancementb4e5f882_84c0_476d_beaf_dd5a740ec0ec)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_627777ba_f602_41bf_a77c_6ebe3ec45ead, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    final Maybe<Boolean> activator_c00f7abb_03fd_4bda_9093_e7c8842401f2 = Maybe.just(execution)
	        .map(f -> this.simpleServiceConditions.isEnhanceEnabled(f.simpleService_init))
	        .subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> activationCheck = Maybe.just(true)
	        .zipWith(activator_c00f7abb_03fd_4bda_9093_e7c8842401f2, (u, r) -> u.booleanValue() && r.booleanValue())
	        .mapOptional(b -> b ? Optional.of(execution) : Optional.empty())
	        .flatMap(e -> callAdaptersAndServiceConst)
	        .doOnComplete(() -> { execution.setStateBindingB4e5f88284c0476dBeafDd5a740ec0ec(DataPointState.TRIGGERED); execution.setStateEnhanced(DataPointState.SKIPPED); })
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init || DataPointState.SKIPPED == f.state_simpleService_init))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingB4e5f88284c0476dBeafDd5a740ec0ec()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> activationCheck.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_2c80b64f_0227_4e95_a8b7_a7f9ba1f2db9(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding2c80b64f02274e95A8b7A7f9ba1f2db9(DataPointState.TRIGGERED))
	        .mapOptional(f -> DataPointState.SKIPPED == f.state_enhanced?Optional.of(f):Optional.empty())
	        .doOnComplete(() -> execution.setStateBinding2c80b64f02274e95A8b7A7f9ba1f2db9(DataPointState.SKIPPED))
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
	        .mapOptional(f -> f.canTriggerBinding2c80b64f02274e95A8b7A7f9ba1f2db9()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- enhanced -> exit -> exit -------------------------
	private Maybe<FlowExecution> binding_81767e8d_b71b_4b8e_bd10_77f958088c44(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_f1e88b86_2b62_4d4b_bab9_d86ec5d6fb37 = Maybe.just(execution)        .doOnSuccess(f -> this.simpleExternalAdapter.display(f.enhanced))
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding81767e8dB71b4b8eBd1077f958088c44(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding81767e8dB71b4b8eBd1077f958088c44(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setStateBinding81767e8dB71b4b8eBd1077f958088c44(DataPointState.TRIGGERED))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_f1e88b86_2b62_4d4b_bab9_d86ec5d6fb37, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_enhanced || DataPointState.SKIPPED == f.state_enhanced))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBinding81767e8dB71b4b8eBd1077f958088c44()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	

	public Maybe<FlowExecution> execute(java.lang.String inputDataPoint,	
		final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> exitModifier) {
		
		final FlowExecution execution = new FlowExecution(inputDataPoint);
		// ------------------------- enhanced -> exit -> exit -------------------------
		final Maybe<FlowExecution> binding_81767e8d_b71b_4b8e_bd10_77f958088c44 = binding_81767e8d_b71b_4b8e_bd10_77f958088c44(execution, exitModifier);
		// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_2c80b64f_0227_4e95_a8b7_a7f9ba1f2db9 = binding_2c80b64f_0227_4e95_a8b7_a7f9ba1f2db9(execution, null, () -> binding_81767e8d_b71b_4b8e_bd10_77f958088c44.subscribe());
		// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_b4e5f882_84c0_476d_beaf_dd5a740ec0ec = binding_b4e5f882_84c0_476d_beaf_dd5a740ec0ec(execution, null, () -> binding_2c80b64f_0227_4e95_a8b7_a7f9ba1f2db9.subscribe(), () -> binding_81767e8d_b71b_4b8e_bd10_77f958088c44.subscribe());
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		final Maybe<FlowExecution> binding_2baa2ec0_4402_403e_847d_0ee809bd3d67 = binding_2baa2ec0_4402_403e_847d_0ee809bd3d67(execution, null, () -> binding_b4e5f882_84c0_476d_beaf_dd5a740ec0ec.subscribe(), () -> binding_2c80b64f_0227_4e95_a8b7_a7f9ba1f2db9.subscribe());
		
		return Maybe.zipArray(a -> execution, binding_2baa2ec0_4402_403e_847d_0ee809bd3d67);
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