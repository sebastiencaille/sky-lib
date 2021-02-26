/*
 *Copyright (c) 2013 Sebastien Caille.
 *All rights reserved.
 *
 *Redistribution and use in source and binary forms are permitted
 *provided that the above copyright notice and this paragraph are
 *duplicated in all such forms and that any documentation,
 *advertising materials, and other materials related to such
 *distribution and use acknowledge that the software was developed
 *by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *from this software without specific prior written permission.
 *THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
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

const string string_to_ustring::convert_component_value_to_property_value(
		const Glib::ustring _componentValue) {
	return _componentValue;
}

const Glib::ustring string_to_ustring::convert_property_value_to_component_value(
		const string _propertyValue) {
	return ustring(_propertyValue);
}

}

