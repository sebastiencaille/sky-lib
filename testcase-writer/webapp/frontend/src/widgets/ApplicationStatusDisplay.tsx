import React from 'react';
import { useApplicationStatusContext } from '../contexts/ApplicationStatusContext';

export function ApplicationStatusDisplay() {
	const applicationStatus = useApplicationStatusContext();
	return (<div>
		<div className="error">{applicationStatus.lastError}</div>
		<table id="application_status"><tr><td className={"status_available_" + applicationStatus.webSocketConnected}>WebSocket</td></tr></table>
	</div>);
}

