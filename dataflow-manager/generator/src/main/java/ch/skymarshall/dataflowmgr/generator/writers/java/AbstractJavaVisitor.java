package ch.skymarshall.dataflowmgr.generator.writers.java;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.skymarshall.dataflowmgr.generator.AbstractFlowVisitor;
import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.BindingRule;
import ch.skymarshall.dataflowmgr.model.Call;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.WithId;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;

public abstract class AbstractJavaVisitor extends AbstractFlowVisitor {

	protected static class BindingImplVariable {
		final String name;
		final String dataType;
		final String codeVariable;

		public BindingImplVariable(final String name, final String dataType, final String codeVariable) {
			if (name.startsWith("get")) {
				this.name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
			} else {
				this.name = name;
			}
			this.dataType = dataType;
			this.codeVariable = codeVariable;
		}

		public BindingImplVariable(final Call<?> call, final String variable) {
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

	protected final Set<String> definedDataPoints = new HashSet<>();

	protected final List<BindingImplVariable> availableVars = new ArrayList<>();

	protected AbstractJavaVisitor(final Flow flow, final String packageName, final Template template) {
		super(flow);
		this.packageName = packageName;
		this.template = template;
	}

	protected List<String> guessParameters(final BindingContext context, final Call<?> call) {
		return call.getParameters().entrySet().stream().map(kv -> guessParameter(context, kv.getKey(), kv.getValue()))
				.collect(toList());
	}

	protected String guessParameter(final BindingContext context, final String paramName, final String paramType) {
		if (paramType.equals(context.inputDataType)) {
			return availableVars.stream().filter(a -> a.name.equals(context.inputDataPoint)).findFirst()
					.map(v -> v.codeVariable).get();
		}
		List<BindingImplVariable> matches = availableVars.stream().filter(a -> a.name.equals(paramName))
				.collect(toList());
		if (matches.size() > 1) {
			throw new IllegalArgumentException("Too many possible parameters found for " + paramName + ": " + matches);
		} else if (matches.size() == 1) {
			return matches.get(0).codeVariable;
		}
		matches = availableVars.stream().filter(a -> a.dataType.equals(paramType)).collect(toList());
		if (matches.size() > 1) {
			throw new IllegalArgumentException("Too many possible parameters found for " + paramType + ": " + matches);
		} else if (matches.size() == 1) {
			return matches.get(0).codeVariable;
		}
		throw new IllegalStateException("No parameter found for " + paramName + "/" + paramType);
	}

	protected String varNameOf(final Binding binding, final Call<?> call) {
		return call.getCall().replace('.', '_') + toVariable(binding);
	}

	protected String toVariable(final ExternalAdapter adapter) {
		return JavaCodeGenerator.simpleNameOf(adapter.getName());
	}

	protected String toVariable(final Binding binding) {
		String activatorsSuffix = BindingRule.getActivators(binding.getRules())
				.map(c -> JavaCodeGenerator.simpleNameOf(c.getName())).collect(joining("_"));
		String bindingName = binding.toDataPoint().replace('-', '_');
		if (!activatorsSuffix.isEmpty()) {
			bindingName = bindingName + '_' + activatorsSuffix;
		}
		return bindingName;
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
