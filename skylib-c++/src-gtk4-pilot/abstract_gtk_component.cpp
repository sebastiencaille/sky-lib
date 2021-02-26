/*
 * AbstractGtkComponent.cpp
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */

#include "abstract_gtk_component.hh"

namespace ch_skymarshall::gui::gtk4::pilot {

abstract_gtk_component::abstract_gtk_component() {
}

abstract_gtk_component::~abstract_gtk_component() {
}

bool abstract_gtk_component::isEditable(Gtk::Widget *widget) {
	return widget->is_visible();
}

}
