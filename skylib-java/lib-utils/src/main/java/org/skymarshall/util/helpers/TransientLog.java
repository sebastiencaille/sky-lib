/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.util.helpers;

import java.util.ArrayList;
import java.util.List;

public class TransientLog {

	private final List<String> logs = new ArrayList<String>();

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
		final StringBuilder builder = new StringBuilder();
		for (int i = logs.size() - 1; i >= 0; i--) {
			builder.append(logs.get(i));
		}
		builder.append(currentBucket.toString());
		return builder.toString();
	}

	public void clear() {
		logs.clear();
	}
}
