import React from 'react';
import Dictionary from './Dictionary.js';
import TestCase from './TestCases.js';
import WebApis from './WebApis.js';
import './App.css';

class App extends React.Component {

	state = {
		currentDictionary: null,
		currentTestCase: null
	};

	dictionaryChanged = () => {
		WebApis.loadCurrentDictionary(dict => this.setState({ currentDictionary: dict }));
	}

	testCaseChanged = () => {
		if (this.state.currentDictionary) {
			WebApis.loadCurrentTestCase(tc => this.setState({ currentTestCase: tc }));
		}
	}

	render() {
		return (<div className="App">
			<Dictionary
				currentDictionary={this.state.currentDictionary}
				dictionaryChanged={this.dictionaryChanged} />
			<TestCase
				currentDictionary={this.state.currentDictionary}
				currentTestCase={this.state.currentTestCase}
				testCaseChanged={this.testCaseChanged} />
		</div>);
	}
}

export default App;
