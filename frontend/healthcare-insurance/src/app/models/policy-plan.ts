export interface PolicyPlan {

  id: number;

  planCode: string;

  name: string;

  description: string;

  premium: number;

  coverageAmount: number;

  durationYears: number;

  benefits: string[];

  exclusions: string[];

  status: 'ACTIVE' | 'INACTIVE' | 'Active' | 'Inactive';

  createdAt?: string;
}