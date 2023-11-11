
/*
 * GlibConverter.cpp
 *
 *  Created on: Apr 4, 2012
 *      Author: scaille
 */

#include<iostream>

#include "glib_converter.hh"

namespace ch_skymarshall::gui::glib {
using namespace std;
using namespace Glib;

string_to_ustring::string_to_ustring() = default;

string_to_ustring::~string_to_ustring() = default;

string string_to_ustring::convert_component_value_to_property_value(
		const Glib::ustring _componentValue) {
	return _componentValue;
}

Glib::ustring string_to_ustring::convert_property_value_to_component_value(
		const string _propertyValue) {
	return Glib::ustring(_propertyValue);
}
}
