package ch.skymarshall.example.gui.generic;

import java.awt.Component;
import java.awt.Dialog;

import javax.swing.SwingUtilities;

import ch.skymarshall.gui.swing.tools.SwingGenericEditorDialog;
import ch.skymarshall.gui.tools.ClassAdapter;
import ch.skymarshall.gui.tools.GenericEditorAdapter;
import ch.skymarshall.util.annotations.Labeled;
import ch.skymarshall.util.annotations.Ordered;

public class GenericEditorLauncher {

	public static class EditedObject {
		String str;

		boolean bool;

		@Ordered(order = 2)
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

		@Override
		public String toString() {
			return str + " / " + bool;
		}
	}

	public static void main(final String[] args) {

		final EditedObject obj = new EditedObject();
		obj.setBool(true);
		obj.setStr("Hello");

		final SwingGenericEditorDialog dialog = new SwingGenericEditorDialog(null, "Test",
				Dialog.ModalityType.DOCUMENT_MODAL);
		final GenericEditorAdapter<EditedObject, Component> editor = new GenericEditorAdapter<>(dialog,
				new ClassAdapter<>(EditedObject.class));
		editor.apply();
		SwingUtilities.invokeLater(() -> {
			editor.load(obj);
			dialog.setVisible(true);
			System.out.println(obj);
		});
	}

}
