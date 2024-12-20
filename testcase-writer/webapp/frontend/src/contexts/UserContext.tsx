import { ReactNode, createContext, useReducer, Dispatch, useMemo, useCallback, useContext } from 'react';

export interface UserContext {
	dictionary: string | null;
	testCase: string | null;
}

export interface UserContextAction {
	type: string;
	newContext: UserContext;
}

const initialUserContext: UserContext = {
	dictionary: null,
	testCase: null,
}

const UserContext = createContext<UserContext>(initialUserContext);

const UserContextUpdater = createContext<Dispatch<UserContextAction>>(() => { });

export function useUserContext(): UserContext {
	return useContext(UserContext);
}

export function useUserContextUpdater(): Dispatch<UserContextAction> {
	return useContext(UserContextUpdater);
}

function userContextReducer(_state: UserContext, action: UserContextAction): UserContext {
	if (action.type === 'update') {
		return action.newContext;
	}
	throw Error('Unknown action: ' + action.type);
}

export function UserContextProvider({ children }: Readonly<{ children: ReactNode[] | ReactNode }>): ReactNode {

	const [userContext, userContextDispatch] = useReducer(
		userContextReducer,
		initialUserContext
	);

	const safeUserContext = useMemo(() => (userContext), [userContext]);
	const safeUserContextDispatch = useCallback(userContextDispatch, [userContextDispatch]);

	return (
		<UserContext.Provider value={safeUserContext}>
			<UserContextUpdater.Provider value={safeUserContextDispatch} >
				{children}
			</UserContextUpdater.Provider>
		</UserContext.Provider>
	);
}

export function contextUpdate(newContext: UserContext): UserContextAction {
	return { type: 'update', newContext: newContext };
}


