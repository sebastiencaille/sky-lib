package ch.skymarshall.example.gui.tools;

import java.awt.Component;
import java.awt.Dialog;

import javax.swing.SwingUtilities;

import ch.skymarshall.gui.swing.tools.SwingGenericEditorDialog;
import ch.skymarshall.gui.tools.GenericEditorClassModel;
import ch.skymarshall.gui.tools.GenericEditorAdapter;
import ch.skymarshall.util.annotations.Labeled;
import ch.skymarshall.util.annotations.Ordered;

public class GenericEditorLauncher {

	public static class EditedObject {
		private String str;

		private boolean bool;

		private Integer intValue;

		@Ordered(order = 3)
		@Labeled(label = "A string value")
		public String getStr() {
			return str;
		}

		public void setStr(final String str) {
			this.str = str;
		}

		@Ordered(order = 1)
		@Labeled(label = "A boolean value")
		public boolean isBool() {
			return bool;
		}

		public void setBool(final boolean bool) {
			this.bool = bool;
		}

		@Ordered(order = 2)
		@Labeled(label = "An Integer value")
		public Integer getIntValue() {
			return intValue;
		}

		public void setIntValue(final Integer intValue) {
			this.intValue = intValue;
		}

		@Override
		public String toString() {
			return str + " / " + bool + " / " + intValue;
		}
	}

	public static void main(final String[] args) {

		final EditedObject obj = new EditedObject();
		obj.setBool(true);
		obj.setStr("Hello");
		obj.setIntValue(1);

		final SwingGenericEditorDialog view = new SwingGenericEditorDialog(null, "Test",
				Dialog.ModalityType.DOCUMENT_MODAL);
		final GenericEditorAdapter<EditedObject, Component> editor = new GenericEditorAdapter<>(view,
				new GenericEditorClassModel<>(EditedObject.class));
		editor.apply();
		SwingUtilities.invokeLater(() -> {
			editor.load(obj);
			view.setVisible(true);
			System.out.println(obj);
		});
	}

}
