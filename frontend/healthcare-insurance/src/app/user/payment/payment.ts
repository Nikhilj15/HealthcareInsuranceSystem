import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import {
  DecimalPipe
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  ActivatedRoute,
  Router
} from '@angular/router';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  PolicyPlan
} from '../../models/policy-plan';

import {
  PolicyPlanService
} from '../../services/policy-plan.service';

import {
  PolicyService
} from '../../services/policy.service';

@Component({
  selector: 'app-payment',
  standalone: true,

  imports: [
    DecimalPipe,
    FormsModule
  ],

  templateUrl: './payment.html',
  styleUrl: './payment.css'
})
export class Payment implements OnInit {

  plan: PolicyPlan | null = null;

  planId = 0;

  paymentMethod:
    'upi' | 'qr' = 'upi';

  upiId = '';

  isProcessing = false;

  paymentComplete = false;

  purchaseError = '';

  purchaseDate = '';

  expiryDate = '';

  policyNumber = '';

  // Payment processing message
  paymentStatus = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private policyPlanService: PolicyPlanService,
    private policyService: PolicyService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    console.log(
      'Payment Page Loaded'
    );

    this.route.queryParamMap
      .subscribe(params => {

        const id =
          params.get('planId');

        console.log(
          'Plan ID from URL:',
          id
        );

        if (!id) {

          this.purchaseError =
            'Policy plan was not selected.';

          this.changeDetectorRef
            .detectChanges();

          return;
        }

        const parsedId =
          Number(id);

        if (
          !Number.isInteger(parsedId) ||
          parsedId <= 0
        ) {

          this.purchaseError =
            'Invalid policy plan.';

          this.changeDetectorRef
            .detectChanges();

          return;
        }

        this.planId =
          parsedId;

        this.loadPlan();
      });
  }


  // ==============================
  // LOAD POLICY PLAN
  // ==============================

  loadPlan(): void {

    this.purchaseError = '';

    this.plan = null;

    this.policyPlanService
      .getPlanById(this.planId)
      .subscribe({

        next: (plan) => {

          console.log(
            'Payment plan received:',
            plan
          );

          // ------------------------------
          // Validate returned plan
          // ------------------------------

          if (!plan) {

            this.purchaseError =
              'Policy plan was not found.';

            this.changeDetectorRef
              .detectChanges();

            return;
          }

          // ------------------------------
          // Debug status
          // ------------------------------

          console.log(
            'PLAN STATUS FROM BACKEND:',
            plan.status
          );

          console.log(
            'NORMALIZED STATUS:',
            String(plan.status)
              .trim()
              .toUpperCase()
          );

          // ------------------------------
          // Active plan validation
          // ------------------------------

          if (
            !this.isPlanActive(plan)
          ) {

            this.purchaseError =
              'This policy plan is inactive and cannot be purchased.';

            this.plan = null;

            this.changeDetectorRef
              .detectChanges();

            return;
          }

          // ------------------------------
          // Premium validation
          // ------------------------------

          if (
            !plan.premium ||
            plan.premium <= 0
          ) {

            this.purchaseError =
              'This policy has an invalid premium amount.';

            this.plan = null;

            this.changeDetectorRef
              .detectChanges();

            return;
          }

          // ------------------------------
          // Coverage validation
          // ------------------------------

          if (
            !plan.coverageAmount ||
            plan.coverageAmount <= 0
          ) {

            this.purchaseError =
              'This policy has an invalid coverage amount.';

            this.plan = null;

            this.changeDetectorRef
              .detectChanges();

            return;
          }

          // ------------------------------
          // Plan ready
          // ------------------------------

          this.plan = plan;

          this.purchaseError = '';

          console.log(
            'ACTIVE PLAN READY FOR PAYMENT:',
            this.plan
          );

          this.changeDetectorRef
            .detectChanges();
        },

        error: (
          error: HttpErrorResponse
        ) => {

          console.error(
            'Error loading policy plan:',
            error
          );

          this.plan = null;

          this.purchaseError =
            this.getErrorMessage(
              error,
              'Unable to load policy plan.'
            );

          this.changeDetectorRef
            .detectChanges();
        }
      });
  }


  // ==============================
  // CHECK PLAN STATUS
  // ==============================

  private isPlanActive(
    plan: PolicyPlan
  ): boolean {

    if (
      plan.status === null ||
      plan.status === undefined
    ) {

      return false;
    }

    const status =
      String(plan.status)
        .trim()
        .toUpperCase();

    return status === 'ACTIVE';
  }


  // ==============================
  // PAYMENT VALIDATION
  // ==============================

  proceedToPayment(): void {

    if (!this.plan) {

      alert(
        'Policy plan is not available.'
      );

      return;
    }

    // ------------------------------
    // Check active plan
    // ------------------------------

    if (
      !this.isPlanActive(this.plan)
    ) {

      alert(
        'This policy plan is inactive.'
      );

      return;
    }

    // ------------------------------
    // Premium validation
    // ------------------------------

    if (
      !this.plan.premium ||
      this.plan.premium <= 0
    ) {

      alert(
        'Invalid premium amount.'
      );

      return;
    }

    // ------------------------------
    // UPI validation
    // ------------------------------

    if (
      this.paymentMethod === 'upi'
    ) {

      const enteredUpi =
        this.upiId.trim();

      if (!enteredUpi) {

        alert(
          'Enter a UPI ID to continue.'
        );

        return;
      }

      if (
        !this.isValidUpiId(
          enteredUpi
        )
      ) {

        alert(
          'Enter a valid UPI ID, for example name@bank.'
        );

        return;
      }
    }

    // ------------------------------
    // Start mock payment
    // ------------------------------

    this.startMockPayment();
  }


  // ==============================
  // MOCK PAYMENT
  // ==============================

  startMockPayment(): void {

    if (this.isProcessing) {
      return;
    }

    console.log(
      'Starting mock payment...'
    );

    this.isProcessing = true;

    this.paymentStatus =
      'Processing payment...';

    this.purchaseError = '';

    this.changeDetectorRef
      .detectChanges();

    /*
     * Mock payment delay.
     *
     * 3 seconds pause before
     * payment is considered successful.
     */

    setTimeout(() => {

      console.log(
        'Mock payment successful.'
      );

      this.paymentStatus =
        'Payment successful! Creating your policy...';

      this.changeDetectorRef
        .detectChanges();

      /*
       * Small additional delay so the
       * user can actually see the
       * "Payment successful" message.
       */

      setTimeout(() => {

        this.purchasePolicy();

      }, 700);

    }, 3000);
  }


  // ==============================
  // UPI VALIDATION
  // ==============================

  private isValidUpiId(
    upiId: string
  ): boolean {

    const upiPattern =
      /^[a-zA-Z0-9._-]{2,50}@[a-zA-Z0-9._-]{2,30}$/;

    return upiPattern.test(
      upiId
    );
  }


  // ==============================
// PURCHASE POLICY
// ==============================

purchasePolicy(): void {

  if (!this.plan) {

    this.isProcessing = false;

    this.paymentStatus = '';

    alert(
      'Policy plan is not available.'
    );

    return;
  }

  // ------------------------------
  // Check active plan
  // ------------------------------

  if (!this.isPlanActive(this.plan)) {

    this.isProcessing = false;

    this.paymentStatus = '';

    alert(
      'This policy plan is inactive.'
    );

    return;
  }

  console.log(
    'Creating policy after successful payment...',
    {
      planId: this.planId,
      paymentMethod: this.paymentMethod
    }
  );

  this.policyService
    .purchasePolicy(
      this.planId
    )
    .subscribe({

      next: (policy) => {

        console.log(
          'Policy purchased successfully:',
          policy
        );

        this.isProcessing = false;

        this.paymentStatus = '';

        this.paymentComplete = true;

        this.policyNumber =
          policy.policyNumber;

        this.purchaseDate =
          this.formatDate(
            policy.startDate
          );

        this.expiryDate =
          this.formatDate(
            policy.endDate
          );

        this.purchaseError = '';

        this.changeDetectorRef
          .detectChanges();
      },

      error: (
        error: HttpErrorResponse
      ) => {

        console.error(
          'Policy purchase failed:',
          error
        );

        this.isProcessing = false;

        this.paymentStatus = '';

        this.purchaseError =
          this.getErrorMessage(
            error,
            'Unable to purchase policy. Please try again.'
          );

        this.changeDetectorRef
          .detectChanges();
      }
    });
}

  // ==============================
  // GET USER ID FROM JWT
  // ==============================

  private getUserIdFromToken():
    number | null {

    const token =
      localStorage.getItem(
        'healthcare_token'
      );

    if (!token) {
      return null;
    }

    try {

      const tokenParts =
        token.split('.');

      if (
        tokenParts.length !== 3
      ) {

        throw new Error(
          'Invalid JWT structure'
        );
      }

      const payload =
        JSON.parse(
          atob(
            tokenParts[1]
          )
        );

      console.log(
        'JWT payload:',
        payload
      );

      if (
        payload.userId === undefined ||
        payload.userId === null
      ) {

        return null;
      }

      const userId =
        Number(
          payload.userId
        );

      if (
        !Number.isInteger(userId) ||
        userId <= 0
      ) {

        return null;
      }

      return userId;

    } catch (error) {

      console.error(
        'Invalid JWT token:',
        error
      );

      localStorage.removeItem(
        'healthcare_token'
      );

      return null;
    }
  }


  // ==============================
  // FORMAT DATE
  // ==============================

  private formatDate(
    date: string
  ): string {

    if (!date) {
      return '';
    }

    const parsedDate =
      new Date(date);

    return parsedDate.toLocaleDateString(
      'en-IN',
      {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      }
    );
  }


  // ==============================
  // ERROR MESSAGE
  // ==============================

  private getErrorMessage(
    error: HttpErrorResponse,
    defaultMessage: string
  ): string {

    if (
      error.status === 409
    ) {

      return (
        error.error?.message ||
        'You already have an active or pending policy for this plan.'
      );
    }

    if (
      error.status === 400
    ) {

      return (
        error.error?.message ||
        'Invalid policy purchase request.'
      );
    }

    if (
      error.status === 401
    ) {

      return 'Session expired. Please login again.';
    }

    if (
      error.status === 403
    ) {

      return 'You are not allowed to purchase this policy.';
    }

    if (
      error.status === 404
    ) {

      return (
        error.error?.message ||
        'Policy plan was not found.'
      );
    }

    return (
      error.error?.message ||
      defaultMessage
    );
  }


  // ==============================
  // VIEW MY POLICIES
  // ==============================

 viewPolicy(): void {

  this.router.navigate([
    '/user/policies'
  ]);
}
}
