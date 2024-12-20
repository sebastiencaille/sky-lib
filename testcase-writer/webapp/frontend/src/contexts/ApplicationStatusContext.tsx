import {  createContext, Dispatch, useContext } from 'react';

export interface ApplicationStatus {
	webSocketConnected: boolean;
	errors: string[];
	lastError?: string;
}

export interface ApplicationStatusAction {
	type: string;
	error?: string;
	webSocketConnected?: boolean;
}


export const initialApplicationStatus: ApplicationStatus = {
	webSocketConnected: false,
	errors: [],
	lastError: undefined
}

export const ApplicationStatusContext = createContext<ApplicationStatus>(initialApplicationStatus);

export const ApplicationStatusContextUpdater = createContext<Dispatch<ApplicationStatusAction>>(() => { });


export function useApplicationStatusContext(): ApplicationStatus {
	return useContext(ApplicationStatusContext);
}

export function useApplicationStatusContextUpdater(): Dispatch<ApplicationStatusAction> {
	return useContext(ApplicationStatusContextUpdater);
}

export function updateWebConnection(webSocketConnected: boolean): ApplicationStatusAction {
	return { type: 'webSocketConnected', webSocketConnected: webSocketConnected };
}

export function clearErrors(): ApplicationStatusAction {
	return { type: 'clearErrors' };
}

export function addError(error: string): ApplicationStatusAction {
	return { type: 'addError', error: error };
}

