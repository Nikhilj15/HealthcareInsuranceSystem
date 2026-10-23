import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  PolicyService,
  AdminPolicyFilters
} from '../services/policy.service';

interface AdminPolicy {

  id: number;

  policyNumber: string;

  customerName: string;

  planName: string;

  premium: number;

  coverageAmount: number;

  startDate: string;

  expiryDate: string;

  status: string;
}

@Component({
  selector: 'app-policies',
  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl: './policies.html',

  styleUrl: './policies.css'
})
export class Policies implements OnInit {

  policies: AdminPolicy[] = [];

  loading = true;

  errorMessage = '';

  processingPolicyId: number | null = null;

  // =========================================================
  // SEARCH
  // =========================================================

  searchTerm = '';

  // =========================================================
  // COLUMN FILTERS
  // =========================================================

  policyNumberFilter = '';

  customerNameFilter = '';

  planNameFilter = '';

  statusFilter = '';

  // =========================================================
  // FILTER PANEL
  // =========================================================

  showFilters = false;

  constructor(
    private policyService: PolicyService,

    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    console.log(
      'Admin Policies Page Loaded'
    );

    this.loadPolicies();
  }

  // =========================================================
  // LOAD ADMIN POLICIES
  // =========================================================

  loadPolicies(): void {

    this.loading = true;

    this.errorMessage = '';

    console.log(
      'Loading admin policies...'
    );

    const filters: AdminPolicyFilters = {

      search: this.searchTerm,

      policyNumber:
        this.policyNumberFilter,

      customerName:
        this.customerNameFilter,

      planName:
        this.planNameFilter,

      status:
        this.statusFilter
    };

    this.policyService
      .getAdminPolicies(filters)
      .subscribe({

        next: (policies: any[]) => {

          console.log(
            'Admin Policies received:',
            policies
          );

          this.policies =
            policies.map(
              (policy: any) => {

                return {

                  id:
                    policy.id,

                  policyNumber:
                    policy.policyNumber,

                  customerName:
                    policy.user?.fullName ||
                    policy.user?.name ||
                    'Unknown Customer',

                  planName:
                    policy.plan?.name ||
                    'Unknown Plan',

                  premium:
                    Number(
                      policy.premiumAmount
                    ),

                  coverageAmount:
                    Number(
                      policy.coverageAmount
                    ),

                  startDate:
                    policy.startDate ||
                    '-',

                  expiryDate:
                    policy.endDate ||
                    '-',

                  status:
                    policy.status ||
                    'UNKNOWN'
                };
              }
            );

          this.loading = false;

          this.changeDetectorRef.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error loading admin policies:',
            error
          );

          this.policies = [];

          this.loading = false;

          this.errorMessage =
            'Unable to load policies from the server.';

          this.changeDetectorRef.detectChanges();
        }
      });
  }

  // =========================================================
  // SEARCH BUTTON
  // =========================================================

  searchPolicies(): void {

    console.log(
      'Searching policies:',
      this.searchTerm
    );

    this.loadPolicies();
  }

  // =========================================================
  // APPLY FILTERS
  // =========================================================

  applyFilters(): void {

    console.log(
      'Applying admin policy filters'
    );

    this.loadPolicies();

    this.showFilters = false;
  }

  // =========================================================
  // CLEAR FILTERS
  // =========================================================

  clearFilters(): void {

    this.searchTerm = '';

    this.policyNumberFilter = '';

    this.customerNameFilter = '';

    this.planNameFilter = '';

    this.statusFilter = '';

    this.loadPolicies();
  }

  // =========================================================
  // CHECK ACTIVE FILTERS
  // =========================================================

  hasActiveFilters(): boolean {

    return !!(
      this.searchTerm.trim() ||
      this.policyNumberFilter.trim() ||
      this.customerNameFilter.trim() ||
      this.planNameFilter.trim() ||
      this.statusFilter.trim()
    );
  }

  // =========================================================
  // COUNT ACTIVE FILTERS
  // =========================================================

  get activeFilterCount(): number {

    let count = 0;

    if (this.policyNumberFilter.trim()) {
      count++;
    }

    if (this.customerNameFilter.trim()) {
      count++;
    }

    if (this.planNameFilter.trim()) {
      count++;
    }

    if (this.statusFilter.trim()) {
      count++;
    }

    return count;
  }

  // =========================================================
  // APPROVE POLICY
  // =========================================================

  approvePolicy(
    policy: AdminPolicy
  ): void {

    const confirmed =
      confirm(
        `Are you sure you want to approve policy ${policy.policyNumber}?`
      );

    if (!confirmed) {
      return;
    }

    this.processingPolicyId =
      policy.id;

    console.log(
      'Approving policy:',
      policy.id
    );

    this.policyService
      .approvePolicy(policy.id)
      .subscribe({

        next: (updatedPolicy) => {

          console.log(
            'Policy approved:',
            updatedPolicy
          );

          policy.status =
            updatedPolicy.status;

          this.processingPolicyId =
            null;

          this.changeDetectorRef.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error approving policy:',
            error
          );

          this.processingPolicyId =
            null;

          alert(
            'Unable to approve policy. Please try again.'
          );

          this.changeDetectorRef.detectChanges();
        }
      });
  }

  // =========================================================
  // REJECT POLICY
  // =========================================================

  rejectPolicy(
    policy: AdminPolicy
  ): void {

    const reason =
      prompt(
        `Enter rejection reason for ${policy.policyNumber}:`
      );

    if (reason === null) {
      return;
    }

    const trimmedReason =
      reason.trim();

    if (!trimmedReason) {

      alert(
        'Please enter a rejection reason.'
      );

      return;
    }

    this.processingPolicyId =
      policy.id;

    console.log(
      'Rejecting policy:',
      policy.id,
      'Reason:',
      trimmedReason
    );

    this.policyService
      .rejectPolicy(
        policy.id,
        trimmedReason
      )
      .subscribe({

        next: (updatedPolicy) => {

          console.log(
            'Policy rejected:',
            updatedPolicy
          );

          policy.status =
            updatedPolicy.status;

          this.processingPolicyId =
            null;

          this.changeDetectorRef.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error rejecting policy:',
            error
          );

          this.processingPolicyId =
            null;

          alert(
            'Unable to reject policy. Please try again.'
          );

          this.changeDetectorRef.detectChanges();
        }
      });
  }
}