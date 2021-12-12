import { Injectable } from '@angular/core';
import { MessageService } from './message.service';
import {
	HttpClient
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Constant } from '../object/constant';
import { Util } from '../object/util';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  	constructor(
		private http: HttpClient,
		private messageService: MessageService
	) { }

	getContent(): Observable<any> {
		return this.http.get(Constant.endpoint + 'accountResetList').pipe(
			map(Util.extractData),
			catchError(
				this.messageService.handleObservableError<{}>(
					'Unable to retrieve Item List'
				)
			)
		);
	}

	resetAccounts(accountList): Observable<any> {
		this.messageService.clear();

		if (accountList) {
			return this.http.post(Constant.endpoint + 'resetAccount', accountList).pipe(
				map(Util.extractData),
				catchError(
					this.messageService.handleObservableError<{}>(
						'Unable to reset account'
					)
				)
			);
		} else {
			this.messageService.clear();
			this.messageService.handleObservableError(
				'Unable to reset accounts: Invalid Content'
			);
		}
	}
	
}
