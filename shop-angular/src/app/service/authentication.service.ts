import { Injectable } from '@angular/core';
import {
	HttpClient,
	HttpHeaders,
	HttpErrorResponse,
	HttpParams,
	HttpRequest,
	HttpHandler,
	HttpInterceptor,
	HttpEvent
} from '@angular/common/http';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { Router } from '@angular/router';
import { catchError, tap, map } from 'rxjs/operators';

import { MessageService, ErrorMessage } from './message.service';
import { User } from '../object/user';
import { Constant } from '../object/constant';
import { Util } from '../object/util';

const httpOptions = {
	loginHeaders: new HttpHeaders({
		'Content-Type': 'application/x-www-form-urlencoded'
	}),
	signupHeaders: new HttpHeaders({
		'Content-Type': 'application/json'
	})
};
@Injectable({
	providedIn: 'root'
})
export class AuthenticationService {

	authenticated = false;
	user: any = new User();

	public static JWT_TOKEN = 'token';

	// reference when logout happens
    logoutSubject: Subject<boolean> = new Subject<boolean>();

    logoutChange(val: boolean) {
        this.logoutSubject.next(val);
    }

    // reference for jwt token to start auto refresh timer
    loginSubject: Subject<boolean> = new Subject<boolean>();

    loginChange(val: boolean) {
        this.loginSubject.next(val);
    }

	constructor(
		private http: HttpClient,
		private router: Router,
		private messageService: MessageService
	) { }

	getUser(): Observable<any> {
		return this.http
			.get(Constant.endpoint + 'retrieveUser')
			.pipe(
				tap(data => {
					if (data) {
						this.authenticated = true;
						this.user = new User(data);
					} else {
						this.authenticated = false;
					}
				}),
				catchError(
					this.messageService.handleObservableError(
						'Unable to Authenticate information. Try again later'
					)
				)
			);
	}

	authenticate(credentials, callback) {
		const loginParams = new HttpParams()
			.set('username', credentials.username)
			.set('password', credentials.password);
		this.http
			.post(Constant.endpoint + 'loginUser', loginParams, {
				headers: httpOptions.loginHeaders,
				// responseType: 'text'
			})
			.pipe( map(Util.extractData) )
			.subscribe(
				data => {
					if (data && data['status'] === 'SUCCESS') {
						this.authenticated = true;
						if (data['details']) {
							this.user = new User(data['details']);
						} else {
							console.error('missing user details');
						}
						if (this.user.token) {
							localStorage.setItem(AuthenticationService.JWT_TOKEN, this.user.token);
						} else {
							console.error('missing token');
						}
						this.loginChange(true);
					} else {
						this.authenticated = false;
						localStorage.removeItem(AuthenticationService.JWT_TOKEN);
					}
					return callback && callback(data);
				},
				error => {
					this.authenticated = false;
					localStorage.removeItem(AuthenticationService.JWT_TOKEN);
					this.messageService.clear();
					this.messageService.handleError(
						error,
						'You are unable to login at this moment'
					);					
					return callback && callback(undefined);
				}
			);
	}

	signup(credentials, callback) {
		this.http
			.post(Constant.endpoint + 'signupUser', credentials, {
				headers: httpOptions.signupHeaders,
				// responseType: 'text'
			})
			.pipe( map(Util.extractData) )
			.subscribe(
				data => {
					return callback && callback(data);
				},
				error => {
					this.messageService.clear();
					this.messageService.handleError(
						error,
						'You are unable to signup at this moment'
					);
					return callback && callback(undefined);
				}
			);
	}

	logout() {
		localStorage.removeItem(AuthenticationService.JWT_TOKEN);
        this.authenticated = false;
		this.user = new User();
		this.messageService.clear();
		this.logoutChange(true);
		this.router.navigateByUrl('/shop');
	}

	/**
     * retrieves a new jwt token with extended session time
     * @param credentials
     */
	 public retrieveToken() {
        return this.http
        .post(Constant.endpoint + 'retrieveToken', null, { responseType: 'text' })
        .pipe(map((result: string) => {
            if (result) {
                localStorage.setItem(AuthenticationService.JWT_TOKEN, result);
            }
            return result;
        },
            (err) => {
            console.error(err);
            })
        );
    }

	/**
	 * will redirect to a page if a user is authenticated or not depending on the parameter
	 */
	redirectToHome(isAuthenticated: boolean, callback?) {
		this.redirect(isAuthenticated, '/shop', callback);
	}
	
	redirect(isAuthenticated: boolean, url: string, callback?) {
		if (this.authenticated === false) {
			//this.getUser().subscribe(data => {
				callback && callback();
				if (this.authenticated === isAuthenticated) {
					this.router.navigateByUrl(url);
				}
			//});
		} else {
			callback && callback();
			if (this.authenticated === isAuthenticated) {
				this.router.navigateByUrl(url);
			}
		}
	}

}
