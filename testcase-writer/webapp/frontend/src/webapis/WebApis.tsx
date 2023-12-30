import React, { useCallback } from 'react';
import { Metadata, TestDictionary, TestCase, Context, ExportType } from './Types'
import { addError, useApplicationStatusContextUpdater } from '../contexts/ApplicationStatusContext';

const API_URL = process.env.REACT_APP_API_URL;

interface WebApiErrorHandler {
	handle: (msg: string) => void;
}

const webApiErrorHandler: WebApiErrorHandler = {
	handle: (msg: string) => { console.log(msg); }
};

export function ErrorHandler() {

	const applicationStatusUpdater = useApplicationStatusContextUpdater();
	webApiErrorHandler.handle = useCallback((msg) => applicationStatusUpdater(addError(msg)), [applicationStatusUpdater]);

	return (<div />);
}

async function callResult(url: string, init?: RequestInit): Promise<Response> {
	const r = await fetch(url, init);
	if (!r.ok) {
		throw new Error('Unexpected error ' + r.status + ': ' + r.statusText);
	}
	return r;
};


/**
 * Performs the call, handle the errors and unwrap the received object
 */
async function call(url: string, init?: RequestInit): Promise<object> {
	return await callResult(url, init).then(r => r.json());
};

/**
 * Performs the call and handle the errors
 */
function wrap(promise: Promise<void | object>): void {
	promise.catch(reason => {
		let msg;
		if (reason instanceof Error) {
			msg = reason.message;
		} else {
			msg = reason.toString();
		}
		webApiErrorHandler.handle(msg)
	});
};

function loadCurrentContext(callback: (context: Context) => void) {
	wrap(call(API_URL + '/context').then(r => callback(r as Context)));
}
function listAllDictionaries(callback: (metadata: Metadata[]) => void) {
	wrap(call(API_URL + '/dictionary')
		.then(r => callback(r as Metadata[])));
}

function loadCurrentDictionary(callback: (dict: TestDictionary) => void) {
	wrap(call(API_URL + '/dictionary/current')
		.then(r => callback(r as TestDictionary)));
}

function listAllTestCases(callback: (metadata: Metadata[]) => void) {
	wrap(call(API_URL + '/testcase')
		.then(r => callback(r as Metadata[])));
}

function loadCurrentTestCase(callback: (dict: TestCase) => void) {
	wrap(call(API_URL + '/testcase/current')
		.then(r => callback(r as TestCase)));
}

function selectCurrentDictionary(transientId: string, callback: (context: Context) => void) {
	wrap(call(API_URL + '/context',
		{
			method: 'PUT',
			headers: { 'Content-Type': "application/json" },
			body: JSON.stringify({ dictionary: transientId })
		})
		.then(r => callback(r as Context)));
}

function selectCurrentTestCase(transientId: string, callback: (context: Context) => void) {
	wrap(call(API_URL + '/context',
		{
			method: 'PUT',
			headers: { 'Content-Type': "application/json" },
			body: JSON.stringify({ testCase: transientId })
		})
		.then(r => callback(r as Context)));
}

function executeCurrentTestCase(tabId: string) {
	wrap(callResult(API_URL + '/testcase/current/execute?tabId=' + tabId,
		{
			method: 'POST',
		}));
}

function exportCurrentTestCase(format: ExportType, callback: (content: string) => void) {
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
	ErrorHandler: ErrorHandler,
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
