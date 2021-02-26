/*
 * AbstractGtkComponent.h
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */

#ifndef ABSTRACTGTKCOMPONENT_H_
#define ABSTRACTGTKCOMPONENT_H_

#include <gtkmm.h>

#include "abstract_gui_component.hh"

namespace ch_skymarshall::gui::gtk4::pilot {

class abstract_gtk_component: public ch_skymarshall::gui::pilot::abstract_gui_component {

protected:
	bool isEditable(Gtk::Widget *widget);

public:
	abstract_gtk_component();
	virtual ~abstract_gtk_component();

};
}

#endif /* ABSTRACTGTKCOMPONENT_H_ */
