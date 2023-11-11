
/*
 * GlibConverter.hh
 *
 *  Created on: Apr 4, 2012
 *      Author: scaille
 */

#ifndef GLIB_CONVERTER_HH_
#define GLIB_CONVERTER_HH_

#include <converters.hh>
#include <glibmm.h>
#include <string>

#include "binding_interface.hh"

namespace ch_skymarshall::gui::glib {

using namespace std;
using namespace Glib;

class string_to_ustring: public binding_converter<string, ustring> {

public:
	string_to_ustring();

	~string_to_ustring() override;

	string convert_component_value_to_property_value(
			const ustring _componentValue) override;
	ustring convert_property_value_to_component_value(
			const string _propertyValue) override;

	static shared_ptr<binding_converter<string, ustring>> of() {
		return make_shared<string_to_ustring>();
	}
};


}
#endif /* GLIB_CONVERTER_HH_ */
