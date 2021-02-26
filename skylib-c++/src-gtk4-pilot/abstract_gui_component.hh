/*
 * AbstractComponent.h
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */

#ifndef ABSTRACTGUICOMPONENT_H_
#define ABSTRACTGUICOMPONENT_H_

#include <functional>
#include <chrono>

namespace ch_skymarshall::gui::pilot {

using namespace std;

class polling {

private:
	function<bool ()> m_pollingFunction;

public:
	polling(function<bool ()> _pollingFunction):m_pollingFunction(_pollingFunction) {
	}

	virtual ~polling() {
	}

	std::function<bool ()> getPollingFunction() {
		return m_pollingFunction;
	}
};

class abstract_gui_component {
protected:
	void wait(polling& _polling, chrono::seconds _duration, function<void (polling&)> _onFailure);

public:
	abstract_gui_component();

	virtual ~abstract_gui_component();
};

}

#endif /* ABSTRACTGUICOMPONENT_H_ */
