/*
 * AbstractGtkComponent.cpp
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */

#include <iostream>

#include "abstract_gtk_component.hh"
#include "gtk_utils.hh"

namespace ch_skymarshall::gui::gtk4::pilot {

using namespace ch_skymarshall::gui::pilot;
using namespace ch_skymarshall::gui::gtk4::utils;

abstract_gtk_component::abstract_gtk_component(
		gtk_gui_pilot* _gui_pilot, string _name) :
		m_gui_pilot(_gui_pilot), m_name(_name) {
}

abstract_gtk_component::~abstract_gtk_component() {
}

bool abstract_gtk_component::isEditable(Gtk::Widget *widget) {
	return widget->is_visible();
}

bool abstract_gtk_component::executePolling(polling &_polling) {
	return get_run_in_gtk<bool>([&_polling, this]() {
		return this->abstract_gui_component::executePolling(_polling);
	});
}
}
