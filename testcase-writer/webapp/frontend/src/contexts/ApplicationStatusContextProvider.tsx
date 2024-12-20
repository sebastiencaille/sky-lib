import { ReactNode, useCallback, useMemo, useReducer } from "react";
import { ApplicationStatus, ApplicationStatusAction, ApplicationStatusContext, ApplicationStatusContextUpdater, initialApplicationStatus } from "./ApplicationStatusContext";



function applicationStatusReducer(applicationStatus: ApplicationStatus, action: ApplicationStatusAction): ApplicationStatus {
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


export function ApplicationStatusProvider({ children }: Readonly<{ children: ReactNode[] | ReactNode }>): ReactNode {

	const [applicationStatus, applicationStatusDispatch] = useReducer(
		applicationStatusReducer,
		initialApplicationStatus
	);

	const safeApplicationStatus = useMemo(() => (applicationStatus), [applicationStatus]);
	const safeApplicationStatusDispatch = useCallback(applicationStatusDispatch, [applicationStatusDispatch]);

	return (
		<ApplicationStatusContext.Provider value={safeApplicationStatus}>
			<ApplicationStatusContextUpdater.Provider value={safeApplicationStatusDispatch} >
				{children}
			</ApplicationStatusContextUpdater.Provider>
		</ApplicationStatusContext.Provider>
	);
}
