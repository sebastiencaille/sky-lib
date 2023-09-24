package ch.scaille.example.gui.tools;

import static ch.scaille.gui.mvc.factories.Persisters.getSet;
import static ch.scaille.gui.mvc.properties.Configuration.persistent;

import java.awt.Dialog;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import ch.scaille.gui.mvc.IPropertiesGroup;
import ch.scaille.gui.mvc.persisters.ObjectProviderPersister.IObjectProvider;
import ch.scaille.gui.mvc.properties.AbstractTypedProperty;
import ch.scaille.gui.mvc.properties.BooleanProperty;
import ch.scaille.gui.mvc.properties.ObjectProperty;
import ch.scaille.gui.swing.tools.SwingGenericEditorDialog;
import ch.scaille.gui.tools.GenericEditorController;
import ch.scaille.gui.tools.PropertyEntry;
import ch.scaille.gui.tools.SimpleEditorModel;
import ch.scaille.util.helpers.Logs;

public class GenericPropertiesEditorLauncher {

	public static class EditedObject {
		private String str;

		private boolean bool;

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}

		public boolean isBool() {
			return bool;
		}

		public void setBool(boolean bool) {
			this.bool = bool;
		}

	}

	private static List<PropertyEntry> builder(IPropertiesGroup support, IObjectProvider<EditedObject> obj) {

		final var strProp = new ObjectProperty<String>("str", support)
				.configureTyped(persistent(obj, getSet(EditedObject::getStr, EditedObject::setStr)));
		final var boolProp = new BooleanProperty("bool", support)
				.configureTyped(persistent(obj, getSet(EditedObject::isBool, EditedObject::setBool)));

		return Arrays.asList(//
				SimpleEditorModel.entry(strProp, AbstractTypedProperty::createBindingChain, String.class, false,
						"A string property", null),
				SimpleEditorModel.entry(boolProp, AbstractTypedProperty::createBindingChain, Boolean.class, false,
						"A boolean property", null) //
		);
	}

	public static void main(final String[] args) {

		final var obj = new EditedObject();
		final var model = new SimpleEditorModel<>(GenericPropertiesEditorLauncher::builder);
		final var view = new SwingGenericEditorDialog(null, "Test", Dialog.ModalityType.DOCUMENT_MODAL);
		final var editor = new GenericEditorController<>(view.mainPanel(), model);
		SwingUtilities.invokeLater(() -> {
			editor.activate();
			editor.load(obj);
			view.build(model.getErrorProperty());
			view.validate();
			view.pack();
			view.setVisible(true);
			Logs.of(GenericPropertiesEditorLauncher.class).info(obj.toString());
			view.dispose();
		});
	}

}
