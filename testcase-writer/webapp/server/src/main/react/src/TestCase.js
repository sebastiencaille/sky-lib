import React from 'react';
import PropTypes from 'prop-types';

class TestCase extends React.Component {

	state = {
		currentTestCase: null,
	};

	createTestCaseSteps = () => {
		let rows = [];
		rows.push(
			<thead>
				<tr>
					<th>Ordinal</th>
					<th>Actor</th>
					<th>Action</th>
					<th>Selector</th>
					<th>Parameter</th>
				</tr>
			</thead>);
		if (!this.props.currentTestCase) {
			return rows;
		}
		for (let step of this.props.currentTestCase.steps) {
			let selector = step.parametersValue[0]?.id || 'none';
			let parameter = step.parametersValue[1]?.id || 'none';
			rows.push(
				<tbody>
					<tr>
						<td>{step.ordinal}</td>
						<td>{step.actorRef}</td>
						<td>{step.actionRef}</td>
						<td>{selector}</td>
						<td>{parameter}</td>
					</tr>
				</tbody>);
		}
		return rows;
	}

	render() {
		return (
			<table>
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
