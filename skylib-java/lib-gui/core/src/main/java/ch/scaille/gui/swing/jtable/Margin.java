package ch.scaille.gui.swing.jtable;

public interface Margin {

	int compute(int columnWidth);

	static Margin px(int i) {
		return _ -> i;
	}

	static Margin percent(float pct) {
		return cw -> (int) (pct * cw);
	}

}