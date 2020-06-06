// File generated from template
package ch.skymarshall.dataflowmgr.examples.simplerx;

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

		private DataPointState state_binding_6a0b3c38_e889_456f_b455_8dd849c57ecd = DataPointState.NOT_TRIGGERED;
		private void setStateBinding6a0b3c38E889456fB4558dd849c57ecd(DataPointState state_binding_6a0b3c38_e889_456f_b455_8dd849c57ecd) {
		    this.state_binding_6a0b3c38_e889_456f_b455_8dd849c57ecd = state_binding_6a0b3c38_e889_456f_b455_8dd849c57ecd;
		}
		private synchronized boolean canTriggerBinding6a0b3c38E889456fB4558dd849c57ecd() {
		    if (this.state_binding_6a0b3c38_e889_456f_b455_8dd849c57ecd == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_6a0b3c38_e889_456f_b455_8dd849c57ecd = DataPointState.TRIGGERING;
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
		
		private DataPointState state_binding_7dab3daa_18fb_4b1b_9be6_0e2abd148cdd = DataPointState.NOT_TRIGGERED;
		private void setStateBinding7dab3daa18fb4b1b9be60e2abd148cdd(DataPointState state_binding_7dab3daa_18fb_4b1b_9be6_0e2abd148cdd) {
		    this.state_binding_7dab3daa_18fb_4b1b_9be6_0e2abd148cdd = state_binding_7dab3daa_18fb_4b1b_9be6_0e2abd148cdd;
		}
		private synchronized boolean canTriggerBinding7dab3daa18fb4b1b9be60e2abd148cdd() {
		    if (this.state_binding_7dab3daa_18fb_4b1b_9be6_0e2abd148cdd == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_7dab3daa_18fb_4b1b_9be6_0e2abd148cdd = DataPointState.TRIGGERING;
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
		
		private DataPointState state_simpleExternalAdapter_enhancement7dab3daa_18fb_4b1b_9be6_0e2abd148cdd = DataPointState.NOT_TRIGGERED;
		private java.lang.String simpleExternalAdapter_enhancement7dab3daa_18fb_4b1b_9be6_0e2abd148cdd;
		private void setStateSimpleExternalAdapterEnhancement7dab3daa18fb4b1b9be60e2abd148cdd(DataPointState state_simpleExternalAdapter_enhancement7dab3daa_18fb_4b1b_9be6_0e2abd148cdd) {
		    this.state_simpleExternalAdapter_enhancement7dab3daa_18fb_4b1b_9be6_0e2abd148cdd = state_simpleExternalAdapter_enhancement7dab3daa_18fb_4b1b_9be6_0e2abd148cdd;
		}
		private void setSimpleExternalAdapterEnhancement7dab3daa18fb4b1b9be60e2abd148cdd(java.lang.String simpleExternalAdapter_enhancement7dab3daa_18fb_4b1b_9be6_0e2abd148cdd) {
		    this.simpleExternalAdapter_enhancement7dab3daa_18fb_4b1b_9be6_0e2abd148cdd = simpleExternalAdapter_enhancement7dab3daa_18fb_4b1b_9be6_0e2abd148cdd;
		    this.state_simpleExternalAdapter_enhancement7dab3daa_18fb_4b1b_9be6_0e2abd148cdd = DataPointState.TRIGGERED;
		}
		
		private DataPointState state_binding_346f7475_11fc_4040_aca1_869acdcc2f98 = DataPointState.NOT_TRIGGERED;
		private void setStateBinding346f747511fc4040Aca1869acdcc2f98(DataPointState state_binding_346f7475_11fc_4040_aca1_869acdcc2f98) {
		    this.state_binding_346f7475_11fc_4040_aca1_869acdcc2f98 = state_binding_346f7475_11fc_4040_aca1_869acdcc2f98;
		}
		private synchronized boolean canTriggerBinding346f747511fc4040Aca1869acdcc2f98() {
		    if (this.state_binding_346f7475_11fc_4040_aca1_869acdcc2f98 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_346f7475_11fc_4040_aca1_869acdcc2f98 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		private DataPointState state_binding_f23c10b1_f316_49bd_9aac_8020d5733bb8 = DataPointState.NOT_TRIGGERED;
		private void setStateBindingF23c10b1F31649bd9aac8020d5733bb8(DataPointState state_binding_f23c10b1_f316_49bd_9aac_8020d5733bb8) {
		    this.state_binding_f23c10b1_f316_49bd_9aac_8020d5733bb8 = state_binding_f23c10b1_f316_49bd_9aac_8020d5733bb8;
		}
		private synchronized boolean canTriggerBindingF23c10b1F31649bd9aac8020d5733bb8() {
		    if (this.state_binding_f23c10b1_f316_49bd_9aac_8020d5733bb8 == DataPointState.NOT_TRIGGERED) {
		        this.state_binding_f23c10b1_f316_49bd_9aac_8020d5733bb8 = DataPointState.TRIGGERING;
		        return true;
		    }
		    return false;
		}
		
	}

	// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
	private Maybe<FlowExecution> binding_6a0b3c38_e889_456f_b455_8dd849c57ecd(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    // All dependencies have been either triggered or skipped
	    Maybe<FlowExecution> call = Maybe.just(execution)
	        .subscribeOn(Schedulers.computation())
	        .doOnComplete(() -> execution.setStateBinding6a0b3c38E889456fB4558dd849c57ecd(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setSimpleServiceInit(this.simpleService.init(f.inputDataPoint)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	    if (callModifier != null) {
	        call = callModifier.apply(call);
	    }
	    final Maybe<FlowExecution> last = call;
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> f.canTriggerBinding6a0b3c38E889456fB4558dd849c57ecd()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> last.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_7dab3daa_18fb_4b1b_9be6_0e2abd148cdd(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_925cc15f_34b7_48cf_8b44_f3c1de8d4efe = Maybe.just(execution)
	        .subscribeOn(Schedulers.io())
	        .map(f -> this.simpleExternalAdapter.enhancement(f.simpleService_init))
	        .subscribeOn(Schedulers.computation())
	        .doOnSuccess(execution::setSimpleExternalAdapterEnhancement7dab3daa18fb4b1b9be60e2abd148cdd);
	
	    // All dependencies have been either triggered or skipped
	    Maybe<FlowExecution> call = Maybe.just(execution)
	        .subscribeOn(Schedulers.computation())
	        .zipWith(adapter_925cc15f_34b7_48cf_8b44_f3c1de8d4efe, (r, s) -> execution)
	        .doOnComplete(() -> execution.setStateBinding7dab3daa18fb4b1b9be60e2abd148cdd(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setEnhanced(this.simpleService.enhance(f.simpleService_init,f.simpleExternalAdapter_enhancement7dab3daa_18fb_4b1b_9be6_0e2abd148cdd)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	    if (callModifier != null) {
	        call = callModifier.apply(call);
	    }
	    final Maybe<FlowExecution> last = call;
	    final Maybe<Boolean> activator_bd30d4b8_beea_4b7c_8e19_2c9e634ac8ca = Maybe.just(execution)
	        .subscribeOn(Schedulers.computation())
	        .map(f -> this.simpleServiceConditions.isEnhanceEnabled(f.simpleService_init));
	
	    final Maybe<FlowExecution> doActivation = Maybe.just(true)
	        .zipWith(activator_bd30d4b8_beea_4b7c_8e19_2c9e634ac8ca, (u, r) -> u.booleanValue() && r.booleanValue())
	        .mapOptional(b -> b ? Optional.of(execution) : Optional.empty())
	        .doOnSuccess(e ->  { execution.setStateBinding7dab3daa18fb4b1b9be60e2abd148cdd(DataPointState.TRIGGERED); last.subscribe(); })
	        .doOnComplete(() -> { execution.setStateBinding7dab3daa18fb4b1b9be60e2abd148cdd(DataPointState.TRIGGERED); execution.setStateEnhanced(DataPointState.SKIPPED); })
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init || DataPointState.SKIPPED == f.state_simpleService_init))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBinding7dab3daa18fb4b1b9be60e2abd148cdd()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> doActivation.subscribe());
	    return first;
	}
	
	// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_346f7475_11fc_4040_aca1_869acdcc2f98(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    // All dependencies have been either triggered or skipped
	    Maybe<FlowExecution> call = Maybe.just(execution)
	        .subscribeOn(Schedulers.computation())
	        .mapOptional(f -> DataPointState.SKIPPED == f.state_enhanced?Optional.of(f):Optional.empty())
	        .doOnComplete(() -> execution.setStateBinding346f747511fc4040Aca1869acdcc2f98(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setEnhanced(this.simpleService.noEnhance(f.simpleService_init)))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	    if (callModifier != null) {
	        call = callModifier.apply(call);
	    }
	    final Maybe<FlowExecution> last = call;
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_simpleService_init || DataPointState.SKIPPED == f.state_simpleService_init) && (DataPointState.TRIGGERED == f.state_enhanced || DataPointState.SKIPPED == f.state_enhanced))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBinding346f747511fc4040Aca1869acdcc2f98()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> last.subscribe());
	    return first;
	}
	
	// ------------------------- enhanced -> exit -> exit -------------------------
	private Maybe<FlowExecution> binding_f23c10b1_f316_49bd_9aac_8020d5733bb8(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks) {
	    final Maybe<?> adapter_6bc5be29_8e1f_4ed1_855c_5e75aa9c47ed = Maybe.just(execution)
	        .subscribeOn(Schedulers.io())
	        .doOnSuccess(f -> this.simpleExternalAdapter.display(f.enhanced));
	
	    // All dependencies have been either triggered or skipped
	    Maybe<FlowExecution> call = Maybe.just(execution)
	        .subscribeOn(Schedulers.computation())
	        .zipWith(adapter_6bc5be29_8e1f_4ed1_855c_5e75aa9c47ed, (r, s) -> execution)
	        .doOnComplete(() -> execution.setStateBindingF23c10b1F31649bd9aac8020d5733bb8(DataPointState.SKIPPED))
	        .doOnSuccess(f -> f.setStateBindingF23c10b1F31649bd9aac8020d5733bb8(DataPointState.TRIGGERED))
	        .doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run));
	    if (callModifier != null) {
	        call = callModifier.apply(call);
	    }
	    final Maybe<FlowExecution> last = call;
	    Maybe<FlowExecution> first = Maybe.just(execution)
	        .mapOptional(f -> ((DataPointState.TRIGGERED == f.state_enhanced || DataPointState.SKIPPED == f.state_enhanced))?Optional.of(execution):Optional.empty())
	        .mapOptional(f -> f.canTriggerBindingF23c10b1F31649bd9aac8020d5733bb8()?Optional.of(execution):Optional.empty())
	        .doOnSuccess(r -> last.subscribe());
	    return first;
	}
	
	

	public Maybe<FlowExecution> execute(java.lang.String inputDataPoint,	
		final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> exitModifier) {
		
		final FlowExecution execution = new FlowExecution(inputDataPoint);
		// ------------------------- enhanced -> exit -> exit -------------------------
		final Maybe<FlowExecution> binding_f23c10b1_f316_49bd_9aac_8020d5733bb8 = binding_f23c10b1_f316_49bd_9aac_8020d5733bb8(execution, exitModifier);
		// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_346f7475_11fc_4040_aca1_869acdcc2f98 = binding_346f7475_11fc_4040_aca1_869acdcc2f98(execution, null, () -> binding_f23c10b1_f316_49bd_9aac_8020d5733bb8.subscribe());
		// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
		final Maybe<FlowExecution> binding_7dab3daa_18fb_4b1b_9be6_0e2abd148cdd = binding_7dab3daa_18fb_4b1b_9be6_0e2abd148cdd(execution, null, () -> binding_346f7475_11fc_4040_aca1_869acdcc2f98.subscribe(), () -> binding_f23c10b1_f316_49bd_9aac_8020d5733bb8.subscribe());
		// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
		final Maybe<FlowExecution> binding_6a0b3c38_e889_456f_b455_8dd849c57ecd = binding_6a0b3c38_e889_456f_b455_8dd849c57ecd(execution, null, () -> binding_7dab3daa_18fb_4b1b_9be6_0e2abd148cdd.subscribe(), () -> binding_346f7475_11fc_4040_aca1_869acdcc2f98.subscribe());
		
		return Maybe.zipArray(a -> execution, binding_6a0b3c38_e889_456f_b455_8dd849c57ecd);
	}

	@Test	
	public void testFlow() throws InterruptedException  {
		Semaphore finished = new Semaphore(0);
		
		simpleExternalAdapter.reset();
		FlowExecution result1 = execute("Hello", e -> e.doOnSuccess(r -> finished.release())).blockingGet();
		Assert.assertTrue(finished.tryAcquire(500, TimeUnit.MILLISECONDS));
		Assert.assertFalse(finished.tryAcquire(500, TimeUnit.MILLISECONDS));
		Assert.assertEquals("Hello -> enhanced with World", simpleExternalAdapter.getOutput());
		
		simpleExternalAdapter.reset();
		FlowExecution result2 = execute("Hi", e -> e.doOnSuccess(r -> finished.release())).blockingGet();
		Assert.assertTrue(finished.tryAcquire(500, TimeUnit.MILLISECONDS));
		Assert.assertFalse(finished.tryAcquire(500, TimeUnit.MILLISECONDS));
		Assert.assertEquals("Hi -> enhanced with There", simpleExternalAdapter.getOutput());
		
		simpleExternalAdapter.reset();
		FlowExecution result3 = execute("Huh", e -> e.doOnSuccess(r -> finished.release())).blockingGet();
 		Assert.assertTrue(finished.tryAcquire(500, TimeUnit.MILLISECONDS));
		Assert.assertFalse(finished.tryAcquire(500, TimeUnit.MILLISECONDS));
		Assert.assertEquals("Huh -> not enhanced", simpleExternalAdapter.getOutput());
	}

}