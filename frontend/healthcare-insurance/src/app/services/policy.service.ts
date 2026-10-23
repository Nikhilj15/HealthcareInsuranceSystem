import { Injectable } from '@angular/core';

import {
  HttpClient,
  HttpParams
} from '@angular/common/http';

import { Observable } from 'rxjs';

import { PurchasedPolicy } from '../models/purchased-policy';


export interface AdminPolicyFilters {

  search?: string;

  policyNumber?: string;

  customerName?: string;

  planName?: string;

  status?: string;

}


@Injectable({
  providedIn: 'root'
})
export class PolicyService {


  /*
   * USER API
   *
   * Used by:
   * - User Policy Details
   * - My Policies
   * - Payment / Purchase Policy
   */
  private userApiUrl =
    'https://insurecare-backend.onrender.com/api/policies';


  /*
   * ADMIN API
   *
   * Used only by Admin Policies page.
   *
   * Search and filters are handled here.
   */
  private adminApiUrl =
    'https://insurecare-backend.onrender.com/api/admin/policies';


  constructor(
    private http: HttpClient
  ) {}


  // ============================================================
  // USER SIDE
  // ============================================================


  /*
   * Get one policy by ID
   */
  getPolicyById(
    id: number
  ): Observable<PurchasedPolicy> {

    return this.http.get<PurchasedPolicy>(
      `${this.userApiUrl}/${id}`
    );

  }


  /*
   * Get policies belonging to logged-in user
   */
  getUserPolicies(
    userId: number
  ): Observable<PurchasedPolicy[]> {

    return this.http.get<PurchasedPolicy[]>(
      `${this.userApiUrl}/user/${userId}`
    );

  }


  /*
   * Purchase an insurance policy
   */
  purchasePolicy(
    planId: number
  ): Observable<PurchasedPolicy> {

    return this.http.post<PurchasedPolicy>(
      `${this.userApiUrl}/purchase`,
      {
        planId: planId
      }
    );

  }


  // ============================================================
  // ADMIN SIDE
  // ============================================================


  /*
   * Get all policies for Admin.
   *
   * Optional filters:
   *
   * search
   * policyNumber
   * customerName
   * planName
   * status
   */
  getAdminPolicies(
    filters?: AdminPolicyFilters
  ): Observable<PurchasedPolicy[]> {


    let params = new HttpParams();


    // Global search
    if (
      filters?.search &&
      filters.search.trim()
    ) {

      params = params.set(
        'search',
        filters.search.trim()
      );

    }


    // Policy number filter
    if (
      filters?.policyNumber &&
      filters.policyNumber.trim()
    ) {

      params = params.set(
        'policyNumber',
        filters.policyNumber.trim()
      );

    }


    // Customer name filter
    if (
      filters?.customerName &&
      filters.customerName.trim()
    ) {

      params = params.set(
        'customerName',
        filters.customerName.trim()
      );

    }


    // Plan name filter
    if (
      filters?.planName &&
      filters.planName.trim()
    ) {

      params = params.set(
        'planName',
        filters.planName.trim()
      );

    }


    // Status filter
    if (
      filters?.status &&
      filters.status.trim()
    ) {

      params = params.set(
        'status',
        filters.status.trim()
      );

    }


    return this.http.get<PurchasedPolicy[]>(
      this.adminApiUrl,
      {
        params: params
      }
    );

  }


  /*
   * Get one policy from Admin API
   */
  getAdminPolicyById(
    id: number
  ): Observable<PurchasedPolicy> {

    return this.http.get<PurchasedPolicy>(
      `${this.adminApiUrl}/${id}`
    );

  }


  /*
   * Approve policy
   */
  approvePolicy(
    id: number
  ): Observable<PurchasedPolicy> {

    return this.http.put<PurchasedPolicy>(
      `${this.adminApiUrl}/${id}/approve`,
      {}
    );

  }


  /*
   * Reject policy
   */
  rejectPolicy(
    id: number,
    reason: string
  ): Observable<PurchasedPolicy> {

    return this.http.put<PurchasedPolicy>(
      `${this.adminApiUrl}/${id}/reject`,
      {
        reason: reason
      }
    );

  }


  /*
   * BACKWARD COMPATIBILITY
   *
   * If any existing Admin component is still using
   * getAllPolicies(), this method will continue working.
   *
   * It intentionally uses ADMIN API.
   */
  getAllPolicies(): Observable<PurchasedPolicy[]> {

    return this.http.get<PurchasedPolicy[]>(
      this.adminApiUrl
    );

  }

}