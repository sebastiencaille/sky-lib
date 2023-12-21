
export type IErrorHandler = ((error: string) => void);

export class ErrorState {
	readonly errors: string[] = [];
	readonly lastError?: string;

	constructor(errors: string[]) {
		this.errors = errors;
		if (errors.length > 0) {
			this.lastError = errors[0];
		} else {
			this.lastError = undefined;
		}
	}

	public add(error: string): ErrorState {
		const newErrors = Array.from<string>(this.errors);
		newErrors.push(error);
		return new ErrorState(newErrors);
	}
	
	public static empty(): ErrorState {
		return new ErrorState([]);
	}
}

/**
 * Allows to centrally collect the errors
 */
export class ErrorHandlerHolder {
	errorHandler?: IErrorHandler;
}

export const defaultErrorHandler: ErrorHandlerHolder = new ErrorHandlerHolder();


