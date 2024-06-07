/*
 * gtk_pilot.cpp
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */

#include <utils.hh>
#include "gtk_entry_pilot.hh"
#include "gtk_gui_pilot.hh"

namespace ch_skymarshall::gui::gtk4::pilot {

gtk_gui_pilot::gtk_gui_pilot(Gtk::Window *_window) :
		m_window(_window) {

}

shared_ptr<gtk_entry_pilot> gtk_gui_pilot::entry(const string &_name) {
	return make_shared<gtk_entry_pilot>(this, _name);
}

Gtk::Widget* gtk_gui_pilot::find_widget(const string &_name) {
	Gtk::Widget *found = find(m_window->get_child(), _name);
	if (found == NULL) {
		throw gui_pilot_exception("No widget found: " + _name);
	}
	return found;
}

Gtk::Widget* gtk_gui_pilot::find(Gtk::Widget *_widget, const ustring &_name) {
	if (_name == _widget->get_name()) {
		return _widget;
	}
	if (_widget->get_first_child() == NULL) {
		return NULL;
	}
	Gtk::Widget *found = NULL;
	for (Gtk::Widget *child = _widget->get_first_child(); found == NULL && child != NULL; child =
			child->get_next_sibling()) {
		found = find(child, _name);
	}
	return found;
}

gtk_gui_pilot::~gtk_gui_pilot() = default;
}
