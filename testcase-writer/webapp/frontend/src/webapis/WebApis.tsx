import { Metadata, TestDictionary, TestCase, Context, ExportType } from './Types'
import { addError, useApplicationStatusContextUpdater } from '../contexts/ApplicationStatusContext';
import { UserContext, useUserContext } from '../contexts/UserContext';
import { useCallback } from "react";

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
		loaded: false,
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

/**
 * Performs the call and handle the errors
 */
async function call(url: string, init?: RequestInit): Promise<any> {
	return fetch(url, init)
		.then(result => {
				if (result.status / 100 != 2 && result.status / 100 != 3) {
					const msg = 'Unexpected error ' + result.status + ': ' + result.statusText;
					webApiErrorHandler.handle(msg);
					throw new Error(msg);
				}
				webApiErrorHandler.handle('');
				console.log(result);
				if (result.headers.get('Content-Type') === "application/json") {
					return result.json()
				}
				return result.text();
			},
			reason => {
				let msg;
				if (reason instanceof Error) {
					msg = reason.message;
				} else {
					msg = reason.toString();
				}
				webApiErrorHandler.handle(msg);
			});
}

function headers(): HeadersInit {
	const context = contextProvider.currentContext;
	return { 'X-UserContext': context.dictionary ?? '' };
}

function mapUserContextFromBackend(context: Context): UserContext {
	return {
		loaded: true,
		dictionary: context.dictionary?? null,
		testCase: context.testCase ?? null
	};
}

async function login(): Promise<void> {
	return call(API_URL + '/context', {
			method: 'POST',
	});
}

async function loadCurrentContext(): Promise<UserContext> {
	return call(API_URL + '/context')
		.then(r => mapUserContextFromBackend(r as Context));
}

async function validateContext(userContext: UserContext): Promise<UserContext> {
	return call(API_URL + '/context',
		{
			method: 'PUT',
			headers: { 'Content-Type': "application/json" },
			body: JSON.stringify({ dictionary: userContext.dictionary, testCase: userContext.testCase })
		})
	.then(r => mapUserContextFromBackend(r as Context));
}

async function listAllDictionaries(): Promise<Metadata[]> {
	return call(API_URL + '/dictionary')
		.then(r => (r as Metadata[]));
}

async function loadDictionary(dictionary: string): Promise<TestDictionary> {
	return call(API_URL + `/dictionary/${dictionary}`)
		.then(r => (r as TestDictionary));
}

async function listAllTestCases(): Promise<Metadata[]> {
	return call(API_URL + '/testcase',
		{
			headers: headers()
		})
		.then(r => (r as Metadata[]));
}

async function loadTestCase(tc: string):Promise<TestCase> {
	return call(API_URL + '/testcase/' + tc,
		{
			headers: headers()
		})
		.then(r => (r as TestCase));
}

async function executeCurrentTestCase(tabId: string) {
	const tc = contextProvider.currentContext.testCase;
	return call(API_URL + `/testcase/${tc}/execute?tabId=${tabId}`,
		{
			method: 'POST',
			headers: headers()
		});
}

async function exportCurrentTestCase(format: ExportType): Promise<string> {
	const tc = contextProvider.currentContext.testCase;
	return call(API_URL + `/testcase/${tc}/export?format=${format}`,
		{
			headers: headers()
		})
		.then(r => typeof r === 'string' ? r : r.join('\n')
		);
}

const WebApis = {
	Component: Component,
	login: login,
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
