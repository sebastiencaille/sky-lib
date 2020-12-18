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

		private DataPointState state_binding_e434cfa1_2612_4a5f_9731_4060ec68eb67 = DataPointState.NOT_TRIGGERED;
		private void setStateBindingE434cfa126124a5f97314060ec68eb67(DataPointState state_binding_e434cfa1_2612_4a5f_9731_4060ec68eb67) {
		    this.state_binding_e434cfa1_2612_4a5f_9731_4060ec68eb67 = state_binding_e434cfa1_2612_4a5f_9731_4060ec68eb67;
		}
		private synchronized boolean canTriggerBindingE434cfa126124a5f97314060ec68eb67() {
		    if (this.state_binding_e434cfa1_2612_4a5f_9731_4060ec68eb67 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_e434cfa1_2612_4a5f_9731_4060ec68eb67 = DataPointState.TRIGGERING;
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
		
		private DataPointState state_binding_c0f38ebe_4902_4054_85ff_5dd949ea2697 = DataPointState.NOT_TRIGGERED;
		private void setStateBindingC0f38ebe4902405485ff5dd949ea2697(DataPointState state_binding_c0f38ebe_4902_4054_85ff_5dd949ea2697) {
		    this.state_binding_c0f38ebe_4902_4054_85ff_5dd949ea2697 = state_binding_c0f38ebe_4902_4054_85ff_5dd949ea2697;
		}
		private synchronized boolean canTriggerBindingC0f38ebe4902405485ff5dd949ea2697() {
		    if (this.state_binding_c0f38ebe_4902_4054_85ff_5dd949ea2697 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_c0f38ebe_4902_4054_85ff_5dd949ea2697 = DataPointState.TRIGGERING;
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
		
		private java.lang.String simpleExternalAdapter_getCompletionc0f38ebe_4902_4054_85ff_5dd949ea2697;
		private void setSimpleExternalAdapterGetCompletionc0f38ebe4902405485ff5dd949ea2697(java.lang.String simpleExternalAdapter_getCompletionc0f38ebe_4902_4054_85ff_5dd949ea2697) {
		    this.simpleExternalAdapter_getCompletionc0f38ebe_4902_4054_85ff_5dd949ea2697 = simpleExternalAdapter_getCompletionc0f38ebe_4902_4054_85ff_5dd949ea2697;
		}
		
		private DataPointState state_binding_57c87497_f1ed_438a_bd60_c3f1cc15af7e = DataPointState.NOT_TRIGGERED;
		private void setStateBinding57c87497F1ed438aBd60C3f1cc15af7e(DataPointState state_binding_57c87497_f1ed_438a_bd60_c3f1cc15af7e) {
		    this.state_binding_57c87497_f1ed_438a_bd60_c3f1cc15af7e = state_binding_57c87497_f1ed_438a_bd60_c3f1cc15af7e;
		}
		private synchronized boolean canTriggerBinding57c87497F1ed438aBd60C3f1cc15af7e() {
		    if (this.state_binding_57c87497_f1ed_438a_bd60_c3f1cc15af7e == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_57c87497_f1ed_438a_bd60_c3f1cc15af7e = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_binding_33684897_c3a7_40fa_aa90_49eebe018796 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding33684897C3a740faAa9049eebe018796(DataPointState state_binding_33684897_c3a7_40fa_aa90_49eebe018796) {
		    this.state_binding_33684897_c3a7_40fa_aa90_49eebe018796 = state_binding_33684897_c3a7_40fa_aa90_49eebe018796;
		}
		private synchronized boolean canTriggerBinding33684897C3a740faAa9049eebe018796() {
		    if (this.state_binding_33684897_c3a7_40fa_aa90_49eebe018796 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_33684897_c3a7_40fa_aa90_49eebe018796 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		
	}

	// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
	private Maybe<FlowExecution> binding_e434cfa1_2612_4a5f_9731_4060ec68eb67(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingE434cfa126124a5f97314060ec68eb67(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingE434cfa126124a5f97314060ec68eb67(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setSimpleServiceInit(this.simpleService.init(f.inputDataPoint)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = callService;
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> f.canTriggerBindingE434cfa126124a5f97314060ec68eb67()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.complete -> complete -------------------------
	private Maybe<FlowExecution> binding_c0f38ebe_4902_4054_85ff_5dd949ea2697(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_c65da0c6_ad19_46b2_a932_32801a2da459 = Maybe.just(execution)
	        .map(f -> this.simpleExternalAdapter.getCompletion(f.simpleService_init))
	        .doOnSuccess(execution::setSimpleExternalAdapterGetCompletionc0f38ebe4902405485ff5dd949ea2697)
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBindingC0f38ebe4902405485ff5dd949ea2697(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBindingC0f38ebe4902405485ff5dd949ea2697(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setComplete(this.simpleService.complete(f.simpleService_init,f.simpleExternalAdapter_getCompletionc0f38ebe_4902_4054_85ff_5dd949ea2697)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_c65da0c6_ad19_46b2_a932_32801a2da459, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    final Maybe<Boolean> activator_98a02654_2639_4c0a_80e2_d2fb85f932a2 = Maybe.just(execution)
	        .map(f -> this.simpleFlowConditions.mustComplete(f.simpleService_init))
	        .subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> activationCheck = Maybe.just(true)
	        .zipWith(activator_98a02654_2639_4c0a_80e2_d2fb85f932a2, (u, r) -> u.booleanValue() && r.booleanValue())
	        .mapOptional(b -> b ? Optional.of(execution) : Optional.empty())
	        .flatMap(e -> callAdaptersAndServiceConst)
	        .doOnComplete(() -> { execution.setStateBindingC0f38ebe4902405485ff5dd949ea2697(DataPointState.TRIGGERED); execution.setStateComplete(DataPointState.SKIPPED); })
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingC0f38ebe4902405485ff5dd949ea2697()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> activationCheck.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.keepAsIs -> complete -------------------------
	private Maybe<FlowExecution> binding_57c87497_f1ed_438a_bd60_c3f1cc15af7e(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding57c87497F1ed438aBd60C3f1cc15af7e(DataPointState.TRIGGERED))
	        .mapOptional(f -> DataPointState.SKIPPED == f.state_complete?Optional.of(f):Optional.empty())
	        .doOnComplete(() -> execution.setStateBinding57c87497F1ed438aBd60C3f1cc15af7e(DataPointState.SKIPPED))
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
	        .mapOptional(f -> f.canTriggerBinding57c87497F1ed438aBd60C3f1cc15af7e()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	// ------------------------- complete -> exit -> exit -------------------------
	private Maybe<FlowExecution> binding_33684897_c3a7_40fa_aa90_49eebe018796(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_4008fffd_b3b6_4b47_850c_70a8d6e66ec9 = Maybe.just(execution)
	        .doOnSuccess(f -> this.simpleExternalAdapter.display(f.complete))
	        .subscribeOn(Schedulers.io());
	
	    Maybe<FlowExecution> callService = Maybe.just(execution)
	        .doOnSuccess(e -> e.setStateBinding33684897C3a740faAa9049eebe018796(DataPointState.TRIGGERED))
	        .doOnComplete(() -> execution.setStateBinding33684897C3a740faAa9049eebe018796(DataPointState.SKIPPED))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))
	        .subscribeOn(Schedulers.computation());
	    if (callModifier != null) {
	        callService = callModifier.apply(callService);
	    }
	    callService.subscribeOn(Schedulers.computation());
	
	    final Maybe<FlowExecution> callServiceConst = callService;
	    final Maybe<FlowExecution> callAdaptersAndServiceConst = Maybe.just(execution)
	        .zipWith(adapter_4008fffd_b3b6_4b47_850c_70a8d6e66ec9, (r, s) -> execution)
	        .flatMap(r -> callServiceConst);
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_complete))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBinding33684897C3a740faAa9049eebe018796()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> callAdaptersAndServiceConst.subscribe());
	    return first;
	}
	
	

	public Maybe<FlowExecution> execute(java.lang.String inputDataPoint,	
		final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> exitModifier) {
		
		final FlowExecution execution = new FlowExecution(inputDataPoint);
		// ------------------------- complete -> exit -> exit -------------------------
		final Maybe<FlowExecution> binding_33684897_c3a7_40fa_aa90_49eebe018796 = binding_33684897_c3a7_40fa_aa90_49eebe018796(execution, exitModifier);
		// ------------------------- simpleService_init -> simpleService.keepAsIs -> complete -------------------------
		final Maybe<FlowExecution> binding_57c87497_f1ed_438a_bd60_c3f1cc15af7e = binding_57c87497_f1ed_438a_bd60_c3f1cc15af7e(execution, null, () -> binding_33684897_c3a7_40fa_aa90_49eebe018796.subscribe());
		// ------------------------- simpleService_init -> simpleService.complete -> complete -------------------------
		final Maybe<FlowExecution> binding_c0f38ebe_4902_4054_85ff_5dd949ea2697 = binding_c0f38ebe_4902_4054_85ff_5dd949ea2697(execution, null, () -> binding_57c87497_f1ed_438a_bd60_c3f1cc15af7e.subscribe(), () -> binding_33684897_c3a7_40fa_aa90_49eebe018796.subscribe());
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		final Maybe<FlowExecution> binding_e434cfa1_2612_4a5f_9731_4060ec68eb67 = binding_e434cfa1_2612_4a5f_9731_4060ec68eb67(execution, null, () -> binding_c0f38ebe_4902_4054_85ff_5dd949ea2697.subscribe(), () -> binding_57c87497_f1ed_438a_bd60_c3f1cc15af7e.subscribe());
		
		return Maybe.zipArray(a -> execution, binding_e434cfa1_2612_4a5f_9731_4060ec68eb67);
	}

	private void runTest(final String in, final String out) throws InterruptedException {
		final Semaphore finished = new Semaphore(0);
		FlowReport.report.clear();
		simpleExternalAdapter.reset();
		final FlowExecution result = execute(in, e -> e.doOnSuccess(r -> finished.release())).blockingGet();
		Assert.assertTrue(finished.tryAcquire(100, TimeUnit.MILLISECONDS));
		Assert.assertEquals(out, simpleExternalAdapter.getOutput());
		System.out.println(FlowReport.report);
	}

	@Test
	public void testFlow() throws InterruptedException {
		runTest("Hello", "Hello -> complete with World");
		runTest("Hi", "Hi -> complete with There");
		runTest("Huh", "Huh -> keep as is");
	}
}