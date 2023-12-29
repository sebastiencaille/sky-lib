import React from 'react';
import { StompSessionProvider, useSubscription } from "react-stomp-hooks";
import { StepStatus } from '../webapis/Types'
import { ApplicationStatusAction, ApplicationStatusContextUpdater, updateWebConnection } from '../contexts/ApplicationStatusContext';

interface StepStatusUpdater {
	stepStatusUpdate: (status: StepStatus) => void
}

function SubscribingComponent(props: Readonly<StepStatusUpdater>) {
	useSubscription("/user/queue/testexec", (message) => props.stepStatusUpdate((JSON.parse(message.body).payload) as StepStatus));
	return (<div></div>)
}


class WebApiFeedback extends React.PureComponent<Readonly<StepStatusUpdater>> {

	updater: React.Dispatch<ApplicationStatusAction> | undefined = undefined;

	constructor(props: StepStatusUpdater) {
		super(props);
		this.connect = this.connect.bind(this);
		this.disconnect = this.disconnect.bind(this);
	}

	private connect = () => {
		if (this.updater) {
			this.updater(updateWebConnection(true));
		}
	}
	private disconnect = () => {
		if (this.updater) {
			this.updater(updateWebConnection(false));
		}
	}

	render() {
		return (
			<ApplicationStatusContextUpdater.Consumer>
				{(updateApplicationStatus) => {
					this.updater = updateApplicationStatus;
					return (
						<StompSessionProvider url={"/api/websocket"}
							onConnect={this.connect}
							onDisconnect={this.disconnect} >
							<SubscribingComponent stepStatusUpdate={this.props.stepStatusUpdate} />
						</StompSessionProvider >
					)
				}
				}
			</ApplicationStatusContextUpdater.Consumer>
		);
	}
}
export default WebApiFeedback;