import React from 'react';
import PropTypes from 'prop-types';
import DictionaryHelper from './helpers/DictionaryHelper.js';
import TestCaseHelper from './helpers/TestCaseHelper.js';

class TestCase extends React.Component {

	createTestCaseSteps = () => {
		const rows = [];
		rows.push(
			<thead key="tcHead">
				<tr>
					<th>Ordinal</th><th>Actor</th><th>Action</th><th>Selector</th><th>Parameter</th>
				</tr>
			</thead>);

		const dict = this.props.dictionary;
		const tc = this.props.testCase;
		if (!tc) {
			return rows;
		}
		for (const step of tc.steps) {
			let selector;
			let parameter;
			if (DictionaryHelper.hasSelector(dict, step.action)) {
				selector = step.parametersValue[0];
				parameter = step.parametersValue[1];
			} else {
				selector = null;
				parameter = step.parametersValue[0];
			}

			rows.push(
				<tbody class="steps" key={"tcStep" + step.ordinal}>
					<tr>
						<td>{step.ordinal}</td>
						<td>{dict.descriptions[step.actorRef].description}</td>
						<td>{dict.descriptions[step.actionRef].description}</td>
						<td>{TestCaseHelper.descriptionOfParameter(dict, selector)}</td>
						<td>{TestCaseHelper.descriptionOfParameter(dict, parameter)}</td>
					</tr>
				</tbody>);
		}
		return rows;
	}

	render() {
		return (
			<table class="steps">
				{this.createTestCaseSteps()}
			</table>
		);
	}
}


TestCase.propTypes = {
	dictionary: PropTypes.object,
	testCase: PropTypes.object
};


export default TestCase;
