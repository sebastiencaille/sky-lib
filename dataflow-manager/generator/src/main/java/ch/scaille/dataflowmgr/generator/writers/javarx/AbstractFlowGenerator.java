package ch.scaille.dataflowmgr.generator.writers.javarx;

import java.util.List;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor;
import ch.scaille.dataflowmgr.generator.writers.IFlowGenerator;
import ch.scaille.dataflowmgr.model.Call;
import ch.scaille.dataflowmgr.model.Processor;
import ch.scaille.generators.util.JavaCodeGenerator;

public abstract class AbstractFlowGenerator implements IFlowGenerator<AbstractFlowGenerator.GenContext> {

	public static class GenContext {

		public final boolean debug;
		public final List<Processor> processorCallDeps;

		public GenContext(boolean debug, List<Processor> processorCallDeps) {
			this.debug = debug;
			this.processorCallDeps = processorCallDeps;
		}

	}

	protected final FlowToRXJavaVisitor visitor;

	protected final JavaCodeGenerator<RuntimeException> flowExecutionAttributes;

	protected final JavaCodeGenerator<RuntimeException> flowExecutionBuilder;

	protected final JavaCodeGenerator<RuntimeException> flowExecutionDependencies;

	protected AbstractFlowGenerator(FlowToRXJavaVisitor visitor,
									JavaCodeGenerator<RuntimeException> flowExecutionAttributes,
									JavaCodeGenerator<RuntimeException> flowExecutionBuilder,
									JavaCodeGenerator<RuntimeException> flowExecutionDependencies) {
		this.visitor = visitor;
		this.flowExecutionAttributes = flowExecutionAttributes;
		this.flowExecutionBuilder = flowExecutionBuilder;
		this.flowExecutionDependencies = flowExecutionDependencies;
	}

	protected void addCallBuilder(AbstractFlowVisitor.CallContext context, String variableName, String description,
								  String callType, String conditionFactory, Call call, String defaultValue) {
		variableName = variableName.replace('.', '_');
		final var returnType = switch(call.getReturnType()) {
			case "boolean" -> "Boolean";
			case "void" -> "Void";
			default -> call.getReturnType();
		};
		flowExecutionAttributes.addInstanceVarDecl("private final", "CallExecution<%s>".formatted(returnType), variableName);

		flowExecutionBuilder.append(variableName).append(" = new ")
				.appendMethodCall(null, "CallExecution<>",
					'"' + description + '"', "Map.of(\"TYPE\", \"" + callType + "\")", conditionFactory,
					buildMethodCall(context, call, returnType)).eos();

		if (defaultValue != null) {
			flowExecutionBuilder.appendMethodCall(variableName, "setOutput", defaultValue).eos();
		}
		flowExecutionBuilder.eol();
	}

	private String buildMethodCall(AbstractFlowVisitor.CallContext context, Call call, String returnType) {
		final var hasCall = !"exit".equals(call.getCall());
		final var isVoid = "Void".equals(returnType);
		final var callExecution = JavaCodeGenerator.inMemory().append("() -> ");
		if (hasCall && isVoid) {
			callExecution.append("{ ");
		}
		if (hasCall) {
			callExecution.appendMethodCall(null, call.getCall(),
					visitor.guessParameters(context, call).map(p -> "inputDataPoint".equals(p) ? p : p + ".getOutput()").toList());
		}
		if (hasCall && isVoid) {
			callExecution.append("; return ");
		}
		if (isVoid) {
			callExecution.append("null");
		}
		if (hasCall && isVoid) {
			callExecution.append("; }");
		}
		return callExecution.toString();
	}

}
