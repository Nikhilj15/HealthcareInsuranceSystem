import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';


export interface Customer {

  id: number;

  fullName: string;

  email: string;

  phone: string;

  dateOfBirth?: string;

  gender?: string;

  address?: string;

  status?: string;

  createdAt?: string;
}


export interface MedicalHistory {

  id: number;

  customerId: number;

  condition: string;

  diagnosisDate?: string;

  treatment?: string;

  doctorName?: string;

  hospitalName?: string;

  status?: string;

  notes?: string;
}


export interface MedicalHistoryRequest {

  condition: string;

  diagnosisDate?: string;

  treatment?: string;

  doctorName?: string;

  hospitalName?: string;

  status?: string;

  notes?: string;
}


@Injectable({
  providedIn: 'root'
})
export class CustomerService {


  private apiUrl =
    'https://insurecare-backend.onrender.com/api/admin/customers';


  private medicalHistoryUrl =
    'https://insurecare-backend.onrender.com/api/admin';


  constructor(
    private http: HttpClient
  ) {}


  // =========================================================
  // GET ALL CUSTOMERS
  // =========================================================

  getAllCustomers(): Observable<Customer[]> {

    return this.http.get<Customer[]>(
      this.apiUrl
    );
  }


  // =========================================================
  // GET CUSTOMER BY ID
  // =========================================================

  getCustomerById(
    id: number
  ): Observable<Customer> {

    return this.http.get<Customer>(
      `${this.apiUrl}/${id}`
    );
  }


  // =========================================================
  // UPDATE CUSTOMER STATUS
  // =========================================================

  updateCustomerStatus(

    id: number,

    status: 'ACTIVE' | 'INACTIVE'

  ): Observable<Customer> {

    return this.http.put<Customer>(

      `${this.apiUrl}/${id}/status`,

      {
        status: status
      }

    );
  }


  // =========================================================
  // GET MEDICAL HISTORY
  // =========================================================

  getMedicalHistory(
    customerId: number
  ): Observable<MedicalHistory[]> {

    return this.http.get<MedicalHistory[]>(

      `${this.medicalHistoryUrl}/customers/${customerId}/medical-history`

    );
  }


  // =========================================================
  // ADD MEDICAL HISTORY
  // =========================================================

  addMedicalHistory(

    customerId: number,

    data: MedicalHistoryRequest

  ): Observable<MedicalHistory> {

    return this.http.post<MedicalHistory>(

      `${this.medicalHistoryUrl}/customers/${customerId}/medical-history`,

      data

    );
  }


  // =========================================================
  // UPDATE MEDICAL HISTORY
  // =========================================================

  updateMedicalHistory(

    id: number,

    data: MedicalHistoryRequest

  ): Observable<MedicalHistory> {

    return this.http.put<MedicalHistory>(

      `${this.medicalHistoryUrl}/medical-history/${id}`,

      data

    );
  }


  // =========================================================
  // DELETE MEDICAL HISTORY
  // =========================================================

  deleteMedicalHistory(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(

      `${this.medicalHistoryUrl}/medical-history/${id}`

    );
  }

}