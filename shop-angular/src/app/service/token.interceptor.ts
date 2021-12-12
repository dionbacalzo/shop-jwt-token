import { Injectable } from '@angular/core';
import {
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AuthenticationService } from './authentication.service';
@Injectable()
export class TokenInterceptor implements HttpInterceptor {

    urlsToNotUse: Array<string>;

    /**
     *
     * @param auth calls containing the following paths will include no authorization header using the jwt token
     */
    constructor(
        private router: Router
        ) {
        this.urlsToNotUse = [
            
        ];
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (this.isValidRequestForInterceptor(request.url)) {   
            let token = null;
            token = localStorage.getItem(AuthenticationService.JWT_TOKEN);
            //console.log('url ' + request.url)
            //console.log("Token intercept: ", token);
            if (token) {
                let base64Url = token.split('.')[1];
                let base64 = base64Url.replace('-', '+').replace('_', '/');
                let parsedJson = JSON.parse(window.atob(base64));
                //console.log(parsedJson)
                //console.log(new Date(parsedJson.iat))
                //console.log(new Date(parsedJson.exp))
                //console.log('Token Issued '+new Date(parsedJson.issuedDate))
                //console.log('Token Expiration '+new Date(parsedJson.expiryDate).toString())
                //console.log(new Date(parsedJson.countdownDate))

                let modifiedRequest = request.clone({
                    setHeaders: {
                    Authorization: `Bearer ${token}`
                    }
                });
            
                return next.handle(modifiedRequest);
            } else {
                console.log('No user JWT token for url ' + request.url);
            }
        }
        return next.handle(request);
    }

    private isValidRequestForInterceptor(requestUrl: string): boolean {
        for (let address of this.urlsToNotUse) {
            if (new RegExp(address).test(requestUrl)) {
                return false;
            }
        }
        return true;
    }
}
