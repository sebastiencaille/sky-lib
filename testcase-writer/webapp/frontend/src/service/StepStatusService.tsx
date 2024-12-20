import { StepStatus } from "../webapis/Types";

export interface StepStatusAction {
	type: string;
	stepStatus?: StepStatus;
}


export const handleStepStatusAction = (stepStatuses: Map<number, StepStatus>, action: StepStatusAction): Map<number, StepStatus> => {
	switch (action.type) {
		case 'update': {
			const newStatuses = new Map(stepStatuses)
			if (action.stepStatus) {
				newStatuses.set(action.stepStatus.ordinal, action.stepStatus);
			}
			return newStatuses;
		}
		case 'clear': {
			return new Map();
		}
		default: {
			throw Error('Unknown action: ' + action.type);
		}
	}
}


export function stepStatusUpdate(stepStatus: StepStatus): StepStatusAction {
	return { type: 'update', stepStatus: stepStatus };
}

export function clearStepStatuses(): StepStatusAction {
	return { type: 'clear' };
}
