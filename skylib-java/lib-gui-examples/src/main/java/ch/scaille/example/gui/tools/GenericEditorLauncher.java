package ch.scaille.example.gui.tools;

import java.awt.Dialog;

import javax.swing.SwingUtilities;

import ch.scaille.annotations.Labeled;
import ch.scaille.annotations.Ordered;
import ch.scaille.annotations.Persistency;
import ch.scaille.gui.swing.tools.SwingGenericEditorDialog;
import ch.scaille.gui.tools.GenericEditorClassModel;
import ch.scaille.gui.tools.GenericEditorController;
import ch.scaille.gui.validation.GenericEditorValidationAdapter;
import ch.scaille.util.helpers.Logs;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import lombok.ToString;

public class GenericEditorLauncher {

	@Setter
	@ToString
	public static class EditedObject {
		private String str;

		private boolean bool;

		private Integer intValue;

		private int readOnly = 100;

		@Ordered(order = 3)
		@Labeled(label = "A string value (not empty)")
		@NotBlank
		public String getStr() {
			return str;
		}

		@Ordered(order = 1)
		@Labeled(label = "A boolean value")
		public boolean isBool() {
			return bool;
		}

		@Ordered(order = 2)
		@Labeled(label = "An Integer value")
		@Min(value = 1)
		public Integer getIntValue() {
			return intValue;
		}

		@Labeled(label = "Read only component")
		@Persistency(readOnly = true)
		public int getReadOnly() {
			return readOnly;
		}

	}

	public static void main(final String[] args) {

		final var obj = new EditedObject();
		obj.setBool(true);
		obj.setStr("Hello");
		obj.setIntValue(1);

		final var view = new SwingGenericEditorDialog(null, "Test", Dialog.ModalityType.DOCUMENT_MODAL);
		final var model = GenericEditorClassModel.builder(EditedObject.class) //
				.adapters(new GenericEditorValidationAdapter<>()) // optionally add validation
				.build();
		final var editor = new GenericEditorController<>(model);
		SwingUtilities.invokeLater(() -> {
			editor.build(view::createMainPanel);
			editor.load(obj);
			view.build(model.getErrorProperty());
			view.setVisible(true);
			Logs.of(GenericEditorLauncher.class).info(obj.toString());
			view.dispose();
		});
	}

}
