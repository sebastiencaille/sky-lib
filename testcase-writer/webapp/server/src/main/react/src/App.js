import React from 'react';
import Dictionary from './Dictionary.js';
import WebApis from './WebApis.js';
import './App.css';

class App extends React.Component {

	state = {
		currentDictionary: null
	};

	dictionaryChanged = () => {
		WebApis.loadCurrentDictionary(dict => this.setState({ currentDictionary: dict }));
	}

	render() {
		return (<div className="App">
			<Dictionary currentDictionary={this.state.currentDictionary}
				dictionaryChanged={this.dictionaryChanged} />
		</div>);
	}
}

export default App;


