// File generated from template
package ch.skymarshall.dataflowmgr.examples.simplerx;

import org.junit.Test;
import org.junit.Assert;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import java.util.Optional;


public class SimpleFlow extends ch.skymarshall.dataflowmgr.examples.simple.AbstractFlow {

	public class FlowExecution {
	    private final java.lang.String inputDataPoint;
		
		public FlowExecution(java.lang.String inputDataPoint) {
			this.inputDataPoint = inputDataPoint;
		}

		private ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init;
		private void setSimpleServiceInit(ch.skymarshall.dataflowmgr.examples.simple.dto.MyData simpleService_init) {
		    this.simpleService_init = simpleService_init;
		}
		
		private java.lang.String simpleExternalAdapter_enhancement83faca4f_19cc_4ea2_b81f_5e4a1fbae85f;
		private void setSimpleExternalAdapterEnhancement83faca4f19cc4ea2B81f5e4a1fbae85f(java.lang.String simpleExternalAdapter_enhancement83faca4f_19cc_4ea2_b81f_5e4a1fbae85f) {
		    this.simpleExternalAdapter_enhancement83faca4f_19cc_4ea2_b81f_5e4a1fbae85f = simpleExternalAdapter_enhancement83faca4f_19cc_4ea2_b81f_5e4a1fbae85f;
		}
		private ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced;
		private void setEnhanced(ch.skymarshall.dataflowmgr.examples.simple.dto.MyData enhanced) {
		    this.enhanced = enhanced;
		}
		
		private boolean enhanced_available;
		private void setEnhancedAvailable(boolean enhanced_available) {
		    this.enhanced_available = enhanced_available;
		}
		
		

	}

	// ------------------------- inputDataPoint -> simpleService.init -> simpleService_init -------------------------
	private Maybe<FlowExecution> binding_b44df766_dd27_4a9b_8c0b_d5761dff039a(FlowExecution execution) {
	    final Maybe<FlowExecution> call = Single.just(execution)
	        .subscribeOn(Schedulers.computation())
	        .doOnSuccess(f -> f.setSimpleServiceInit(this.simpleService.init(f.inputDataPoint)))
	                .toMaybe();
	    return call;
	}
	
	// ------------------------- simpleService_init -> simpleService.enhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_83faca4f_19cc_4ea2_b81f_5e4a1fbae85f(FlowExecution execution) {
	    final Single<?> adapter_994df79b_b13d_4a2b_8e64_2390b578722c = Single.just(execution) //
	        .subscribeOn(Schedulers.io())
	        .map(f -> this.simpleExternalAdapter.enhancement(f.simpleService_init))
	        .subscribeOn(Schedulers.computation())
	        .doOnSuccess(execution::setSimpleExternalAdapterEnhancement83faca4f19cc4ea2B81f5e4a1fbae85f);
	
	    final Maybe<FlowExecution> call = Single.zipArray(a -> execution, adapter_994df79b_b13d_4a2b_8e64_2390b578722c)
	        .subscribeOn(Schedulers.computation())
	        .doOnSuccess(f -> f.setEnhanced(this.simpleService.enhance(f.simpleService_init,f.simpleExternalAdapter_enhancement83faca4f_19cc_4ea2_b81f_5e4a1fbae85f)))
	        .doOnSuccess(f -> f.setEnhancedAvailable(true))
	                .toMaybe();
	    final Single<Boolean> activator_d7889bc3_de07_4f82_a92d_7949bbd360db = Single.just(execution)
	        .subscribeOn(Schedulers.computation())
	        .map(f -> this.simpleServiceConditions.isEnhanceEnabled(f.simpleService_init));
	
	    return Single.zipArray(a -> Stream.of(a).map(b->(Boolean)b).allMatch(b->b), activator_d7889bc3_de07_4f82_a92d_7949bbd360db)
	        .toMaybe()
	        .mapOptional(b-> b ? Optional.of(execution) : Optional.empty())
	        .flatMap(e -> call);
	
	}
	
	// ------------------------- simpleService_init -> simpleService.noEnhance -> enhanced -------------------------
	private Maybe<FlowExecution> binding_dab7b62d_633b_4b66_91b2_8d346d077c76(FlowExecution execution) {
	    final Maybe<FlowExecution> call = Single.just(execution)
	        .subscribeOn(Schedulers.computation())
	        .mapOptional(f -> !f.enhanced_available?Optional.of(f):Optional.empty())
	        .doOnSuccess(f -> f.setEnhanced(this.simpleService.noEnhance(f.simpleService_init)));
	    return call;
	}
	
	// ------------------------- enhanced -> exit -> exit -------------------------
	private Maybe<FlowExecution> binding_978a7185_9f2e_4369_bf91_9312c14f02c1(FlowExecution execution) {
	    final Single<?> adapter_b4075e1a_43cd_4129_b21d_edbd1b2af5b4 = Single.just(execution) //
	        .subscribeOn(Schedulers.io())
	        .doOnSuccess(f -> this.simpleExternalAdapter.display(f.enhanced));
	
	    final Maybe<FlowExecution> call = Single.zipArray(a -> execution, adapter_b4075e1a_43cd_4129_b21d_edbd1b2af5b4).toMaybe();
	    return call;
	}
	
	

	public Maybe<FlowExecution> execute(java.lang.String inputDataPoint) {
		final FlowExecution execution = new FlowExecution(inputDataPoint);
		return Maybe.just(execution)
		.flatMap(s -> binding_b44df766_dd27_4a9b_8c0b_d5761dff039a(execution), e -> null, () -> binding_b44df766_dd27_4a9b_8c0b_d5761dff039a(execution))
		.flatMap(s -> binding_83faca4f_19cc_4ea2_b81f_5e4a1fbae85f(execution), e -> null, () -> binding_83faca4f_19cc_4ea2_b81f_5e4a1fbae85f(execution))
		.flatMap(s -> binding_dab7b62d_633b_4b66_91b2_8d346d077c76(execution), e -> null, () -> binding_dab7b62d_633b_4b66_91b2_8d346d077c76(execution))
		.flatMap(s -> binding_978a7185_9f2e_4369_bf91_9312c14f02c1(execution), e -> null, () -> binding_978a7185_9f2e_4369_bf91_9312c14f02c1(execution));
	}

	@Test	
	public void testFlow() {
		simpleExternalAdapter.reset();
		
	 	execute("Hello").blockingGet();
		Assert.assertEquals("Hello -> enhanced with World", simpleExternalAdapter.getOutput());
		
		execute("Hi").blockingGet();
		Assert.assertEquals("Hi -> enhanced with There", simpleExternalAdapter.getOutput());
		
		execute("Huh").blockingGet();
		Assert.assertEquals("Huh -> not enhanced", simpleExternalAdapter.getOutput());
	}

}