package ch.scaille.tcwriter.persistence.testexec;

import ch.scaille.annotations.Labeled;
import ch.scaille.annotations.Ordered;

public class JunitTestExecConfig {
	private String java = System.getProperty("java.home") + "/bin/java";
	private String classpath = null;

	@Ordered(order = 1)
	@Labeled(label = "Path to java (empty for default)")
	public String getJava() {
		return java;
	}

	public void setJava(String java) {
		this.java = java;
	}

	@Ordered(order = 2)
	@Labeled(label = "ClassPath of dictionary implementation")
	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
}
