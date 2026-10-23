import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { DecimalPipe } from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  ActivatedRoute,
  Router
} from '@angular/router';

import {
  ClaimService,
  ClaimResponse
} from '../../services/claim.service';

import {
  PolicyService
} from '../../services/policy.service';

interface UserClaim {
  id: number;
  claimNumber: string;
  policyId: number;
  policyNumber: string;
  policyName: string;
  amount: number;
  reason: string;
  description: string;
  claimDate: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  rejectionReason?: string;
}

@Component({
  selector: 'app-claims',

  standalone: true,

  imports: [
    DecimalPipe,
    FormsModule
  ],

  templateUrl: './claims.html',

  styleUrl: './claims.css'
})
export class Claims implements OnInit {

  claims: UserClaim[] = [];

  loading = true;

  errorMessage = '';

  showClaimForm = false;

  submitting = false;

  selectedPolicyId: number | null = null;

  selectedPolicyName = '';

  selectedPolicyNumber = '';

  selectedPolicyCoverage = 0;

  hospitalName = '';

  treatmentDetails = '';

  selectedFileName = '';

  claimForm = {
    policyId: 0,
    policyName: '',
    claimAmount: 0,
    reason: '',
    description: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private claimService: ClaimService,
    private policyService: PolicyService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    console.log(
      'Claims Page Loaded'
    );

    this.loadMyClaims();

    this.route.queryParamMap.subscribe(
      params => {

        const policyId =
          params.get('policyId');

        if (policyId) {

          const id =
            Number(policyId);

          if (id > 0) {

            this.loadPolicyForClaim(id);
          }
        }
      }
    );
  }

  // ==========================================
  // LOAD MY CLAIMS
  // ==========================================

  loadMyClaims(): void {

    this.loading = true;

    this.errorMessage = '';

    console.log(
      'Loading user claims...'
    );

    this.claimService
      .getMyClaims()
      .subscribe({

        next: (claims) => {

          console.log(
            'Claims received:',
            claims
          );

          this.claims =
            claims.map(
              claim => this.convertClaim(
                claim
              )
            );

          this.loading = false;

          this.changeDetectorRef.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error loading claims:',
            error
          );

          this.claims = [];

          this.loading = false;

          this.errorMessage =
            'Unable to load your claims.';

          this.changeDetectorRef.detectChanges();
        }
      });
  }

  // ==========================================
  // LOAD POLICY FOR CLAIM
  // ==========================================

  loadPolicyForClaim(
    policyId: number
  ): void {

    console.log(
      'Loading policy for claim:',
      policyId
    );

    this.policyService
      .getPolicyById(policyId)
      .subscribe({

        next: (policy) => {

          console.log(
            'Policy received:',
            policy
          );

          if (
            policy.status !== 'APPROVED' &&
            policy.status !== 'ACTIVE'
          ) {

            alert(
              'You can submit a claim only for an approved or active policy.'
            );

            this.router.navigate([
              '/user/my-policies'
            ]);

            return;
          }

          this.selectedPolicyId =
            policy.id;

          this.selectedPolicyName =
            policy.plan?.name ||
            'Healthcare Policy';

          this.selectedPolicyNumber =
            policy.policyNumber;

          this.selectedPolicyCoverage =
            Number(
              policy.coverageAmount
            );

          this.claimForm = {

            policyId: policy.id,

            policyName:
              this.selectedPolicyName,

            claimAmount: 0,

            reason: '',

            description: ''
          };

          this.showClaimForm = true;

          this.changeDetectorRef.detectChanges();
        },

        error: (error) => {

          console.error(
            'Unable to load policy:',
            error
          );

          alert(
            'Unable to load the selected policy.'
          );

          this.router.navigate([
            '/user/my-policies'
          ]);
        }
      });
  }

  // ==========================================
  // OPEN CLAIM FORM
  // ==========================================

  openClaimForm(): void {

    this.selectedPolicyId = null;

    this.selectedPolicyName = '';

    this.selectedPolicyNumber = '';

    this.selectedPolicyCoverage = 0;

    this.claimForm = {

      policyId: 0,

      policyName: '',

      claimAmount: 0,

      reason: '',

      description: ''
    };

    this.showClaimForm = true;

    this.changeDetectorRef.detectChanges();
  }

  // ==========================================
  // CLOSE CLAIM FORM
  // ==========================================

  closeClaimForm(): void {

    if (this.submitting) {
      return;
    }

    this.showClaimForm = false;

    this.selectedPolicyId = null;

    this.changeDetectorRef.detectChanges();
  }

  // ==========================================
  // SELECT FILE
  // ==========================================

  selectFile(
    event: Event
  ): void {

    const input =
      event.target as HTMLInputElement;

    this.selectedFileName =
      input.files?.[0]?.name ?? '';
  }

  // ==========================================
  // SUBMIT CLAIM
  // ==========================================

  submitClaim(): void {

    if (
      !this.claimForm.policyId ||
      this.claimForm.policyId <= 0
    ) {

      alert(
        'Please select a valid policy.'
      );

      return;
    }

    if (
      !this.claimForm.claimAmount ||
      this.claimForm.claimAmount <= 0
    ) {

      alert(
        'Claim amount must be greater than zero.'
      );

      return;
    }

    if (
      this.selectedPolicyCoverage > 0 &&
      this.claimForm.claimAmount >
      this.selectedPolicyCoverage
    ) {

      alert(
        'Claim amount cannot exceed your policy coverage amount.'
      );

      return;
    }

    if (
      !this.claimForm.reason ||
      !this.claimForm.reason.trim()
    ) {

      alert(
        'Please enter the reason for the claim.'
      );

      return;
    }

    if (
      !this.claimForm.description ||
      !this.claimForm.description.trim()
    ) {

      alert(
        'Please enter claim description.'
      );

      return;
    }

    this.submitting = true;

    console.log(
      'Submitting claim:',
      this.claimForm
    );

    this.claimService
      .submitClaim(
        this.claimForm.policyId,
        this.claimForm.claimAmount,
        this.claimForm.reason.trim(),
        this.claimForm.description.trim()
      )
      .subscribe({

        next: (response) => {

          console.log(
            'Claim submitted successfully:',
            response
          );

          this.submitting = false;

          this.showClaimForm = false;

          alert(
            `Claim submitted successfully.\n\nClaim Number: ${response.claimNumber}\nStatus: ${response.status}`
          );

          this.loadMyClaims();

          this.changeDetectorRef.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error submitting claim:',
            error
          );

          this.submitting = false;

          const message =
            error?.error?.message ||
            error?.error?.error ||
            'Unable to submit claim. Please try again.';

          alert(message);

          this.changeDetectorRef.detectChanges();
        }
      });
  }

  // ==========================================
  // CONVERT BACKEND CLAIM
  // ==========================================

  private convertClaim(
    claim: ClaimResponse
  ): UserClaim {

    return {

      id: claim.id,

      claimNumber:
        claim.claimNumber,

      policyId:
        claim.policyId,

      policyNumber:
        claim.policyNumber,

      policyName:
        claim.policyNumber,

      amount:
        Number(
          claim.claimAmount
        ),

      reason:
        claim.reason,

      description:
        claim.description,

      claimDate:
        claim.claimDate,

      status:
        claim.status,

      rejectionReason:
        claim.rejectionReason
    };
  }

}
