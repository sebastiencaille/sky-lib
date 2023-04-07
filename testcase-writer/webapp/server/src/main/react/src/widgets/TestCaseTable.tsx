import React from 'react';
import DictionaryHelper from '../helpers/DictionaryHelper';
import TestCaseHelper from '../helpers/TestCaseHelper';
import { TestDictionary, TestCase, StepStatus } from '../webapis/Types';

interface ITestCaseProps {
	dictionary?: TestDictionary;
	testCase?: TestCase;
	stepStatuses: Map<number, StepStatus>;
}

class TestCaseTable extends React.Component<ITestCaseProps> {

	createTestCaseSteps = () => {
		if (!this.props.dictionary || !this.props.testCase) {
			return;
		}
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
				selector = undefined;
				parameter = step.parametersValue[0];
			}

			const status = this.props.stepStatuses.get(step.ordinal);
			const stepClass = (status?.state as string) || "";
			rows.push(
				<tbody className='steps' key={"tcStep" + step.ordinal}>
					<tr>
					<td rowSpan={2} className={stepClass}>{step.ordinal}</td>
					<td colSpan={4}>{step.humanReadable || '---'}</td>
					</tr>
					<tr>
						<td></td>
						<td>{DictionaryHelper.descriptionOf(dict, [step.actor])}</td>
						<td>{DictionaryHelper.descriptionOf(dict, [step.action])}</td>
						<td>{TestCaseHelper.descriptionOfParameter(dict, selector)}</td>
						<td>{TestCaseHelper.descriptionOfParameter(dict, parameter)}</td>
					</tr>
				</tbody>);
		}
		return rows;
	}

	render() {
		return (
			<table className='steps'>
				{this.createTestCaseSteps()}
			</table>
		);
	}
}

export default TestCaseTable;
