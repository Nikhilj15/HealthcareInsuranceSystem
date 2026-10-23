export interface MedicalHistory {
  id: number;
  condition: string;
  diagnosisDate: string;
  treatment: string;
  doctorName: string;
  hospitalName: string;
  status: 'Ongoing' | 'Recovered' | 'Chronic';
  notes?: string;
}

export interface Customer {
  id: number;
  name: string;
  age: number;
  email: string;
  phone: string;
  address: string;

  gender?: string;
  dateOfBirth?: string;

  medicalHistory: MedicalHistory[];

  createdAt?: string;
}