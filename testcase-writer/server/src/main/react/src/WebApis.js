import $ from 'jquery';

const API_URL = process.env.REACT_APP_API_URL;

const loadAllDictionaries = (callback) =>
	$.getJSON(API_URL + '/dictionaries')
		.then(callback);


const loadCurrentDictionary = (callback) =>
	$.getJSON(API_URL + '/dictionaries/current')
		.then(callback);


const selectCurrentDictionary = (transientId, callback) =>
	$.ajax({
		type: 'PUT',
		url: API_URL + '/context',
		contentType: "application/json",
		data: JSON.stringify({ dictionary: transientId }),
		success: (newContext) => callback(newContext)
	});

const WebApis = {
	loadAllDictionaries: loadAllDictionaries,
	selectCurrentDictionary: selectCurrentDictionary,
	loadCurrentDictionary: loadCurrentDictionary
};

export default WebApis;
