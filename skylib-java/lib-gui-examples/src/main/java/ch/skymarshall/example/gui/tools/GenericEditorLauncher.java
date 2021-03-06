package ch.skymarshall.example.gui.tools;

import java.awt.Dialog;

import javax.swing.SwingUtilities;

import ch.skymarshall.annotations.Labeled;
import ch.skymarshall.annotations.Ordered;
import ch.skymarshall.annotations.Persistency;
import ch.skymarshall.gui.swing.tools.SwingGenericEditorDialog;
import ch.skymarshall.gui.tools.GenericEditorClassModel;
import ch.skymarshall.gui.tools.GenericEditorController;
import ch.skymarshall.gui.validation.GenericEditorValidationAdapter;
import ch.skymarshall.util.helpers.Log;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class GenericEditorLauncher {

	public static class EditedObject {
		private String str;

		private boolean bool;

		private Integer intValue;

		private int readOnly = 100;

		@Ordered(order = 3)
		@Labeled(label = "A string value")
		@NotBlank
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
		@Min(value = 1)
		public Integer getIntValue() {
			return intValue;
		}

		public void setIntValue(final Integer intValue) {
			this.intValue = intValue;
		}

		@Labeled(label = "Read only component")
		@Persistency(readOnly = true)
		public int getReadOnly() {
			return readOnly;
		}

		public void setReadOnly(final int readOnly) {
			this.readOnly = readOnly;
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
		final GenericEditorController<EditedObject> editor = new GenericEditorController<>(view,
				GenericEditorClassModel.builder(EditedObject.class) //
						.addAdapters(new GenericEditorValidationAdapter()) // optionally add validation
						.build());
		SwingUtilities.invokeLater(() -> {
			editor.activate();
			editor.load(obj);
			view.setVisible(true);
			Log.of(GenericEditorLauncher.class).info(obj.toString());
			view.dispose();
		});
	}

}
