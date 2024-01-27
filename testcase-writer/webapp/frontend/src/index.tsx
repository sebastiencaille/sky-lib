import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import { ApplicationStatusProvider } from './contexts/ApplicationStatusContext';
import { UserContextProvider } from './contexts/UserContext';

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);
root.render(
	<React.StrictMode>
		<ApplicationStatusProvider>
			<UserContextProvider>
				<App />
			</UserContextProvider>
		</ApplicationStatusProvider>
	</React.StrictMode>
);

