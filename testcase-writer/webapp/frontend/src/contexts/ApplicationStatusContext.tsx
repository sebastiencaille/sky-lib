import React, { ReactNode, createContext, useReducer, Dispatch, useMemo } from 'react';

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

const initialApplicationStatus: ApplicationStatus = {
	webSocketConnected: false,
	errors: [],
	lastError: undefined
}

export const ApplicationStatusContext = createContext<ApplicationStatus>(initialApplicationStatus);

export const ApplicationStatusContextUpdater = createContext<Dispatch<ApplicationStatusAction>>(() => { });

const applicationStatusReducer = (applicationStatus: ApplicationStatus, action: ApplicationStatusAction): ApplicationStatus => {
	const newStatus = { ...applicationStatus };
	switch (action.type) {
		case 'webSocketConnected': {
			newStatus.webSocketConnected = action.webSocketConnected ?? false;
			return newStatus;
		}
		case 'addError': {
			newStatus.errors = [...applicationStatus.errors.slice(), action.error ?? '']
			newStatus.lastError = action.error ?? '';
			return newStatus;
		}
		case 'clearErrors': {
			newStatus.errors = [];
			newStatus.lastError = undefined;
			return newStatus;
		}
		default: {
			throw Error('Unknown action: ' + action.type);
		}
	}
}

export const ApplicationStatusProvider = ({ children }: { children: ReactNode[] | ReactNode }): ReactNode => {

	const [applicationStatus, applicationStatusDispatch] = useReducer(
		applicationStatusReducer,
		initialApplicationStatus
	);

	const safeApplicationStatus = useMemo(() => (applicationStatus), [applicationStatus]);
	const safeApplicationStatusDispatch = useMemo(() => (applicationStatusDispatch), [applicationStatusDispatch]);

	return (
		<ApplicationStatusContext.Provider value={safeApplicationStatus}>
			<ApplicationStatusContextUpdater.Provider value={safeApplicationStatusDispatch} >
				{children}
			</ApplicationStatusContextUpdater.Provider>
		</ApplicationStatusContext.Provider>
	);
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

