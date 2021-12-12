import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivateChild } from '@angular/router';
import { Observable, of } from 'rxjs';
import { AuthenticationService } from './service/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate, CanActivateChild {

  constructor(private authService: AuthenticationService) { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.tryLogin()
  }

  canActivateChild(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.canActivate(route, state);
  }

  tryLogin(): Observable<boolean> | Promise<boolean> | boolean {
    // check if a user is logged in
    if (!this.authService.authenticated) {
      //retrieve as Promise<boolean> after the subscription
      return new Promise(resolve => {
        this.authService.getUser().subscribe(() => {          
          resolve(true);
        }, error => {
          resolve(true)
        })
      });
    } else {
      return true;
    }
  }
}
