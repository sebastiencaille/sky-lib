package ch.scaille.util.helpers;

import java.util.ArrayList;
import java.util.List;

public class TransientLog {

	private final List<String> logs = new ArrayList<>();

	private final int maxLineNumber;
	private final int bucketSize;

	private int lineCount;
	private int bucketCount;
	private StringBuilder currentBucket = new StringBuilder();

	public TransientLog(final int maxLineNumber, final int removeWhenFull) {
		if (removeWhenFull >= maxLineNumber) {
			throw new IllegalArgumentException("maxLineNumber must be >= removeWhenFull");
		}
		this.maxLineNumber = maxLineNumber;
		bucketSize = removeWhenFull;
	}

	public synchronized void addLine(final String line) {

		currentBucket.append(line);
		currentBucket.append('\n');
		lineCount++;
		bucketCount++;
		if (bucketCount == bucketSize) {
			bucketCount = 0;
			logs.add(0, currentBucket.toString());
			currentBucket = new StringBuilder();
		}
		if (lineCount > maxLineNumber) {
			lineCount -= bucketSize;
			logs.remove(logs.size() - 1);
		}

	}

	@Override
	public synchronized String toString() {
		final var builder = new StringBuilder();
		for (var i = logs.size() - 1; i >= 0; i--) {
			builder.append(logs.get(i));
		}
		builder.append(currentBucket.toString());
		return builder.toString();
	}

	public void clear() {
		logs.clear();
	}
}
