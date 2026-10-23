export interface Payment {
  id: number;
  customerId: number;
  policyPlanId: number;
  amount: number;
  paymentMethod: 'UPI' | 'QR';
  transactionId: string;
  paymentStatus: 'Pending' | 'Success' | 'Failed';
  paymentDate: string;
}