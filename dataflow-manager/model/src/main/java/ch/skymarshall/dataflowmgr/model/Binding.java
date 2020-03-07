package ch.skymarshall.dataflowmgr.model;

public class Binding {

	private final Builder config;

	public static class Builder {

		private final String fromProcessor;
		private final String toProcessor;

		public Builder(final String fromProcessor, final String toProcessor) {
			this.fromProcessor = fromProcessor;
			this.toProcessor = toProcessor;
		}

		public Binding build() {
			return new Binding(this);
		}

	}

	public static Builder builder(final String entryProcessor, final String string) {
		return new Builder(entryProcessor, string);
	}

	public Binding(final Builder config) {
		this.config = config;
	}

	public String fromProcessor() {
		return config.fromProcessor;
	}

	public String toProcessor() {
		return config.toProcessor;
	}

	@Override
	public int hashCode() {
		return config.fromProcessor.hashCode() + config.toProcessor.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Binding)) {
			return false;
		}
		final Binding other = (Binding) obj;
		return other.config.fromProcessor.equals(config.fromProcessor)
				&& other.config.toProcessor.equals(config.toProcessor);
	}

}
