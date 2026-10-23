import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ClaimResponse {
  id: number;
  claimNumber: string;
  policyId: number;
  policyNumber: string;
  userId: number;
  userName: string;
  userEmail: string;
  claimAmount: number;
  reason: string;
  description: string;
  claimDate: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  approvedAt?: string;
  rejectedAt?: string;
  rejectionReason?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClaimService {

  private apiUrl =
    'https://insurecare-backend.onrender.com/api/claims';

  constructor(
    private http: HttpClient
  ) {}

  // =========================
  // USER
  // =========================

  submitClaim(
    policyId: number,
    claimAmount: number,
    reason: string,
    description: string
  ): Observable<ClaimResponse> {

    return this.http.post<ClaimResponse>(
      `${this.apiUrl}/submit`,
      {
        policyId: policyId,
        claimAmount: claimAmount,
        reason: reason,
        description: description
      }
    );
  }

  getMyClaims(): Observable<ClaimResponse[]> {

    return this.http.get<ClaimResponse[]>(
      `${this.apiUrl}/my`
    );
  }

  getMyClaimById(
    id: number
  ): Observable<ClaimResponse> {

    return this.http.get<ClaimResponse>(
      `${this.apiUrl}/${id}`
    );
  }

  // =========================
  // ADMIN
  // =========================

  getAllClaims(): Observable<ClaimResponse[]> {

    return this.http.get<ClaimResponse[]>(
      `${this.apiUrl}/admin/all`
    );
  }

  approveClaim(
    id: number
  ): Observable<ClaimResponse> {

    return this.http.put<ClaimResponse>(
      `${this.apiUrl}/${id}/approve`,
      {}
    );
  }

  rejectClaim(
    id: number,
    reason: string
  ): Observable<ClaimResponse> {

    return this.http.put<ClaimResponse>(
      `${this.apiUrl}/${id}/reject`,
      {
        reason: reason
      }
    );
  }
}