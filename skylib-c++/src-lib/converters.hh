
/*
 * IntConverters.hh
 *
 *  Created on: Mar 4, 2012
 *      Author: scaille
 */

#ifndef INT_CONVERTERS_HH_
#define INT_CONVERTERS_HH_

#include <string>

#include "binding_interface.hh"

namespace ch_skymarshall::gui::converters {

using namespace std;

class int_to_string: public binding_converter<int, string> {

public:
	int_to_string();

	~int_to_string() override;

	int convert_component_value_to_property_value(
			const string& _componentValue) override;

	string convert_property_value_to_component_value(
			const int& _propertyValue) override;

	static shared_ptr<binding_converter<int, string>> of() {
		return make_shared<int_to_string>();
	}
};

}

#endif /* INT_CONVERTERS_HH_ */

