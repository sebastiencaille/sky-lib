import React from 'react';
import DictionarySelector from './DictionarySelector.js';
import TestCaseSelector from './TestCaseSelector.js';
import TestCase from './TestCase.js';
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
			WebApis.loadCurrentTestCase(tc => 
				this.setState({ currentTestCase: tc }))
		}
	}
	
	render() {
		return (
			<div className="App">
				<DictionarySelector
					currentDictionary={this.state.currentDictionary}
					dictionaryChanged={this.dictionaryChanged} />
				<TestCaseSelector
					dictionary={this.state.currentDictionary}
					currentTestCase={this.state.currentTestCase}
					testCaseChanged={this.testCaseChanged} />
				<TestCase
					dictionary={this.state.currentDictionary}
					testCase={this.state.currentTestCase} />
			</div>);
	}
}

export default App;
