import React from 'react';
import WebApis from './WebApis.js';

class TestCaseList extends React.Component {

	state = {
		allTests: [],
		currentSelection: '',
	};

	componentDidMount() {
		WebApis.loadAllDictionaries(dicts => this.setState({ allDictionaries: dicts }));
	}

	componentDidUpdate(oldProps, oldState) {
		let current;
		if (this.props.currentDictionary) {
			current = this.props.currentDictionary.metadata.transientId;
		}
		if (!current && this.state.allDictionaries[0]) {
			current = this.state.allDictionaries[0].transientId;
		}
		if (current && current !== this.state.currentSelection) {
			this.setState({ currentSelection: current })
		}
	}

	changeSelection = (e) => this.setState({ currentSelection: e.target.value });

	select = (e) => WebApis.selectCurrentDictionary(this.state.currentSelection, context => this.props.dictionaryChanged(context));

	createDictionaryItems = () => {
		let options = [];
		for (let dict of this.state.allDictionaries) {
			options.push(<option key={dict.transientId} value={dict.transientId}>{dict.description}</option>);
		}
		return options;
	}

	render() {
		return (<div>
			<select value={this.state.currentSelection} onChange={this.changeSelection}>
				{this.createDictionaryItems()}
			</select>
			<button onClick={this.select}>Select</button>
		</div>
		);
	}
}

export default DictionaryList;
