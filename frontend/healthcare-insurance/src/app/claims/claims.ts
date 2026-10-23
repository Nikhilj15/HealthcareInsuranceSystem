import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';

import {
  ClaimService,
  ClaimResponse
} from '../services/claim.service';

@Component({
  selector: 'app-claims',
  standalone: true,
  imports: [
    CommonModule
  ],
  templateUrl: './claims.html',
  styleUrl: './claims.css'
})
export class Claims implements OnInit {

  claims: ClaimResponse[] = [];

  loading = true;
  errorMessage = '';

  processingClaimId: number | null = null;

  constructor(
    private claimService: ClaimService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    console.log('Admin Claims Page Loaded');

    this.loadClaims();
  }

  loadClaims(): void {

    this.loading = true;
    this.errorMessage = '';

    this.claimService
      .getAllClaims()
      .subscribe({

        next: (claims) => {

          console.log(
            'Admin claims received:',
            claims
          );

          this.claims = claims;

          this.loading = false;

          this.changeDetectorRef.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error loading admin claims:',
            error
          );

          this.claims = [];

          this.loading = false;

          this.errorMessage =
            'Unable to load claims. Please try again.';

          this.changeDetectorRef.detectChanges();
        }
      });
  }

  approveClaim(claim: ClaimResponse): void {

    if (claim.status !== 'PENDING') {
      return;
    }

    const confirmed =
      confirm(
        `Are you sure you want to approve claim ${claim.claimNumber}?`
      );

    if (!confirmed) {
      return;
    }

    this.processingClaimId = claim.id;

    this.claimService
      .approveClaim(claim.id)
      .subscribe({

        next: (updatedClaim) => {

          console.log(
            'Claim approved:',
            updatedClaim
          );

          const index =
            this.claims.findIndex(
              item => item.id === claim.id
            );

          if (index !== -1) {
            this.claims[index] =
              updatedClaim;
          }

          this.processingClaimId = null;

          alert(
            `Claim ${updatedClaim.claimNumber} approved successfully.`
          );

          this.changeDetectorRef.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error approving claim:',
            error
          );

          this.processingClaimId = null;

          const message =
            error?.error?.message ||
            error?.error?.error ||
            'Unable to approve claim.';

          alert(message);

          this.changeDetectorRef.detectChanges();
        }
      });
  }

  rejectClaim(claim: ClaimResponse): void {

    if (claim.status !== 'PENDING') {
      return;
    }

    const reason =
      prompt(
        `Enter rejection reason for claim ${claim.claimNumber}:`
      );

    if (reason === null) {
      return;
    }

    if (!reason.trim()) {

      alert(
        'Rejection reason is required.'
      );

      return;
    }

    this.processingClaimId = claim.id;

    this.claimService
      .rejectClaim(
        claim.id,
        reason.trim()
      )
      .subscribe({

        next: (updatedClaim) => {

          console.log(
            'Claim rejected:',
            updatedClaim
          );

          const index =
            this.claims.findIndex(
              item => item.id === claim.id
            );

          if (index !== -1) {
            this.claims[index] =
              updatedClaim;
          }

          this.processingClaimId = null;

          alert(
            `Claim ${updatedClaim.claimNumber} rejected successfully.`
          );

          this.changeDetectorRef.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error rejecting claim:',
            error
          );

          this.processingClaimId = null;

          const message =
            error?.error?.message ||
            error?.error?.error ||
            'Unable to reject claim.';

          alert(message);

          this.changeDetectorRef.detectChanges();
        }
      });
  }

  refreshClaims(): void {

    this.loadClaims();
  }
}