
/*
 * GtkBindings.h
 *
 *  Created on: Apr 4, 2012
 *      Author: scaille
 */

#ifndef GTKBINDINGS_H_
#define GTKBINDINGS_H_

#include <glibmm.h>
#include <gtkmm.h>

#include <binding_interface.hh>

namespace ch_skymarshall::gui::gtk4 {

class entry_binding: public component_binding<Glib::ustring> {
private:
	Gtk::Entry &m_entry;
	weak_ptr<component_link<Glib::ustring>> m_componentLink;
	sigc::connection m_connection;
	void on_changed_signal();
public:
	explicit entry_binding(Gtk::Entry &entry);
	~entry_binding() override;
	void add_component_value_change_listener(
			weak_ptr<component_link<Glib::ustring>> _componentLink) override;
	void remove_component_value_change_listener() override;
	void set_component_value(property &_source, Glib::ustring _value) override;
	source_ptr get_component() override;

	static shared_ptr<component_binding<Glib::ustring>> of(Gtk::Entry &entry) {
		return make_shared<entry_binding>(entry);
	}
};

class label_binding: public component_binding<Glib::ustring> {
private:
	Gtk::Label &m_label;
	void on_changed_signal();
public:
	explicit label_binding(Gtk::Label &entry);
	~label_binding() override;
	void add_component_value_change_listener(
			weak_ptr<component_link<Glib::ustring>> _componentLink) override;
	void remove_component_value_change_listener() override;
	void set_component_value(property &_source, Glib::ustring _value) override;
	source_ptr get_component();

	static shared_ptr<component_binding<Glib::ustring>> of(Gtk::Label &entry) {
		return make_shared<label_binding>(entry);
	}
};

}

#endif /* GTKBINDINGS_H_ */
