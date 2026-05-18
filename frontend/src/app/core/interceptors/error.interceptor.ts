import { HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error) => {
      if ([401, 403].includes(error.status)) {
        // Auto logout if 401 or 403 response returned from api
        // authService.logout();
      }

      const errorMessage = error.error?.message || error.statusText;
      console.error('HTTP Error:', errorMessage);
      return throwError(() => new Error(errorMessage));
    })
  );
};
