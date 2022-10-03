import $ from 'jquery';

const API_URL = process.env.REACT_APP_API_URL;

const listAllDictionaries = (callback) =>
	$.getJSON(API_URL + '/dictionary')
		.then(callback);

const loadCurrentDictionary = (callback) =>
	$.getJSON(API_URL + '/dictionary/current')
		.then(callback);

const listAllTestCases = (callback) =>
	$.getJSON(API_URL + '/testcase')
		.then(callback);

const loadCurrentTestCase = (callback) =>
	$.getJSON(API_URL + '/testcase/current')
		.then(callback);

const selectCurrentDictionary = (transientId, callback) =>
	$.ajax({
		type: 'PUT',
		url: API_URL + '/context',
		contentType: "application/json",
		data: JSON.stringify({ dictionary: transientId }),
		success: (newContext) => callback(newContext)
	});
	
const selectCurrentTestCase = (transientId, callback) =>
	$.ajax({
		type: 'PUT',
		url: API_URL + '/context',
		contentType: "application/json",
		data: JSON.stringify({ testCase: transientId }),
		success: (newContext) => callback(newContext)
	});

const WebApis = {
	listAllDictionaries: listAllDictionaries,
	loadCurrentDictionary: loadCurrentDictionary,
	listAllTestCases: listAllTestCases,
	loadCurrentTestCase: loadCurrentTestCase,
	selectCurrentDictionary: selectCurrentDictionary,
	selectCurrentTestCase: selectCurrentTestCase,
};

export default WebApis;
