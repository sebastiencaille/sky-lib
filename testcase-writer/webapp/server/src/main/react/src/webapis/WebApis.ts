import { Metadata, TestDictionary, TestCase, Context } from './Types'

const API_URL = process.env.REACT_APP_API_URL;


const loadCurrentContext = (callback: (context: Context) => void) =>
	fetch(API_URL + '/context')
		.then(r => r.json()).then(r => callback(r as Context));

const listAllDictionaries = (callback: (metadata: Metadata[]) => void) =>
	fetch(API_URL + '/dictionary')
		.then(r => r.json()).then(r => callback(r as Metadata[]));

const loadCurrentDictionary = (callback: (dict: TestDictionary) => void) =>
	fetch(API_URL + '/dictionary/current')
		.then(r => r.json()).then(r => callback(r as TestDictionary));

const listAllTestCases = (callback: (metadata: Metadata[]) => void) =>
	fetch(API_URL + '/testcase')
		.then(r => r.json()).then(r => callback(r as Metadata[]));

const loadCurrentTestCase = (callback: (dict: TestCase) => void) =>
	fetch(API_URL + '/testcase/current')
		.then(r => r.json()).then(r => callback(r as TestCase));


const selectCurrentDictionary = (transientId: string, callback: (context: Context) => void) =>
	fetch(API_URL + '/context',
		{
			method: 'PUT',
			headers: { 'Content-Type': "application/json" },
			body: JSON.stringify({ dictionary: transientId })
		})
		.then(r => r.json()).then(r => callback(r as Context));



const selectCurrentTestCase = (transientId: string, callback: (context: Context) => void) =>
	fetch(API_URL + '/context',
		{
			method: 'PUT',
			headers: { 'Content-Type': "application/json" },
			body: JSON.stringify({ testCase: transientId })
		})
		.then(r => r.json()).then(r => callback(r as Context));
		
const executeCurrentTestCase = () => {
		fetch(API_URL + '/testcase/current/execute',
		{
			method: 'POST',
		})
} 

const WebApis = {
	loadCurrentContext: loadCurrentContext,
	listAllDictionaries: listAllDictionaries,
	loadCurrentDictionary: loadCurrentDictionary,
	listAllTestCases: listAllTestCases,
	loadCurrentTestCase: loadCurrentTestCase,
	selectCurrentDictionary: selectCurrentDictionary,
	selectCurrentTestCase: selectCurrentTestCase,
	executeCurrentTestCase: executeCurrentTestCase
};

export default WebApis;
