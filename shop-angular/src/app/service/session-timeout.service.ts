import { Injectable } from '@angular/core';
import { Subscription, Observable, Subject, timer } from 'rxjs';
import { takeWhile, map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class SessionTimeoutService {

    private isCounting = false;
    private timerSubscription: Subscription;
    private _countdown = new Subject<number>();

    private sessionWarningSubscription: Subscription;
    private _sessionWarningTimer = new Subject();
    private _isSessionExpired: boolean

    public currentStorageToken: string;
  
    public isSessionExpired(): boolean {
        return this._isSessionExpired;
    }

    public countdown(): Observable<number> {
        return this._countdown.asObservable();
    }

    public sessionWarningTimer(): Observable<any> {
        return this._sessionWarningTimer.asObservable();
    }

    public startTimer() {
        // Ensure that only one timer is in progress at any given time.
        if (!this.isCounting) {
            this._isSessionExpired = false;

            let seconds = this.getRemainingTime();
            if (this.timerSubscription) {
                this.timerSubscription.unsubscribe();
            }

            if (seconds) {
                this.isCounting = true;
                this.timerSubscription = timer(0, 1000).pipe(
                    takeWhile(t => t < seconds),
                    map(t => seconds - t)
                ).subscribe({
                    next: (t) => {this._countdown.next(t)},
                    error: () =>console.error('timerSubscription error'),
                    complete:() => {
                        this._countdown.complete();
                        this._isSessionExpired = true;

                        this.isCounting = false;
                        // Reset the countdown Subject so that a 
                        // countdown can be performed more than once.
                        this._countdown = new Subject<number>();
                    }
                });
                // set timeout for when dialog box appears
                this.setSessionTimeout(seconds);
            }
        }
    }

    public stopTimer() {
        if (this.timerSubscription) {
            this.timerSubscription.unsubscribe();
        }
        if (this.sessionWarningSubscription) {
            this.sessionWarningSubscription.unsubscribe();
        }
        this.isCounting = false;
        if (this._countdown) {
            this._countdown.complete();
        }
        // Reset the countdown Subject so that a countdown can be performed more than once.
        this._countdown = new Subject<number>();

        if (this._sessionWarningTimer) {
            this._sessionWarningTimer.complete();
        }
        // Reset the countdown Subject so that a countdown can be performed more than once.
        this._sessionWarningTimer = new Subject<number>();
    }

    public getRemainingTime(): number {
        let remainingTime = 0;
        if (localStorage.getItem(this.currentStorageToken)) {
            let base64Url = localStorage.getItem(this.currentStorageToken).split('.')[1];
            let base64 = base64Url.replace('-', '+').replace('_', '/');
            let parsedJson = JSON.parse(window.atob(base64));
            if (parsedJson && parsedJson.expiryDate) {
                let expdate = new Date(parsedJson.expiryDate);
                let rightNow = new Date();
                if (expdate.getTime() > rightNow.getTime()) {
                    remainingTime = (expdate.getTime() - rightNow.getTime()) / 1000;
                }
            }
        }
        return remainingTime
    }

    private getCountdownTime(): number {

        let remainingTime = 0;
        if (localStorage.getItem(this.currentStorageToken)) {
            let base64Url = localStorage.getItem(this.currentStorageToken).split('.')[1];
            let base64 = base64Url.replace('-', '+').replace('_', '/');
            let parsedJson = JSON.parse(window.atob(base64));
            if (parsedJson && parsedJson.countdownDate) {
                let countdownDate = new Date(parsedJson.countdownDate);
                let rightNow = new Date();
                if (countdownDate.getTime() > rightNow.getTime()) {
                    remainingTime = (countdownDate.getTime() - rightNow.getTime()) / 1000;
                }
            }
        }
        return remainingTime
    }

    setSessionTimeout(timeoutSeconds: number) {        
        if (this.sessionWarningSubscription) {
            this.sessionWarningSubscription.unsubscribe();
        }
        let showWarningSeconds = this.getCountdownTime();
        this.sessionWarningSubscription = timer(showWarningSeconds * 1000)
            .subscribe(
                t => {
                    this._sessionWarningTimer.next(t);                    
                }
            );
    }

}