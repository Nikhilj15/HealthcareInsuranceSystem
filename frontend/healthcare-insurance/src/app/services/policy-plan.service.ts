import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { PolicyPlan } from '../models/policy-plan';

export interface PolicyPlanRequest {
  planCode: string;
  name: string;
  description: string;
  premium: number;
  coverageAmount: number;
  durationYears: number;
  benefits: string[];
  exclusions: string[];
  status: 'Active' | 'Inactive';
}

@Injectable({
  providedIn: 'root'
})
export class PolicyPlanService {

  private readonly apiUrl = 'https://insurecare-backend.onrender.com/api/policy-plans';

  constructor(
    private http: HttpClient
  ) {}

  getAllPlans(): Observable<PolicyPlan[]> {
    return this.http.get<PolicyPlan[]>(
      this.apiUrl
    );
  }

  getActivePlans(): Observable<PolicyPlan[]> {
    return this.http.get<PolicyPlan[]>(
      `${this.apiUrl}/active`
    );
  }

  getPlanById(id: number): Observable<PolicyPlan> {
    return this.http.get<PolicyPlan>(
      `${this.apiUrl}/${id}`
    );
  }

  createPlan(
    request: PolicyPlanRequest
  ): Observable<PolicyPlan> {
    return this.http.post<PolicyPlan>(
      this.apiUrl,
      request
    );
  }

  updatePlan(
    id: number,
    request: PolicyPlanRequest
  ): Observable<PolicyPlan> {
    return this.http.put<PolicyPlan>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  deletePlan(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}
