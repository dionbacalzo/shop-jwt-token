import { Observable } from 'rxjs';
import { retryWhen } from 'rxjs/operators';
import { Injectable, ClassProvider } from '@angular/core';
import { HttpRequest, HttpHandler, HttpInterceptor, HTTP_INTERCEPTORS, HttpEvent } from '@angular/common/http';
import { genericRetryStrategy, ShouldRetryFn } from '../object/rxjs-utils';

@Injectable()
export class RetryInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const { shouldRetry } = this;
    return next.handle(request)
      .pipe(retryWhen(genericRetryStrategy({
        shouldRetry
      })));
  }

  private shouldRetry: ShouldRetryFn = (error) => error.status === 0;
}

export const RETRY_PROVIDER: ClassProvider = {
  provide: HTTP_INTERCEPTORS,
  useClass: RetryInterceptor,
  multi: true
}