package ch.skymarshall.dataflowmgr.generator.writers.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.skymarshall.dataflowmgr.generator.AbstractFlowVisitor;
import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.Call;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.WithId;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;

public abstract class AbstractJavaVisitor extends AbstractFlowVisitor {

	protected static class BindingImplVariable {
		final String parameterName;
		final String parameterType;
		final String variable;

		public BindingImplVariable(final String parameterName, final String parameterType, final String variable) {
			this.parameterName = parameterName;
			this.parameterType = parameterType;
			this.variable = variable;
		}

		public BindingImplVariable(final Call<?> call, final String variable) {
			this.parameterName = call.getName().substring(call.getName().lastIndexOf('.') + 1);
			this.parameterType = call.getReturnType();
			this.variable = variable;
		}

		@Override
		public String toString() {
			return parameterName + ": " + parameterType;
		}

	}

	protected final Set<String> imports = new HashSet<>();

	protected final String packageName;

	protected final Template template;

	protected final Set<String> definedDataPoints = new HashSet<>();

	protected final List<BindingImplVariable> availableVars = new ArrayList<>();

	protected AbstractJavaVisitor(final Flow flow, final String packageName, final Template template) {
		super(flow);
		this.packageName = packageName;
		this.template = template;
	}

	protected List<String> guessParameters(final BindingContext context, final Call<?> call) {
		return call.getParameters().entrySet().stream().map(kv -> guessParameter(context, kv.getKey(), kv.getValue()))
				.collect(Collectors.toList());
	}

	protected String guessParameter(final BindingContext context, final String paramName, final String paramType) {
		if (paramType.equals(context.inputDataType)) {
			return availableVars.stream().filter(a -> a.parameterName.equals(context.inputDataPoint)).findFirst()
					.map(v -> v.variable).get();
		}
		List<BindingImplVariable> matches = availableVars.stream().filter(a -> a.parameterName.equals(paramName))
				.collect(Collectors.toList());
		if (matches.size() > 1) {
			throw new IllegalArgumentException("Too many possible parameters found for " + paramName + ": " + matches);
		} else if (matches.size() == 1) {
			return matches.get(0).variable;
		}
		matches = availableVars.stream().filter(a -> a.parameterType.equals(paramType)).collect(Collectors.toList());
		if (matches.size() > 1) {
			throw new IllegalArgumentException("Too many possible parameters found for " + paramType + ": " + matches);
		} else if (matches.size() == 1) {
			return matches.get(0).variable;
		}
		throw new IllegalStateException("No parameter found for " + paramName + "/" + paramType);
	}

	protected String varNameOf(final Binding binding, final Call<?> call) {
		return call.getCall().replace('.', '_') + toVariable(binding);
	}

	protected String toVariable(final WithId withId) {
		return withId.uuid().toString().replace('-', '_');
	}

	/**
	 * Specifies if a binding a data point is available (executed by any possible
	 * binding)
	 *
	 * @param dataPoint
	 * @return
	 */
	protected String availableVarNameOf(final String dataPoint) {
		return dataPoint + "_available";
	}

	protected JavaCodeGenerator<RuntimeException> appendInfo(final JavaCodeGenerator<RuntimeException> generator,
			final Binding binding) {
		return generator.append("// ------------------------- ").append(binding.toString())
				.append(" -------------------------");
	}

}
