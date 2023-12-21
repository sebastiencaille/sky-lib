import React from 'react';
import { ErrorState } from '../service/Errors';

interface IErrorDisplayProps {
	errors: ErrorState;
}

class ErrorDisplay extends React.Component<IErrorDisplayProps, object> {

	render() {
		return (<div className="error">{this.props.errors.lastError}</div>);
	}
}

export default ErrorDisplay;