package ch.scaille.example.gui.tools;

import static ch.scaille.javabeans.persisters.Persisters.persister;
import static ch.scaille.javabeans.properties.Configuration.persistent;

import java.awt.Dialog;
import java.util.List;

import javax.swing.SwingUtilities;

import ch.scaille.gui.swing.tools.SwingGenericEditorDialog;
import ch.scaille.gui.tools.GenericEditorController;
import ch.scaille.gui.tools.IPropertyEntry;
import ch.scaille.gui.tools.SimpleEditorModel;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.persisters.IPersisterFactory.IObjectProvider;
import ch.scaille.javabeans.properties.AbstractTypedProperty;
import ch.scaille.javabeans.properties.BooleanProperty;
import ch.scaille.javabeans.properties.ObjectProperty;
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

	private static List<IPropertyEntry> builder(IPropertiesGroup support, IObjectProvider<EditedObject> obj) {

		final var strProp = new ObjectProperty<String>("str", support)
				.configureTyped(persistent(obj, persister(EditedObject::getStr, EditedObject::setStr)));
		final var boolProp = new BooleanProperty("bool", support)
				.configureTyped(persistent(obj, persister(EditedObject::isBool, EditedObject::setBool)));

		return List.of(//
				SimpleEditorModel.entry(String.class, strProp, AbstractTypedProperty::createBindingChain, false,
						"A string property", null),
				SimpleEditorModel.entry(Boolean.class, boolProp, AbstractTypedProperty::createBindingChain, false,
						"A boolean property", null) //
		);
	}

	public static void main(final String[] args) {

		final var obj = new EditedObject();
		final var model = new SimpleEditorModel<>(GenericPropertiesEditorLauncher::builder);
		final var view = new SwingGenericEditorDialog(null, "Test", Dialog.ModalityType.DOCUMENT_MODAL);
		final var editor = new GenericEditorController<>(model);
		SwingUtilities.invokeLater(() -> {
			editor.build(view::createMainPanel);
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
