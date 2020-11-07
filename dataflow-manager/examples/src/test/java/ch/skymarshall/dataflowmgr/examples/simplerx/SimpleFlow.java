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

		private DataPointState state_binding_257c0b50_22a2_421a_a79b_987689cd5a65 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding257c0b5022a2421aA79b987689cd5a65(DataPointState state_binding_257c0b50_22a2_421a_a79b_987689cd5a65) {
		    this.state_binding_257c0b50_22a2_421a_a79b_987689cd5a65 = state_binding_257c0b50_22a2_421a_a79b_987689cd5a65;
		}
		private synchronized boolean canTriggerBinding257c0b5022a2421aA79b987689cd5a65() {
		    if (this.state_binding_257c0b50_22a2_421a_a79b_987689cd5a65 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_257c0b50_22a2_421a_a79b_987689cd5a65 = DataPointState.TRIGGERING;
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
		
		private DataPointState state_binding_c7ca8834_6f34_4de6_939f_6c10df037be1 = DataPointState.NOT_TRIGGERED;
		private void setStateBindingC7ca88346f344de6939f6c10df037be1(DataPointState state_binding_c7ca8834_6f34_4de6_939f_6c10df037be1) {
		    this.state_binding_c7ca8834_6f34_4de6_939f_6c10df037be1 = state_binding_c7ca8834_6f34_4de6_939f_6c10df037be1;
		}
		private synchronized boolean canTriggerBindingC7ca88346f344de6939f6c10df037be1() {
		    if (this.state_binding_c7ca8834_6f34_4de6_939f_6c10df037be1 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_c7ca8834_6f34_4de6_939f_6c10df037be1 = DataPointState.TRIGGERING;
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
		
		private java.lang.String simpleExternalAdapter_enhancementc7ca8834_6f34_4de6_939f_6c10df037be1;
		private void setSimpleExternalAdapterEnhancementc7ca88346f344de6939f6c10df037be1(java.lang.String simpleExternalAdapter_enhancementc7ca8834_6f34_4de6_939f_6c10df037be1) {
		    this.simpleExternalAdapter_enhancementc7ca8834_6f34_4de6_939f_6c10df037be1 = simpleExternalAdapter_enhancementc7ca8834_6f34_4de6_939f_6c10df037be1;
		}
		
		private DataPointState state_binding_7c79fcff_3cbf_42fb_af7d_148c1806021a = DataPointState.NOT_TRIGGERED;
		private void setStateBinding7c79fcff3cbf42fbAf7d148c1806021a(DataPointState state_binding_7c79fcff_3cbf_42fb_af7d_148c1806021a) {
		    this.state_binding_7c79fcff_3cbf_42fb_af7d_148c1806021a = state_binding_7c79fcff_3cbf_42fb_af7d_148c1806021a;
		}
		private synchronized boolean canTriggerBinding7c79fcff3cbf42fbAf7d148c1806021a() {
		    if (this.state_binding_7c79fcff_3cbf_42fb_af7d_148c1806021a == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_7c79fcff_3cbf_42fb_af7d_148c1806021a = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_binding_6ecaf727_f167_46b7_b7b0_ff208197fef4 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding6ecaf727F16746b7B7b0Ff208197fef4(DataPointState state_binding_6ecaf727_f167_46b7_b7b0_ff208197fef4) {
		    this.state_binding_6ecaf727_f167_46b7_b7b0_ff208197fef4 = state_binding_6ecaf727_f167_46b7_b7b0_ff208197fef4;
		}
		private synchronized boolean canTriggerBinding6ecaf727F16746b7B7b0Ff208197fef4() {
		    if (this.state_binding_6ecaf727_f167_46b7_b7b0_ff208197fef4 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_6ecaf727_f167_46b7_b7b0_ff208197fef4 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		
	}

	// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
	private Maybe<FlowExecution> binding_257c0b50_22a2_421a_a79b_987689cd5a65(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding257c0b5022a2421aA79b987689cd5a65(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding257c0b5022a2421aA79b987689cd5a65(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setSimpleServiceInit(this.simpleService.init(f.inputDataPoint)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = callService;
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> f.canTriggerBinding257c0b5022a2421aA79b987689cd5a65()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_c7ca8834_6f34_4de6_939f_6c10df037be1(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_ea909653_a321_4cef_b30c_b5603cf302ab = Maybe.just(execution)        .map(f -> this.simpleExternalAdapter.enhancement(f.simpleService_init))
	        .doOnSuccess(execution::setSimpleExternalAdapterEnhancementc7ca88346f344de6939f6c10df037be1)
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingC7ca88346f344de6939f6c10df037be1(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingC7ca88346f344de6939f6c10df037be1(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setEnhanced(this.simpleService.enhance(f.simpleService_init,f.simpleExternalAdapter_enhancementc7ca8834_6f34_4de6_939f_6c10df037be1)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_ea909653_a321_4cef_b30c_b5603cf302ab, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    final Maybe<Boolean> activator_2a37ef87_b837_4a08_8dfe_75fc32b2652d = Maybe.just(execution)
	        .map(f -> this.simpleServiceConditions.isEnhanceEnabled(f.simpleService_init))
	        .subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> activationCheck = Maybe.just(true)
	        .zipWith(activator_2a37ef87_b837_4a08_8dfe_75fc32b2652d, (u, r) -> u.booleanValue() && r.booleanValue())
	        .mapOptional(b -> b ? Optional.of(execution) : Optional.empty())
	        .flatMap(e -> callAdaptersAndServiceConst)
	        .doOnComplete(() -> { execution.setStateBindingC7ca88346f344de6939f6c10df037be1(DataPointState.TRIGGERED); execution.setStateEnhanced(DataPointState.SKIPPED); })
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init || DataPointState.SKIPPED == f.state_simpleService_init))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingC7ca88346f344de6939f6c10df037be1()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> activationCheck.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_7c79fcff_3cbf_42fb_af7d_148c1806021a(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding7c79fcff3cbf42fbAf7d148c1806021a(DataPointState.TRIGGERED))
	        .mapOptional(f -> DataPointState.SKIPPED == f.state_enhanced?Optional.of(f):Optional.empty())
	        .doOnComplete(() -> execution.setStateBinding7c79fcff3cbf42fbAf7d148c1806021a(DataPointState.SKIPPED))
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
	        .mapOptional(f -> f.canTriggerBinding7c79fcff3cbf42fbAf7d148c1806021a()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- enhanced -> exit -> exit -------------------------
	private Maybe<FlowExecution> binding_6ecaf727_f167_46b7_b7b0_ff208197fef4(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_b7813660_5b2c_4f1a_b740_81dd59fb3287 = Maybe.just(execution)        .doOnSuccess(f -> this.simpleExternalAdapter.display(f.enhanced))
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding6ecaf727F16746b7B7b0Ff208197fef4(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding6ecaf727F16746b7B7b0Ff208197fef4(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setStateBinding6ecaf727F16746b7B7b0Ff208197fef4(DataPointState.TRIGGERED))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_b7813660_5b2c_4f1a_b740_81dd59fb3287, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_enhanced || DataPointState.SKIPPED == f.state_enhanced))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBinding6ecaf727F16746b7B7b0Ff208197fef4()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	

	public Maybe<FlowExecution> execute(java.lang.String inputDataPoint,	
		final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> exitModifier) {
		
		final FlowExecution execution = new FlowExecution(inputDataPoint);
		// ------------------------- enhanced -> exit -> exit -------------------------
		final Maybe<FlowExecution> binding_6ecaf727_f167_46b7_b7b0_ff208197fef4 = binding_6ecaf727_f167_46b7_b7b0_ff208197fef4(execution, exitModifier);
		// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_7c79fcff_3cbf_42fb_af7d_148c1806021a = binding_7c79fcff_3cbf_42fb_af7d_148c1806021a(execution, null, () -> binding_6ecaf727_f167_46b7_b7b0_ff208197fef4.subscribe());
		// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_c7ca8834_6f34_4de6_939f_6c10df037be1 = binding_c7ca8834_6f34_4de6_939f_6c10df037be1(execution, null, () -> binding_7c79fcff_3cbf_42fb_af7d_148c1806021a.subscribe(), () -> binding_6ecaf727_f167_46b7_b7b0_ff208197fef4.subscribe());
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		final Maybe<FlowExecution> binding_257c0b50_22a2_421a_a79b_987689cd5a65 = binding_257c0b50_22a2_421a_a79b_987689cd5a65(execution, null, () -> binding_c7ca8834_6f34_4de6_939f_6c10df037be1.subscribe(), () -> binding_7c79fcff_3cbf_42fb_af7d_148c1806021a.subscribe());
		
		return Maybe.zipArray(a -> execution, binding_257c0b50_22a2_421a_a79b_987689cd5a65);
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