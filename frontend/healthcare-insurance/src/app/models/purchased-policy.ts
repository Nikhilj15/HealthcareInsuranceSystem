export interface PurchasedPolicy {

  id: number;

  policyNumber: string;

  user: {
    id: number;
    fullName: string;
    email: string;
    phone: string;
  };

  plan: {
    id: number;
    planCode: string;
    name: string;
    description: string;
    premium: number;
    coverageAmount: number;
    durationYears: number;
    benefits: string[];
    exclusions: string[];
    status: string;
  };

  premiumAmount: number;

  coverageAmount: number;

  startDate: string;

  endDate: string;

  status: string;

  appliedAt: string;

  approvedAt: string | null;

  rejectedAt: string | null;

  rejectionReason: string | null;
}