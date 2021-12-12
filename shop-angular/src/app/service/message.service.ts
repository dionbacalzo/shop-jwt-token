import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpErrorResponse, HttpEvent } from '@angular/common/http';
import { Observable, of } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class MessageService {
	messageSubject: Subject<any> = new Subject<any>();
	messages: Message<{}>[] = [];

	add(message: any) {
		if (message instanceof Message) {
			this.messages.push(message);
		} else {
			this.messages.push(new Message(message));
		}
		this.messageSubject.next(new Message(message));
	}

	clear() {
		if (this.messages && this.messages.length) {
			let index: number = this.messages.length - 1;
			while (index >= 0) {
				let currentPersistTime = this.messages[index].getPersist();
				if (this.messages[index].getPersist() === 0) {
					this.messages.splice(index, 1);
				} else if (currentPersistTime > 0) {
					currentPersistTime -= 1;
					this.messages[index].setPersist(currentPersistTime);
				} else {
					this.messages[index].setPersist(0);
					this.messages.splice(index, 1);
				}
				index -= 1;
			}
		}
		this.messageSubject.next(this.messages);
	}

	handleObservableError<T>(message?: any, result?: T) {
		return (error: HttpErrorResponse): Observable<T> => {
			this.handleError(error, message);

			// Let the app keep running by returning an empty result.
			return of(result as T);
		};
	}

	handleError(error: HttpErrorResponse, message?: any) {
		if (message instanceof Message) {
			this.add(message);
		} else if (!message) {
			const errorMessage = new ErrorMessage({
				errorInfo: error,
				type: 'error',
				messageDisplay: 'Error has occured'
			});
			this.add(errorMessage);
		} else {
			const errorMessage = new ErrorMessage({
				errorInfo: error,
				type: 'error',
				messageDisplay: message
			});
			this.add(errorMessage);
		}
	}
}

export class Message<T> {
	private type: string;
	private messageDisplay: string;
	/**
	 *	persist will display the number of times per clear until it reaches 0
	 */
	private persist: number;

	constructor(
		options: {
			type: string;
			messageDisplay: string;
			persist?: number;
		} = { type: '', messageDisplay: '' }
	) {
		this.type = options.type;
		this.messageDisplay = options.messageDisplay;
		if (!options.persist) {
			this.persist = 0;
		} else {
			this.persist = options.persist;
		}
	}

	setType(type: string) {
		this.type = type;
	}

	getType() {
		return this.type;
	}

	setMessageDisplay(messageDisplay: string) {
		this.messageDisplay = messageDisplay;
	}

	getMessageDisplay() {
		return this.messageDisplay;
	}

	setPersist(persist: number) {
		this.persist = persist;
	}

	getPersist() {
		if (!this.persist) {
			this.persist = 0;
		}
		return this.persist;
	}
}

export class ErrorMessage<T> extends Message<T> {
	errorInfo: HttpErrorResponse;

	constructor(
		options: {
			errorInfo?: HttpErrorResponse;
			type?: string;
			messageDisplay: string;
			persist?: number;
		} = { messageDisplay: '' }
	) {
		if (!options.type) {
			options.type = 'error';
		}
		const errorMessage = {
			errorInfo: options.errorInfo,
			type: 'error',
			messageDisplay: options.messageDisplay,
			persist: options.persist
		};

		super(errorMessage);
		this.errorInfo = options.errorInfo;
	}
}
