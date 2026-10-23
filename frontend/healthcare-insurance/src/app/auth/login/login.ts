import {
  ChangeDetectorRef,
  Component
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

import {
  AuthService,
  LoginType
} from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  loginType: 'user' | 'admin' = 'user';

  email = '';
  password = '';

  isSubmitting = false;
  errorMessage = '';
  showErrorMessage = false;

  resetIdentifier = '';
  resetPasswordValue = '';
  resetPasswordConfirmation = '';

  showResetPassword = false;
  resetMessage = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  // LOGIN

  login(): void {

    this.clearLoginError();

    const trimmedEmail = this.email.trim();

    if (!trimmedEmail && !this.password) {
      this.showError(
        'Please enter your email and password.'
      );
      return;
    }

    if (!trimmedEmail) {
      this.showError('Please enter your email.');
      return;
    }

    if (!this.password) {
      this.showError('Please enter your password.');
      return;
    }

    const emailPattern =
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(trimmedEmail)) {
      this.showError(
        'Please enter a valid email address.'
      );
      return;
    }

    if (this.password.length < 6) {
      this.showError(
        'Password must contain at least 6 characters.'
      );
      return;
    }

    if (this.password.length > 100) {
      this.showError(
        'Password cannot exceed 100 characters.'
      );
      return;
    }

    if (this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    this.clearLoginError();

    // Remove any previous session so a failed login
    // cannot leave an older token active in this browser.
    this.authService.logout();

    const expectedRole: LoginType =
      this.loginType === 'admin' ? 'ADMIN' : 'USER';

    this.authService.login(
      {
        email: trimmedEmail,
        password: this.password
      },
      expectedRole
    ).subscribe({

      next: (response) => {

        this.isSubmitting = false;

        // Defensive check before navigation.
        if (response.role !== expectedRole) {
          this.authService.logout();

          this.showError(
            'This account cannot use the selected login page.'
          );
          return;
        }

        this.clearLoginError();

        if (response.role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else {
          this.router.navigate(['/user/dashboard']);
        }

        this.cdr.detectChanges();
      },

      error: (error: unknown) => {

        this.isSubmitting = false;

        // Never keep a token after a failed login.
        this.authService.logout();

        if (error instanceof HttpErrorResponse) {

          // Prefer the backend's specific error message.
          const backendMessage =
            typeof error.error?.message === 'string'
              ? error.error.message.trim()
              : typeof error.error === 'string'
                ? error.error.trim()
                : '';

          if (backendMessage) {
            this.showError(backendMessage);
            return;
          }

          if (error.status === 401) {
            this.showError(
              'Invalid email or password.'
            );
            return;
          }

          if (error.status === 403) {
            this.showError(
              'Your account cannot use this login page, or your account is inactive.'
            );
            return;
          }

          if (error.status === 400) {
            this.showError(
              'Invalid login request. Please check your details.'
            );
            return;
          }

          if (error.status === 0) {
            this.showError(
              'Unable to connect to server. Please make sure the backend is running.'
            );
            return;
          }
        }

        this.showError(
          error instanceof Error
            ? error.message
            : 'Unable to login. Please try again.'
        );
      }
    });
  }

  // SHOW ERROR

  private showError(message: string): void {
    this.errorMessage = message;
    this.showErrorMessage = true;
    this.isSubmitting = false;

    this.cdr.detectChanges();
  }

  // CLEAR ERROR

  clearLoginError(): void {
    this.errorMessage = '';
    this.showErrorMessage = false;

    this.cdr.detectChanges();
  }

  // FORGOT PASSWORD

  forgotPassword(): void {
    this.resetIdentifier = this.email.trim();
    this.resetMessage = '';
    this.resetPasswordValue = '';
    this.resetPasswordConfirmation = '';
    this.showResetPassword = true;
  }

  // CLOSE RESET PASSWORD

  closeResetPassword(): void {
    this.showResetPassword = false;
    this.resetMessage = '';
    this.resetPasswordValue = '';
    this.resetPasswordConfirmation = '';
  }

  // REQUEST PASSWORD RESET
  // The reset API is not implemented yet.

  requestReset(): void {

    this.resetMessage = '';

    const identifier = this.resetIdentifier.trim();

    if (!identifier) {
      this.resetMessage =
        'Please enter your registered email.';
      return;
    }

    const emailPattern =
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(identifier)) {
      this.resetMessage =
        'Please enter a valid email address.';
      return;
    }

    this.resetMessage =
      'Password reset service is currently unavailable. Please contact the administrator.';
  }

  // RESET PASSWORD
  // The reset API is not implemented yet.

  resetPassword(): void {

    this.resetMessage = '';

    if (!this.resetPasswordValue) {
      this.resetMessage =
        'Please enter a new password.';
      return;
    }

    if (this.resetPasswordValue.length < 8) {
      this.resetMessage =
        'Password must contain at least 8 characters.';
      return;
    }

    if (this.resetPasswordValue.length > 100) {
      this.resetMessage =
        'Password cannot exceed 100 characters.';
      return;
    }

    if (!this.resetPasswordConfirmation) {
      this.resetMessage =
        'Please confirm your new password.';
      return;
    }

    if (
      this.resetPasswordValue !==
      this.resetPasswordConfirmation
    ) {
      this.resetMessage =
        'Passwords do not match.';
      return;
    }

    this.resetMessage =
      'Password reset service is currently unavailable. Please contact the administrator.';
  }
}