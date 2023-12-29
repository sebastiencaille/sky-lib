import React from 'react';
import TestCaseHelper from '../helpers/TestCaseHelper';
import { TestDictionary, TestCase, StepStatus, TestStep } from '../webapis/Types';

interface ITestCaseProps {
	dictionary?: TestDictionary;
	testCase?: TestCase;

	stepStatuses: Map<number, StepStatus>;
}


function TestCaseTable(props: Readonly<ITestCaseProps>) {

	const createTestCaseStep = (dict: TestDictionary, step: TestStep) => {
		let selector;
		let parameter;
		if (TestCaseHelper.hasSelector(dict, step.action)) {
			selector = step.parametersValue[0];
			parameter = step.parametersValue[1];
		} else {
			selector = undefined;
			parameter = step.parametersValue[0];
		}

		const status = props.stepStatuses.get(step.ordinal);
		const stepClass = (status?.state as string) || "";
		return (
			<tbody className='steps' key={"tcStep" + step.ordinal}>
				<tr>
					<td rowSpan={2} className={stepClass}>{step.ordinal}</td>
					<td colSpan={4}>{step.humanReadable ?? '---'}</td>
				</tr>
				<tr>
					<td></td>
					<td>{TestCaseHelper.descriptionOf(dict, [step.actor])}</td>
					<td>{TestCaseHelper.descriptionOf(dict, [step.action])}</td>
					<td>{TestCaseHelper.descriptionOfParameter(dict, selector)}</td>
					<td>{TestCaseHelper.descriptionOfParameter(dict, parameter)}</td>
				</tr>
			</tbody>);

	}

	const createTestCaseSteps = () => {
		if (!props.dictionary || !props.testCase) {
			return;
		}
		const rows = [];
		rows.push(
			<thead key="tcHead">
				<tr>
					<th>Ordinal</th><th>Actor</th><th>Action</th><th>Selector</th><th>Parameter</th>
				</tr>
			</thead>);

		const dict = props.dictionary;
		const tc = props.testCase;
		if (!tc) {
			return rows;
		}
		for (const testStep of tc.steps) {
			rows.push(createTestCaseStep(dict, testStep));
		}
		return rows;
	}

	return (
		<table className='steps'>
			{createTestCaseSteps()}
		</table>
	);
}

export default TestCaseTable;
