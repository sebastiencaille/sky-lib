import { useCallback } from 'react';
import { Metadata, TestDictionary, TestCase, Context, ExportType } from './Types'
import { addError, useApplicationStatusContextUpdater } from '../contexts/ApplicationStatusContext';
import { UserContext, useUserContext } from '../contexts/UserContext';

const API_URL = '/api/v0';

interface WebApiErrorHandler {
	handle: (msg: string) => void;
}

const webApiErrorHandler: WebApiErrorHandler = {
	handle: (msg: string) => { console.log(msg); }
};

interface ContextProvider {
	currentContext: UserContext;
}

const contextProvider: ContextProvider = {
	currentContext: {
		dictionary: null,
		testCase: null
	}
}

function Component() {

	const applicationStatusUpdater = useApplicationStatusContextUpdater();
	webApiErrorHandler.handle = useCallback((msg) => applicationStatusUpdater(addError(msg)), [applicationStatusUpdater]);
	contextProvider.currentContext = useUserContext();
	return (<div></div>);
}


async function callResult(url: string, init?: RequestInit): Promise<Response> {
	const r = await fetch(url, init);
	if (!r.ok) {
		throw new Error('Unexpected error ' + r.status + ': ' + r.statusText);
	}
	return r;
}


/**
 * Performs the call, handle the errors and unwrap the received object
 */
async function call(url: string, init?: RequestInit): Promise<object> {
	return await callResult(url, init).then(r => r.json());
}

function headers(): HeadersInit {
	const context = contextProvider.currentContext;
	return { 'X-UserContext': context.dictionary ?? '' };
}

/**
 * Performs the call and handle the errors
 */
function wrap(promise: Promise<void | object>): void {
	promise
	.then(() => {
		webApiErrorHandler.handle('');
	})
	.catch(reason => {
		let msg;
		if (reason instanceof Error) {
			msg = reason.message;
		} else {
			msg = reason.toString();
		}
		webApiErrorHandler.handle(msg)
	});
}

function mapUserContextFromBackend(context: Context) {
	return {
		dictionary: context.dictionary?? null,
		testCase: context.testCase ?? null
	};
}

function loadCurrentContext(callback: (context: UserContext) => void) {
	wrap(call(API_URL + '/context').then(r => callback(mapUserContextFromBackend(r as Context))));
}

function validateContext(userContext: UserContext, callback: (context: UserContext) => void) {
	wrap(call(API_URL + '/context',
		{
			method: 'PUT',
			headers: { 'Content-Type': "application/json" },
			body: JSON.stringify({ dictionary: userContext.dictionary, testCase: userContext.testCase })
		})
		.then(r => callback(mapUserContextFromBackend(r as Context))));
}

function listAllDictionaries(callback: (metadata: Metadata[]) => void) {
	wrap(call(API_URL + '/dictionary')
		.then(r => callback(r as Metadata[])));
}

function loadDictionary(dictionary: string, callback: (dict: TestDictionary) => void) {
	wrap(call(API_URL + `/dictionary/${dictionary}`)
		.then(r => callback(r as TestDictionary)));
}

function listAllTestCases(callback: (metadata: Metadata[]) => void) {
	wrap(call(API_URL + '/testcase',
		{
			headers: headers()
		})
		.then(r => callback(r as Metadata[])));
}

function loadTestCase(tc: string, callback: (tc: TestCase) => void) {
	wrap(call(API_URL + '/testcase/' + tc,
		{
			headers: headers()
		})
		.then(r => callback(r as TestCase)));
}

function executeCurrentTestCase(tabId: string) {
	const tc = contextProvider.currentContext.testCase;
	wrap(callResult(API_URL + `/testcase/${tc}/execute?tabId=${tabId}`,
		{
			method: 'POST',
			headers: headers()
		}));
}

function exportCurrentTestCase(format: ExportType, callback: (content: string) => void) {
	const tc = contextProvider.currentContext.testCase;
	wrap(callResult(API_URL + `/testcase/${tc}/export?format=${format}`,
		{
			headers: headers()
		})
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
	Component: Component,
	loadCurrentContext: loadCurrentContext,
	validateContext: validateContext,
	listAllDictionaries: listAllDictionaries,
	loadDictionary: loadDictionary,
	listAllTestCases: listAllTestCases,
	loadTestCase: loadTestCase,
	executeCurrentTestCase: executeCurrentTestCase,
	exportCurrentTestCase: exportCurrentTestCase,
};

export default WebApis;
