import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  // -----------------------------------------
  // Do NOT attach JWT to authentication APIs
  // -----------------------------------------

  const isAuthRequest =
    req.url.includes('/api/auth/login') ||
    req.url.includes('/api/auth/register');

  if (isAuthRequest) {
    return next(req);
  }

  // -----------------------------------------
  // Get JWT
  // -----------------------------------------

  const token =
    localStorage.getItem('healthcare_token');

  // -----------------------------------------
  // Attach JWT when available
  // -----------------------------------------

  if (token) {

    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};