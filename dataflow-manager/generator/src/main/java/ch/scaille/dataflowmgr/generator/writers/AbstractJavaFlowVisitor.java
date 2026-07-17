package ch.scaille.dataflowmgr.generator.writers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import ch.scaille.dataflowmgr.model.Processor;
import ch.scaille.dataflowmgr.model.Call;
import ch.scaille.dataflowmgr.model.Flow;
import ch.scaille.generators.util.JavaCodeGenerator;
import ch.scaille.generators.util.Template;

import static ch.scaille.util.text.TextFormatter.snakeCaseToCamelCase;

public abstract class AbstractJavaFlowVisitor extends AbstractFlowVisitor {

	public static class CallVariable {
		final String name;
		final String dataType;
		public final String codeVariable;

		public CallVariable(final String name, final String dataType, final String codeVariable) {
			if (name.startsWith("get")) {
				this.name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
			} else {
				this.name = name;
			}
			this.dataType = dataType;
			this.codeVariable = codeVariable;
		}

		public CallVariable(final Call call, final String variable) {
			this(call.getName().substring(call.getName().lastIndexOf('.') + 1), call.getReturnType(), variable);
		}

		@Override
		public String toString() {
			return name + ": " + dataType;
		}

	}

	protected final Set<String> imports = new HashSet<>();

	protected final String packageName;

	protected final Template template;

	public final Set<String> definedDataPoints = new HashSet<>();

	// All variables declared until now
	final public List<CallVariable> availableVars = new ArrayList<>();

	protected AbstractJavaFlowVisitor(final Flow flow, final String packageName, final Template template) {
		super(flow);
		this.packageName = packageName;
		this.template = template;
	}

	public Stream<String> guessParameters(final CallContext context, final Call call) {
		return call.getParameters().entrySet().stream().map(kv -> guessParameter(context, kv.getKey(), kv.getValue()));
	}

	protected String guessParameter(final CallContext context, final String paramName, final String paramType) {
		if (paramType.equals(context.inputDataType)) {
			// Matches the processor's input datatype
			System.out.println(context.processor + ": " + paramName + ": match by type: " + availableVars.stream().filter(a -> a.name.equals(context.inputDataPoint)).toList());
			return availableVars.stream()
					.filter(a -> a.name.equals(context.inputDataPoint))
					.reduce((_, second) -> second)
					.map(v -> v.codeVariable)
					.orElseThrow(() -> new IllegalStateException(paramName + ": not found: " + context.inputDataPoint));
		}
		var matches = availableVars.stream().filter(a -> a.name.equals(paramName)).toList();
		if (matches.size() > 1) {
			throw new IllegalArgumentException(paramName + ": too many possible parameters found for " + paramName + ": " + matches);
		} else if (matches.size() == 1) {
			return matches.getFirst().codeVariable;
		}
		matches = availableVars.stream().filter(a -> a.dataType.equals(paramType)).toList();
		if (matches.size() > 1) {
			throw new IllegalArgumentException(paramName + ": too many possible parameters found for " + paramType + ": " + matches);
		} else if (matches.size() == 1) {
			return matches.getFirst().codeVariable;
		}
		throw new IllegalStateException("No parameter found for " + paramName + "/" + paramType);
	}

	public String varNameOf(final Processor processorCall, final Call call) {
		return snakeCaseToCamelCase(call.getCall().replace('.', '_') + toVariable(processorCall));
	}

	public static String toVariable(final Processor processorCall) {
		return snakeCaseToCamelCase((processorCall.getCall().getCall() + '_' + processorCall.toDataPoint()).replace('-', '_').replace('.', '_'));
	}

	/**
	 * Specifies if a call a data point is available (executed by any possible
	 * call)
	 */
	public String availableVarNameOf(final String dataPoint) {
		return dataPoint + "_available";
	}

	protected JavaCodeGenerator<RuntimeException> appendInfo(final JavaCodeGenerator<RuntimeException> generator,
			final Processor processorCall) {
		return generator.append("// ------------------------- ").append(processorCall.toString())
				.append(" -------------------------");
	}

}
