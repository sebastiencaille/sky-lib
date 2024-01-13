import React from 'react';
import { useApplicationStatusContext } from '../contexts/ApplicationStatusContext';
import './ApplicationStatusDisplay.css'

export function ApplicationStatusDisplay() {
	const applicationStatus = useApplicationStatusContext();
	return (<div id="status_bar">
		<table id="application_status"><tbody>
			<tr>
				<td className={"status_available_" + applicationStatus.webSocketConnected} >WebSocket</td>
			</tr>
		</tbody></table>
		<p id="application_error">{applicationStatus.lastError}</p>
	</div>);
}

