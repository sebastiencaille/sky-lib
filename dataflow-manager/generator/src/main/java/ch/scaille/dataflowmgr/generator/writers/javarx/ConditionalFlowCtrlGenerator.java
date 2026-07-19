package ch.scaille.dataflowmgr.generator.writers.javarx;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.CallContext;
import ch.scaille.dataflowmgr.generator.writers.AbstractJavaFlowVisitor;
import ch.scaille.dataflowmgr.model.Call;
import ch.scaille.dataflowmgr.model.Processor;
import ch.scaille.dataflowmgr.model.flowctrl.ConditionalFlowCtrl;
import ch.scaille.generators.util.JavaCodeGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.scaille.dataflowmgr.generator.writers.javarx.FlowToRXJavaVisitor.fieldNameOf;

public class ConditionalFlowCtrlGenerator extends AbstractFlowGenerator {

	public static final String CONTROL_TYPE = "CONTROL";
	private final Set<String> alreadyDefinedDataPoints = new HashSet<>();

	protected ConditionalFlowCtrlGenerator(FlowToRXJavaVisitor visitor,
										   JavaCodeGenerator<RuntimeException> flowExecutionAttributes,
										   JavaCodeGenerator<RuntimeException> flowExecutionBuilder,
										   JavaCodeGenerator<RuntimeException> flowExecutionDependencies) {
		super(visitor, flowExecutionAttributes, flowExecutionBuilder, flowExecutionDependencies);
	}

	@Override
	public boolean matches(CallContext context) {
		return ConditionalFlowCtrl.getCondition(context.processor.getRules()).isPresent();
	}

	@Override
	public void generate(BaseGenContext<GenContext> genContext, CallContext context) {
		final var processor = context.processor;

		// Call activators
		flowExecutionDependencies.appendIndentedLine("// activation dependencies");
		for (var activator: ConditionalFlowCtrl.getActivators(processor.getRules()).toList()) {
			final var fieldName = fieldNameOf(context, activator);
			addCallBuilder(context, fieldName, activator.getCall(), CONTROL_TYPE, "always()", activator, "false");
			flowExecutionDependencies.appendMethodCall(fieldNameOf(context.processor), "addDependency", List.of(fieldName)).eos();
			flowExecutionDependencies.appendMethodCall(fieldName, "addOnSuccess","_ -> triggerProcess(%s).subscribe()".formatted(fieldNameOf(context.processor))).eos();
		}
		final var activatorsSymbols = ConditionalFlowCtrl.getActivators(processor.getRules()).map(activator -> fieldNameOf(context, activator)).toList();
		if (!activatorsSymbols.isEmpty()) {
			flowExecutionDependencies.appendMethodCall(fieldNameOf(context.processor), "addActivations",
					activatorsSymbols.stream().map("%s::getOutput"::formatted).toList()).eos();
		}

		// Call exclusions
		flowExecutionDependencies.appendIndentedLine("// exclusion dependencies");
		final var callExclusions = ConditionalFlowCtrl.getExclusions(processor.getRules(), Call.class).toList();
		for (var exclusion: callExclusions) {
			final var fieldName = fieldNameOf(context, exclusion);
			addCallBuilder(context, fieldName,  exclusion.getCall(), CONTROL_TYPE,"always()", exclusion, "false");
			flowExecutionDependencies.appendMethodCall(fieldNameOf(context.processor), "addDependency", List.of(fieldName)).eos();
			flowExecutionDependencies.appendMethodCall(fieldName, "addOnSuccess","_ -> triggerProcess(%s).subscribe()".formatted(fieldNameOf(context.processor))).eos();
		}
		final var callExclusionSymbols = ConditionalFlowCtrl.getExclusions(processor.getRules(), Call.class).map(exclusion -> fieldNameOf(context, exclusion)).toList();
		if (!callExclusionSymbols.isEmpty()) {
			flowExecutionDependencies.appendMethodCall(fieldNameOf(context.processor), "addExclusions",
					callExclusionSymbols.stream().map(" %s::getOutput"::formatted).toList()).eos();
		}

		// Processor exclusions
		final var processorExclusions = ConditionalFlowCtrl.getExclusions(processor.getRules(), Processor.class).toList();
		for (var activator:  processorExclusions.stream().flatMap(activated -> ConditionalFlowCtrl.getActivators(activated.getRules())).toList()) {
			final var fieldName = fieldNameOf(context, activator);
			flowExecutionDependencies.appendMethodCall(fieldNameOf(context.processor), "addDependency", fieldName).eos();
			flowExecutionDependencies.appendMethodCall(fieldName, "addOnSuccess","_ -> triggerProcess(%s).subscribe()".formatted(fieldNameOf(context.processor))).eos();
		}
		for (var exclusion: processorExclusions.stream().flatMap(excluded -> ConditionalFlowCtrl.getExclusions(excluded.getRules(), Call.class)).toList()) {
			final var fieldName = fieldNameOf(context, exclusion);
			flowExecutionDependencies.appendMethodCall(fieldNameOf(context.processor), "addDependency", fieldName).eos();
			flowExecutionDependencies.appendMethodCall(fieldName, "addOnSuccess","_ -> triggerProcess(%s).subscribe()".formatted(fieldNameOf(context.processor))).eos();
		}

		final var processorExclusionSymbols = ConditionalFlowCtrl.getExclusions(processor.getRules(), Processor.class).map(FlowToRXJavaVisitor::fieldNameOf).toList();
		if (!processorExclusionSymbols.isEmpty()) {
			flowExecutionDependencies.appendMethodCall(fieldNameOf(context.processor), "addExclusions",
					processorExclusionSymbols.stream().map("%s::evaluateCondition"::formatted).toList()).eos();
		}

		// result's DataPoint
		if (!alreadyDefinedDataPoints.contains(context.outputDataPoint)) {
			flowExecutionAttributes.addInstanceVarDecl("private final", "DataPoint<%s>".formatted(context.getProcessorCall().getReturnType()), context.outputDataPoint, "new DataPoint<>()");
			alreadyDefinedDataPoints.add(context.outputDataPoint);
		}
		visitor.availableVars.add(new AbstractJavaFlowVisitor.CallVariable(context.outputDataPoint, context.getProcessorCall().getReturnType(),
				context.outputDataPoint));
		flowExecutionDependencies.appendMethodCall(fieldNameOf(context.processor), "addOnSuccess", List.of("%s::setOutput".formatted(context.outputDataPoint))).eos();
	}

}
