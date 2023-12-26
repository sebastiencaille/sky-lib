import { useContext } from 'react';
import { Metadata, TestDictionary, TestCase, Context, ExportType } from './Types'
import { ApplicationStatusContextUpdater, addError } from '../contexts/ApplicationStatusContext';

const API_URL = process.env.REACT_APP_API_URL;


const callResult = async (url: string, init?: RequestInit): Promise<Response> => {
	const r = await fetch(url, init);
	if (!r.ok) {
		throw new Error('Unexpected error ' + r.status + ': ' + r.statusText);
	}
	return r;
};


/**
 * Performs the call, handle the errors and unwrap the received object
 */
const call = async (url: string, init?: RequestInit): Promise<object> => {
	const r = await callResult(url, init);
	return await r.json();
};

/**
 * Performs the call and handle the errors
 */
const wrap = (promise: Promise<void | object>): void => {
	promise.catch(reason => {
		let msg;
		if (reason instanceof Error) {
			msg = reason.message;
		} else {
			msg = reason.toString();
		}
		useContext(ApplicationStatusContextUpdater)(addError(msg))
	});
};

const loadCurrentContext = (callback: (context: Context) => void) =>
	wrap(call(API_URL + '/context').then(r => callback(r as Context)));

const listAllDictionaries = (callback: (metadata: Metadata[]) => void) =>
	wrap(call(API_URL + '/dictionary')
		.then(r => callback(r as Metadata[])));

const loadCurrentDictionary = (callback: (dict: TestDictionary) => void) =>
	wrap(call(API_URL + '/dictionary/current')
		.then(r => callback(r as TestDictionary)));

const listAllTestCases = (callback: (metadata: Metadata[]) => void) =>
	wrap(call(API_URL + '/testcase')
		.then(r => callback(r as Metadata[])));

const loadCurrentTestCase = (callback: (dict: TestCase) => void) =>
	wrap(call(API_URL + '/testcase/current')
		.then(r => callback(r as TestCase)));

const selectCurrentDictionary = (transientId: string, callback: (context: Context) => void) =>
	wrap(call(API_URL + '/context',
		{
			method: 'PUT',
			headers: { 'Content-Type': "application/json" },
			body: JSON.stringify({ dictionary: transientId })
		})
		.then(r => callback(r as Context)));

const selectCurrentTestCase = (transientId: string, callback: (context: Context) => void) =>
	wrap(call(API_URL + '/context',
		{
			method: 'PUT',
			headers: { 'Content-Type': "application/json" },
			body: JSON.stringify({ testCase: transientId })
		})
		.then(r => callback(r as Context)));

const executeCurrentTestCase = () => {
	wrap(callResult(API_URL + '/testcase/current/execute',
		{
			method: 'POST',
		}));
}

const exportCurrentTestCase = (format: ExportType, callback: (content: string) => void) => {
	wrap(callResult(API_URL + '/testcase/current/export?format=' + format)
		.then(async r => {
			if (r.headers.get("Content-Type")?.startsWith("application/json")) {
				const r_1 = await r.json();
				return (r_1 as Array<string>).join('\n');
			} else {
				return r.text();
			}
		}).then(r => callback(r)));
}

const WebApis = {
	loadCurrentContext: loadCurrentContext,
	listAllDictionaries: listAllDictionaries,
	loadCurrentDictionary: loadCurrentDictionary,
	listAllTestCases: listAllTestCases,
	loadCurrentTestCase: loadCurrentTestCase,
	selectCurrentDictionary: selectCurrentDictionary,
	selectCurrentTestCase: selectCurrentTestCase,
	executeCurrentTestCase: executeCurrentTestCase,
	exportCurrentTestCase: exportCurrentTestCase
};

export default WebApis;
