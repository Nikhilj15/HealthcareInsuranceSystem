import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export type LoginType = 'USER' | 'ADMIN';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  expiresIn: number;
  role: LoginType;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  phone: string;
  password: string;
  dateOfBirth: string;
  gender: string;
  address: string;
}

export interface ResetPasswordRequest {
  identifier: string;
  newPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl = '/api/auth';
  private readonly TOKEN_KEY = 'healthcare_token';

  login(
    request: LoginRequest,
    loginType: LoginType
  ): Observable<LoginResponse> {

    const payload = {
      ...request,
      loginType
    };

    return this.http
      .post<LoginResponse>(
        `${this.apiUrl}/login`,
        payload
      )
      .pipe(
        tap(response => {

          // Extra frontend validation.
          // The backend must also enforce this check.
          if (
            !response.token ||
            response.role !== loginType
          ) {
            throw new Error(
              'Login failed. Please use the correct login page.'
            );
          }

          localStorage.setItem(
            this.TOKEN_KEY,
            response.token
          );
        })
      );
  }

  register(
    request: RegisterRequest
  ): Observable<string> {

    return this.http.post(
      `${this.apiUrl}/register`,
      request,
      {
        responseType: 'text'
      }
    );
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }
}