package ch.scaille.dataflowmgr.generator.writers.javarx;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.CallContext;
import ch.scaille.dataflowmgr.generator.writers.AbstractJavaFlowVisitor;
import ch.scaille.dataflowmgr.model.Processor;
import ch.scaille.dataflowmgr.model.flowctrl.ConditionalFlowCtrl;
import ch.scaille.generators.util.JavaCodeGenerator;

import static ch.scaille.dataflowmgr.generator.writers.javarx.FlowToRXJavaVisitor.fieldNameOf;

public class ProcessorCallGenerator extends AbstractFlowGenerator {

	protected ProcessorCallGenerator(FlowToRXJavaVisitor visitor,
	                                 JavaCodeGenerator<RuntimeException> flowExecutionAttributes,
	                                 JavaCodeGenerator<RuntimeException> flowExecutionBuilder,
	                                 JavaCodeGenerator<RuntimeException> flowExecutionDependencies) {
		super(visitor, flowExecutionAttributes, flowExecutionBuilder, flowExecutionDependencies);
	}

	@Override
	public boolean matches(CallContext context) {
		return true;
	}

	@Override
	public void generate(BaseGenContext<GenContext> genContext, CallContext context) {

		for (var adapterCall : context.callAdapters) {
			final var adapterFieldName = fieldNameOf(context, adapterCall);
			visitor.availableVars.add(new AbstractJavaFlowVisitor.CallVariable(adapterCall, adapterFieldName));
			addCallBuilder(context, adapterFieldName, adapterCall.getCall(), "ADAPTER", "always()", adapterCall, null);
			flowExecutionDependencies.appendMethodCall(fieldNameOf(context.processor), "addDependency", adapterFieldName).eos();
			flowExecutionDependencies.appendMethodCall(adapterFieldName, "addOnSuccess","_ -> triggerProcess(%s).subscribe()".formatted(fieldNameOf(context.processor))).eos();
		}

		final var fieldName = fieldNameOf(context.processor);
		final var conditionFactory = context.processor.getRules().isEmpty() ? "always()" : "allActivationNoExclusions()";
		addCallBuilder(context, fieldName, context.processor.toString(), "PROCESSOR", conditionFactory, context.getProcessorCall(), null);
		visitor.availableVars.add(new AbstractJavaFlowVisitor.CallVariable(context.outputDataPoint, context.getProcessorCall().getReturnType(),
				fieldNameOf(context.processor)));

		flowExecutionDependencies.appendIndentedLine("// reverse dependencies.");
		for (var reverseDependency: context.getReverseDeps()) {
			final var conditionDependencies = ConditionalFlowCtrl.getExclusions(reverseDependency.getRules(), Processor.class).toList();
			if (!conditionDependencies.contains(context.processor)) {
				flowExecutionDependencies.appendMethodCall(fieldNameOf(reverseDependency), "addDependency", fieldName).eos();
			}
		}

	}

}
