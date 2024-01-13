import React from 'react';
import TestCaseHelper from '../helpers/TestCaseHelper';
import { TestDictionary, TestCase, StepStatus, TestStep } from '../webapis/Types';

interface ITestCaseProps {
	dictionary?: TestDictionary;
	testCase?: TestCase;

	stepStatuses: Map<number, StepStatus>;
}

const createTestCaseStep = (dictionary: TestDictionary, step: TestStep, stepStatus?: StepStatus) => {
	let selector;
	let parameter;
	if (TestCaseHelper.hasSelector(dictionary, step.action)) {
		selector = step.parametersValue[0];
		parameter = step.parametersValue[1];
	} else {
		selector = undefined;
		parameter = step.parametersValue[0];
	}

	const stepClass = (stepStatus?.state as string) || "";
	return (
		<tbody className='steps' key={"tcStep" + step.ordinal}>
			<tr>
				<td rowSpan={2} className={stepClass}>{step.ordinal}</td>
				<td colSpan={4}>{step.humanReadable ?? '---'}</td>
			</tr>
			<tr>
				<td></td>
				<td>{TestCaseHelper.descriptionOf(dictionary, [step.actor])}</td>
				<td>{TestCaseHelper.descriptionOf(dictionary, [step.action])}</td>
				<td>{TestCaseHelper.descriptionOfParameter(dictionary, selector)}</td>
				<td>{TestCaseHelper.descriptionOfParameter(dictionary, parameter)}</td>
			</tr>
		</tbody>);

}

const createTestCaseSteps = (stepStatuses: Map<number, StepStatus>, dictionary?: TestDictionary, testCase?: TestCase) => {
	if (!dictionary || !testCase) {
		return;
	}
	const rows = [];
	rows.push(
		<thead key="tcHead">
			<tr>
				<th>Ordinal</th><th>Actor</th><th>Action</th><th>Selector</th><th>Parameter</th>
			</tr>
		</thead>);

	if (!testCase) {
		return rows;
	}
	for (const testStep of testCase.steps) {
		const status = stepStatuses.get(testStep.ordinal);
		rows.push(createTestCaseStep(dictionary, testStep, status));
	}
	return rows;
}

function TestCaseTable(props: Readonly<ITestCaseProps>) {
	if (!props.testCase) {
		return null;
	}

	return (
		<table className='steps'>
			{createTestCaseSteps(props.stepStatuses, props.dictionary, props.testCase)}
		</table>
	);
}

export default TestCaseTable;
