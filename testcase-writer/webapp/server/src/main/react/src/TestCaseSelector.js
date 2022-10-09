import React from 'react';
import PropTypes from 'prop-types';
import WebApis from './WebApis.js';

class TestCaseSelector extends React.Component {

	state = {
		allTestCases: [],
		currentSelection: '',
	};

	componentDidUpdate(oldProps, oldState) {
		if (this.props.dictionary && oldProps.dictionary !== this.props.dictionary) {
			WebApis.listAllTestCases(tcs => this.setState({ allTestCases: tcs }));
		}
		let current;
		if (this.props.currentTestCase) {
			current = this.props.currentTestCase.metadata.transientId;
		}
		if (!current && this.state.allTestCases[0]) {
			current = this.state.allTestCases[0].transientId;
		}
		if (current && current !== this.state.currentSelection) {
			this.setState({ currentSelection: current })
		}
	}

	changeSelection = (e) => this.setState({ currentSelection: e.target.value });

	select = (e) => WebApis.selectCurrentTestCase(this.state.currentSelection, context => this.props.testCaseChanged(context));

	createTestCaseItems = () => {
		let options = [];
		for (let tc of this.state.allTestCases) {
			options.push(<option key={tc.transientId} value={tc.transientId}>{tc.description}</option>);
		}
		return options;
	}

	render() {
		return (<div>
			<select value={this.state.currentSelection} onChange={this.changeSelection}>
				{this.createTestCaseItems()}
			</select>
			<button onClick={this.select}>Select</button>
		</div>
		);
	}
}

TestCaseSelector.propTypes = {
	dictionary: PropTypes.object,
	currentTestCase: PropTypes.object,
	testCaseChanged: PropTypes.func
};

export default TestCaseSelector;
