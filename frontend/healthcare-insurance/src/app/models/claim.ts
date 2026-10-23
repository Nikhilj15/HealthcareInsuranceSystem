export interface Claim {

  id: number;

  claimNumber: string;

  customerId: number;

  customerName: string;

  policyId: number;

  policyName: string;

  amount: number;

  claimDate: string;

  description: string;

  status: 'Pending' | 'Approved' | 'Rejected';

  rejectionReason?: string;

  processedDate?: string;

}