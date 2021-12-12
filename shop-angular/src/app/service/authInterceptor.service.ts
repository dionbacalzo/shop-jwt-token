import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpRequest, HttpHandler, HttpInterceptor, HttpEvent } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Router } from '@angular/router';
import { AuthenticationService } from './authentication.service';
import { catchError } from 'rxjs/operators';
import { User } from '../object/user';
import { SessionTimeoutService } from './session-timeout.service';
import { ErrorMessage, MessageService } from './message.service';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationInterceptor implements HttpInterceptor {

  constructor(private router: Router,
    private authService: AuthenticationService,
    private sessionTimeoutService: SessionTimeoutService,
		private messageService: MessageService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    const request = req.clone({
      withCredentials: true,
      headers: req.headers.set('ngsw-bypass', 'true')
    });

    const uniqId = `${new Date().getTime()}`;
    const startTime = new Date();    

    return next.handle(request).pipe(
      
      catchError((error: HttpErrorResponse, caught: Observable<HttpEvent<any>>): Observable<HttpEvent<any>> => {        
        // catch authentication errors
        if (error.status === 401 || error.status === 403) {
          console.error(error);
          this.sessionTimeoutService.stopTimer();       
          this.authService.logout();
          this.authService.authenticated = false;          
          localStorage.removeItem(AuthenticationService.JWT_TOKEN);
          this.authService.user = new User();

          this.messageService.clear();
          this.messageService.add(
            new ErrorMessage({
              messageDisplay: 'Authorized account required'
            })
          );

		      this.router.navigateByUrl('/shop');
          // immediately stop further processes and display error message
          return of(undefined as HttpEvent<any>);

        } else {
          if (error && error.status === 0 && error.error instanceof ProgressEvent) {
            // A client-side or network error occurred.
            // Both "Connection time out" and "No internet connection" should return a status of 0, 
            // according to the spec https://www.w3.org/TR/XMLHttpRequest1/#the-status-attribute
            console.log('Client side Internet issue: ', error.error)
          }
          throw error;
        }
      })
    );
  }

}