package ch.scaille.gui.mvc;

public interface IObjectGuiModel<T> {

	void setCurrentObject(T object);

	void load();

	void save();

}
