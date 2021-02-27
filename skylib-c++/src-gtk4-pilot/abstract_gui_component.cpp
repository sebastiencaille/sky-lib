/*
 * AbstractComponent.cpp
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */
#include "abstract_gui_component.hh"

namespace ch_skymarshall::gui::pilot {

using namespace std;

abstract_gui_component::abstract_gui_component() {
}

abstract_gui_component::~abstract_gui_component() {
}

void abstract_gui_component::wait(polling &_polling, chrono::seconds _duration,
		std::function<void(polling&)> _onFailure) {

	const chrono::time_point<chrono::steady_clock> start =
			chrono::steady_clock::now();
	while (chrono::steady_clock::now() - start
			< chrono::duration_cast<chrono::steady_clock::duration>(_duration)) {
		if (executePolling(_polling)) {
			return;
		}
	}
	_onFailure(_polling);
}

bool abstract_gui_component::executePolling(polling &_polling) {
	return _polling.getPollingFunction()();
}

}
