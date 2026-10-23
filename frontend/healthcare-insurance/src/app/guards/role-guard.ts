import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router
} from '@angular/router';

export const roleGuard: CanActivateFn = (route) => {

  const router = inject(Router);

  const token =
    localStorage.getItem('healthcare_token');

  if (!token) {
    return router.createUrlTree(['/login']);
  }

  try {

    const parts = token.split('.');

    if (parts.length !== 3) {
      throw new Error('Invalid token format');
    }

    // JWT payload uses Base64URL encoding.
    const base64Payload = parts[1]
      .replace(/-/g, '+')
      .replace(/_/g, '/');

    const paddedPayload = base64Payload.padEnd(
      Math.ceil(base64Payload.length / 4) * 4,
      '='
    );

    const payload = JSON.parse(
      atob(paddedPayload)
    );

    // Reject expired tokens.
    if (
      typeof payload.exp !== 'number' ||
      payload.exp * 1000 <= Date.now()
    ) {
      throw new Error('Token expired');
    }

    const userRole = payload.role;
    const requiredRole = route.data['role'];

    if (
      userRole === requiredRole &&
      (userRole === 'USER' || userRole === 'ADMIN')
    ) {
      return true;
    }

    // Redirect to the dashboard matching the token role.
    if (userRole === 'ADMIN') {
      return router.createUrlTree(['/admin/dashboard']);
    }

    if (userRole === 'USER') {
      return router.createUrlTree(['/user/dashboard']);
    }

    throw new Error('Invalid role in token');

  } catch {

    localStorage.removeItem('healthcare_token');

    return router.createUrlTree(['/login']);
  }
};