import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../service/authentication.service';
import { Router } from '@angular/router';

import { MessageService, Message, ErrorMessage } from '../../service/message.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Subscription, Subject, Observable, timer } from 'rxjs';
import { SessionTimeoutService } from 'src/app/service/session-timeout.service';
import { takeWhile, map } from 'rxjs/operators';
import { UserIdleService } from 'angular-user-idle';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
	credentials = { username: '', password: '' };
	loginForm: FormGroup;
	hideLoginForm: boolean;
	errorLoginForm: boolean;

	constructor(
		private authService: AuthenticationService,
		private router: Router,
		private messageService: MessageService,
		private sessionTimeoutService: SessionTimeoutService,
    	private userIdle: UserIdleService
	) { }

	ngOnInit() {
		this.loginForm = new FormGroup({
			'username': new FormControl(this.credentials.username, Validators.required),
			'password': new FormControl(this.credentials.password, Validators.required)
		});
		this.hideLoginForm = false;
		this.errorLoginForm = false;
		this.initLoginSettings();
		// no need to redirect now that login component is at homepage
		// this.authService.redirectToHome(true);
		// this.messageService.clear();
	}

	get username() { return this.loginForm.get('username'); }

	get password() { return this.loginForm.get('password'); }

	login() {
		this.hideLoginForm = true;
		this.errorLoginForm = false;
		this.credentials = this.loginForm.value;
		this.messageService.clear();
		this.authService.authenticate(this.credentials, data => {			
			if (data) {				
				if (data.status === 'FAIL') {
					this.messageService.add(new ErrorMessage({
						messageDisplay: data.message
					}));
				} else {
					this.messageService.add(
						new Message({
							type: 'info',
							messageDisplay: data.message,
							persist: 0
						})
					);
					this.router.navigateByUrl('/shop');
				}				
			} else {
				this.errorLoginForm = true;
			}
			this.hideLoginForm = false;
		});
	}
	
	private loginSubscription: Subscription;
	private logoutSubscription: Subscription;
	private refreshTokenSubscription: Subscription;  
	private monitorSessSubscription: Subscription;
	private initRefreshTokenSubscription: Subscription;
	private _refreshTokenTimer = new Subject();
	private isUserActive = true;

	public refreshTokenTimer(): Observable<any> {
		return this._refreshTokenTimer.asObservable();
	}

	initLoginSettings(){
		this.loginSubscription = this.authService.loginSubject.subscribe((start: boolean) => {
			if (start) {
				console.log('Renew authentication subscription has started')
				// initialize local storage token to use for renew
				this.sessionTimeoutService.currentStorageToken = AuthenticationService.JWT_TOKEN;	  
				this.sessionTimeoutService.startTimer();	  
				this.monitorRemainingTime();
				// set session auto renew token
				this.initRefreshToken(this.sessionTimeoutService.getRemainingTime());
				this.refreshTokenSubscription = this.refreshTokenTimer()
				.subscribe((t) => {
					this.staySignIn();
				});
			}
		});
		this.logoutSubscription = this.authService.logoutSubject.subscribe((start: boolean) => {
			if (start) {				
				this.sessionTimeoutService.stopTimer();
				this.stopIdleWatching();
				if (this.monitorSessSubscription) {
					this.monitorSessSubscription.unsubscribe();
				}
				if (this.loginSubscription) {
					this.loginSubscription.unsubscribe();
				}
				if (this.logoutSubscription) {
					this.logoutSubscription.unsubscribe();
				}
				console.log('Renew authentication subscription has ended')
			}
		});
	}

	monitorRemainingTime() {
		this.monitorSessSubscription = timer(0, 60000).pipe(
		  takeWhile(t => t < this.sessionTimeoutService.getRemainingTime()),
		  map(t => this.sessionTimeoutService.getRemainingTime() - t)
		  ).subscribe(
			  t => {
				  console.log(Math.trunc(t/60) + ' minutes for session time remaining')
			  },
			  null
		  );
	}

	/**
	 * Initialize the auto renew of token function
	 * @param expiredSeconds time in seconds before token expires
	 */
	initRefreshToken(expiredSeconds: number) {

		if (this.refreshTokenSubscription) {
			this.refreshTokenSubscription.unsubscribe();
		}
		let refreshSeconds = 0;

		// trigger auto renew 3 minutes before expiration
		if (expiredSeconds - (3 * 60) > 0) {
			refreshSeconds = expiredSeconds - (3 * 60);
			console.log(Math.trunc(refreshSeconds / 60) + ' minutes remaining to refresh token ')
			this.refreshTokenSubscription = timer(refreshSeconds * 1000)
				.subscribe(
					t => {
						// trigger only if the user has been active
						if (this.isUserActive) {
							this._refreshTokenTimer.next(t);
						} else {
							console.log('user is not active, will not renew token')
						}
					}
				);
			this.initMonitorActiveUser(expiredSeconds)
		}
	}

	/**
	 * refresh token if the user is still active for 10 minutes
	 * @param expiredSeconds the seconds till token expiration
	 */
	initMonitorActiveUser(expiredSeconds: number) {
		this.isUserActive = true;
		let idleSeconds = 0;
		// check if expiration time is more than 10 minutes before monitoring if user is active
		if (expiredSeconds > (10 * 60)) {
			idleSeconds = (10 * 60);
		}
		if (idleSeconds > 0) {
			this.initializeTimeout(idleSeconds, 3);
		}
	}

	/**
	 * intialize monitor use activity
	 * @param idle the number of seconds the user can be inactive
	 * @param timeout the number of seconds before onTimeout() is triggered after user is inactive
	 */
	initializeTimeout(idle: number, timeout: number) {
		this.userIdle.stopWatching();
		this.userIdle.setConfigValues({ idle: idle, timeout: timeout });
		//Start watching for user inactivity.
		this.userIdle.startWatching();
		// Start watching when user idle is starting.
		// this.userIdle.onTimerStart().subscribe(count => console.log(count));
		// Start watch when time is up.
		this.userIdle.onTimeout().subscribe(() => {
			this.isUserActive = false;
			// check if there is still more than 10 minutes before expiration
			// if yes then reset the timer to give a chance for user to be active
			if (this.sessionTimeoutService.getRemainingTime() > (10 * 60)) {
				this.userIdle.resetTimer();
				this.isUserActive = true;
				console.log('more than 10 minutes before token expiration, will give chance for user to be active');
			} else {
				console.log('user is inactive detected');
			}
		});
	}

	/**
	 * stop all functions related to session auto refresh and user activity monitoring
	 */
	stopIdleWatching() {
		// reset the reference that checks if the user has been active
		this.isUserActive = true;

		if (this.initRefreshTokenSubscription) {
			this.initRefreshTokenSubscription.unsubscribe();
		}
		if (this._refreshTokenTimer) {
			this._refreshTokenTimer.complete();
		}
		// Reset the countdown Subject so that a countdown can be performed more than once.
		this._refreshTokenTimer = new Subject<number>();
	}

	/**
	 * Retrieves a new JWt token with extended expiration date
	 */
	staySignIn() {
		console.log('Now Auto renewing the auth token')
		this.sessionTimeoutService.stopTimer();
		this.stopIdleWatching();
		this.authService.retrieveToken().subscribe((result: any) => {
			// stop session timeout timer
			this.sessionTimeoutService.stopTimer();
			this.sessionTimeoutService.startTimer();
			this.stopIdleWatching();
			this.initRefreshToken(this.sessionTimeoutService.getRemainingTime());

			// reset the refresh token subscription
			this.refreshTokenSubscription = this.refreshTokenTimer()
				.subscribe((t) => {
					this.staySignIn()
				});
		}, (err) => {
			console.error(err)
		});
	}
	
}
