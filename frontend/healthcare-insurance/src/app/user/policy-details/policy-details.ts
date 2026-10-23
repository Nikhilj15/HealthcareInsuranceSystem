import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import {
  DecimalPipe
} from '@angular/common';

import {
  ActivatedRoute,
  Router,
  RouterLink
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

@Component({
  selector: 'app-policy-details',
  standalone: true,

  imports: [
    RouterLink,
    DecimalPipe
  ],

  templateUrl: './policy-details.html',
  styleUrl: './policy-details.css'
})
export class PolicyDetails implements OnInit {

  planId = 0;

  plan: PolicyPlan | null = null;

  loading = true;

  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private policyPlanService: PolicyPlanService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  // ==========================================
  // INITIALIZE
  // ==========================================

  ngOnInit(): void {

    console.log(
      'Policy Details Page Loaded'
    );

    const id =
      this.route.snapshot.paramMap.get(
        'id'
      );

    console.log(
      'Plan ID from URL:',
      id
    );

    // ------------------------------------------
    // CHECK ID EXISTS
    // ------------------------------------------

    if (!id) {

      this.errorMessage =
        'Invalid policy plan.';

      this.loading = false;

      this.changeDetectorRef.detectChanges();

      return;
    }

    // ------------------------------------------
    // CONVERT ID TO NUMBER
    // ------------------------------------------

    const parsedId =
      Number(id);

    // ------------------------------------------
    // VALIDATE ID
    // ------------------------------------------

    if (
      !Number.isInteger(parsedId) ||
      parsedId <= 0
    ) {

      this.errorMessage =
        'Invalid policy plan.';

      this.loading = false;

      this.changeDetectorRef.detectChanges();

      return;
    }

    this.planId =
      parsedId;

    // ------------------------------------------
    // LOAD PLAN
    // ------------------------------------------

    this.loadPlan();
  }

  // ==========================================
  // LOAD POLICY PLAN
  // ==========================================

  loadPlan(): void {

    this.loading = true;

    this.errorMessage = '';

    this.policyPlanService
      .getPlanById(this.planId)
      .subscribe({

        // --------------------------------------
        // SUCCESS
        // --------------------------------------

        next: (
          plan: PolicyPlan
        ) => {

          console.log(
            'Policy plan received:',
            plan
          );

          // ------------------------------------
          // PLAN NOT FOUND
          // ------------------------------------

          if (!plan) {

            this.plan = null;

            this.errorMessage =
              'Policy plan was not found.';

            this.loading = false;

            this.changeDetectorRef
              .detectChanges();

            return;
          }

          // ------------------------------------
          // STORE PLAN
          // ------------------------------------

          this.plan = plan;

          console.log(
            'Plan status received from backend:',
            this.plan.status
          );

          console.log(
            'Plan premium received:',
            this.plan.premium
          );

          console.log(
            'Plan coverage received:',
            this.plan.coverageAmount
          );

          this.loading = false;

          this.changeDetectorRef
            .detectChanges();
        },

        // --------------------------------------
        // ERROR
        // --------------------------------------

        error: (
          error: HttpErrorResponse
        ) => {

          console.error(
            'Error loading policy plan:',
            error
          );

          this.plan = null;

          this.errorMessage =
            error.error?.message ||
            'Unable to load policy details. Please try again.';

          this.loading = false;

          this.changeDetectorRef
            .detectChanges();
        }
      });
  }

  // ==========================================
  // BUY / APPLY PLAN
  // ==========================================

  buyPlan(): void {

    // ------------------------------------------
    // PLAN CHECK
    // ------------------------------------------

    if (!this.plan) {

      console.warn(
        'Cannot purchase because plan is not loaded.'
      );

      return;
    }

    // ------------------------------------------
    // STATUS CHECK
    // ------------------------------------------

    /*
     * Backend returns:
     *
     *     ACTIVE
     *
     * Earlier code checked:
     *
     *     Active
     *
     * That caused:
     *
     *     ACTIVE !== Active
     *
     * and incorrectly showed the inactive message.
     *
     * We now normalize the value to uppercase.
     */

    const planStatus =
      String(this.plan.status || '')
        .trim()
        .toUpperCase();

    console.log(
      'Checking policy plan status:',
      planStatus
    );

    if (planStatus !== 'ACTIVE') {

      alert(
        'This policy plan is inactive and cannot be purchased.'
      );

      return;
    }

    // ------------------------------------------
    // PREMIUM CHECK
    // ------------------------------------------

    if (
      !this.plan.premium ||
      this.plan.premium <= 0
    ) {

      alert(
        'This policy plan has an invalid premium.'
      );

      return;
    }

    // ------------------------------------------
    // COVERAGE CHECK
    // ------------------------------------------

    if (
      !this.plan.coverageAmount ||
      this.plan.coverageAmount <= 0
    ) {

      alert(
        'This policy plan has an invalid coverage amount.'
      );

      return;
    }

    // ------------------------------------------
    // EVERYTHING IS VALID
    // ------------------------------------------

    console.log(
      'Policy plan is ACTIVE and can be purchased:',
      this.plan
    );

    // ------------------------------------------
    // NAVIGATE TO PAYMENT
    // ------------------------------------------

    this.router.navigate(
      ['/user/payment'],
      {
        queryParams: {
          planId: this.plan.id
        }
      }
    );
  }
}