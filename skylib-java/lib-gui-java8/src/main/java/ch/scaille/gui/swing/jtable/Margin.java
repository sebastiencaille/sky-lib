package ch.scaille.gui.swing.jtable;

public interface Margin {

	int compute(int columnWidth);

	public static Margin px(int i) {
		return cw -> i;
	}

	public static Margin percent(float pct) {
		return cw -> (int) (pct * cw);
	}

}