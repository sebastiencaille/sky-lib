package ch.skymarshall.dataflowmgr.generator.writers;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.Call;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;

public abstract class AbstractJavaFlowVisitor extends AbstractFlowVisitor {

	protected static class BindingImplVariable {
		final String name;
		final String dataType;
		public final String codeVariable;

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

	public final Set<String> definedDataPoints = new HashSet<>();

	// All variables declared until now 
	protected final List<BindingImplVariable> availableVars = new ArrayList<>();

	protected AbstractJavaFlowVisitor(final Flow flow, final String packageName, final Template template) {
		super(flow);
		this.packageName = packageName;
		this.template = template;
	}

	public List<String> guessParameters(final BindingContext context, final Call<?> call) {
		return call.getParameters().entrySet().stream().map(kv -> guessParameter(context, kv.getKey(), kv.getValue()))
				.collect(toList());
	}

	protected String guessParameter(final BindingContext context, final String paramName, final String paramType) {
		if (paramType.equals(context.inputDataType)) {
			return availableVars.stream().filter(a -> a.name.equals(context.inputDataPoint)).findFirst()
					.map(v -> v.codeVariable)
					.orElseThrow(() -> new IllegalStateException("Not found: " + context.inputDataPoint));
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

	public String toVariable(final Binding binding) {
		return (binding.getProcessor().getCall() + '_' + binding.toDataPoint()).replace('-', '_').replace('.', '_');
	}

	public String toVariable(final Call<?> call) {
		return call.getName().replace('.', '_');
	}

	/**
	 * Specifies if a binding a data point is available (executed by any possible
	 * binding)
	 *
	 * @param dataPoint
	 * @return
	 */
	public String availableVarNameOf(final String dataPoint) {
		return dataPoint + "_available";
	}

	protected JavaCodeGenerator<RuntimeException> appendInfo(final JavaCodeGenerator<RuntimeException> generator,
			final Binding binding) {
		return generator.append("// ------------------------- ").append(binding.toString())
				.append(" -------------------------");
	}

}
