package ch.skymarshall.example.gui.tools;

import static ch.skymarshall.gui.mvc.factories.Persisters.getSet;
import static ch.skymarshall.gui.mvc.properties.Configuration.persistent;

import java.awt.Dialog;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.persisters.ObjectProviderPersister.IObjectProvider;
import ch.skymarshall.gui.mvc.properties.AbstractTypedProperty;
import ch.skymarshall.gui.mvc.properties.BooleanProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.tools.SwingGenericEditorDialog;
import ch.skymarshall.gui.tools.GenericEditorController;
import ch.skymarshall.gui.tools.PropertyEntry;
import ch.skymarshall.gui.tools.SimpleEditorModel;
import ch.skymarshall.util.helpers.Log;

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

	private static List<PropertyEntry> builder(IScopedSupport support, IObjectProvider<EditedObject> obj) {

		final ObjectProperty<String> strProp = new ObjectProperty<String>("str", support)
				.configureTyped(persistent(obj, getSet(EditedObject::getStr, EditedObject::setStr)));
		final BooleanProperty boolProp = new BooleanProperty("bool", support)
				.configureTyped(persistent(obj, getSet(EditedObject::isBool, EditedObject::setBool)));

		return Arrays.asList(//
				SimpleEditorModel.entry(strProp, AbstractTypedProperty::createBindingChain, String.class, false,
						"A string property", null),
				SimpleEditorModel.entry(boolProp, AbstractTypedProperty::createBindingChain, Boolean.class, false,
						"A boolean property", null) //
		);
	}

	public static void main(final String[] args) {

		final EditedObject obj = new EditedObject();

		final SwingGenericEditorDialog view = new SwingGenericEditorDialog(null, "Test",
				Dialog.ModalityType.DOCUMENT_MODAL);
		final GenericEditorController<EditedObject> editor = new GenericEditorController<>(view,
				new SimpleEditorModel<>(GenericPropertiesEditorLauncher::builder));
		SwingUtilities.invokeLater(() -> {
			editor.activate();
			editor.load(obj);
			view.setVisible(true);
			Log.of(GenericPropertiesEditorLauncher.class).info(obj.toString());
			view.dispose();
		});
	}

}
