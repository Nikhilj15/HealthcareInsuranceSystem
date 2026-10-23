export interface Policy {
  id: number;

  policyId: string;

  customerId: number;

  customerName: string;

  planName: string;

  premium: number;

  coverageAmount: number;

  startDate: string;

  expiryDate: string;

  status: 'Active' | 'Expired' | 'Pending';
}