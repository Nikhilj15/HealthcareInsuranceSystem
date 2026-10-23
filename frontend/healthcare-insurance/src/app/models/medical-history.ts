export interface MedicalHistory {
  id: number;
  customerId: number;
  date: string;
  diagnosis: string;
  treatment: string;
  doctor: string;
  hospital: string;
  notes: string;
}