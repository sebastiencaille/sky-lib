#include <stdlib.h>
#include <errno.h>
#include "converters.hh"

namespace ch_skymarshall::gui::converters {

using namespace std;

int_to_string::int_to_string() = default;

int_to_string::~int_to_string() = default;

int int_to_string::convert_component_value_to_property_value(
		const string _componentValue) {
	char *endPtr;
	errno = 0;
	long result = strtol(_componentValue.c_str(), &endPtr, 10);
	if (errno != 0 || *endPtr != '\0' || _componentValue.empty()) {
		throw gui_exception("Invalid number: " + _componentValue);
	}
	return (int) result;
}

string int_to_string::convert_property_value_to_component_value(
		const int _propertyValue) {
	return to_string(_propertyValue);
}

}
