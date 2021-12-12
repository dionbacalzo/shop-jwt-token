import { Injectable } from '@angular/core';
import {
	HttpClient} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { MessageService } from './message.service';
import { Constant } from '../object/constant';
import { Util } from '../object/util';

@Injectable({
	providedIn: 'root'
})
export class ShopRestService {
	data: any;

	constructor(
		private http: HttpClient,
		private messageService: MessageService
	) { }

	getContent(): Observable<any> {
		return this.http.get(Constant.endpoint + 'viewListUnparsed').pipe(
			map(Util.extractData),
			catchError(
				this.messageService.handleObservableError<{}>(
					'Unable to retrieve Item List'
				)
			)
		);
	}

	saveContent(productList: {}): Observable<any> {
		this.messageService.clear();

		if (productList) {
			return this.http.post(Constant.endpoint + 'save', productList).pipe(
				map(Util.extractData),
				catchError(
					this.messageService.handleObservableError<{}>(
						'Unable to Save Item List: Invalid Content'
					)
				)
			);
		} else {
			this.messageService.clear();
			this.messageService.handleObservableError(
				'Unable to Save Item List: Invalid Content'
			);
		}
	}
}
