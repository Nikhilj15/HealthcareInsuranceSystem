import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import {
  CommonModule,
  DecimalPipe
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  PolicyPlan
} from '../models/policy-plan';

import {
  PolicyPlanService,
  PolicyPlanRequest
} from '../services/policy-plan.service';

@Component({
  selector: 'app-policy-plans',
  standalone: true,

  imports: [
    CommonModule,
    DecimalPipe,
    FormsModule
  ],

  templateUrl: './policy-plans.html',
  styleUrl: './policy-plans.css'
})
export class PolicyPlans implements OnInit {

  policyPlans: PolicyPlan[] = [];

  showForm = false;
  showDetails = false;

  selectedPlan: PolicyPlan | null = null;

  isEditMode = false;
  editingId: number | null = null;

  loading = false;
  saving = false;

  errorMessage = '';

  planForm: PolicyPlan =
    this.createEmptyPlan();

  constructor(
    private policyPlanService: PolicyPlanService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPlans();
  }

  // ==============================
  // LOAD PLANS
  // ==============================

  loadPlans(): void {

    this.loading = true;
    this.errorMessage = '';

    this.policyPlanService
      .getAllPlans()
      .subscribe({

        next: (data: PolicyPlan[]) => {

          console.log(
            'POLICY PLANS API RESPONSE:',
            data
          );

          this.policyPlans = data;

          this.loading = false;

          this.cdr.detectChanges();
        },

        error: (error: HttpErrorResponse) => {

          console.error(
            'Failed to load policy plans:',
            error
          );

          this.policyPlans = [];

          this.loading = false;

          this.errorMessage =
            this.getErrorMessage(
              error,
              'Unable to load policy plans.'
            );

          this.cdr.detectChanges();
        }

      });
  }

  // ==============================
  // EMPTY PLAN
  // ==============================

  createEmptyPlan(): PolicyPlan {

    return {

      id: 0,

      planCode: '',

      name: '',

      description: '',

      premium: 0,

      coverageAmount: 0,

      durationYears: 1,

      benefits: [],

      exclusions: [],

      status: 'Active'

    };
  }

  // ==============================
  // OPEN ADD FORM
  // ==============================

  openAddForm(): void {

    this.isEditMode = false;

    this.editingId = null;

    this.planForm =
      this.createEmptyPlan();

    this.showForm = true;

    this.errorMessage = '';

    this.cdr.detectChanges();
  }

  // ==============================
  // EDIT PLAN
  // ==============================

  editPlan(
    plan: PolicyPlan
  ): void {

    this.isEditMode = true;

    this.editingId = plan.id;

    this.planForm = {

      ...plan,

      benefits: [
        ...(plan.benefits || [])
      ],

      exclusions: [
        ...(plan.exclusions || [])
      ]

    };

    this.showForm = true;

    this.errorMessage = '';

    this.cdr.detectChanges();
  }

  // ==============================
  // CLOSE FORM
  // ==============================

  closeForm(): void {

    this.showForm = false;

    this.isEditMode = false;

    this.editingId = null;

    this.saving = false;

    this.errorMessage = '';

    this.cdr.detectChanges();
  }

  // ==============================
  // FORM VALIDATION
  // ==============================

  isFormValid(): boolean {

    const planCode =
      this.planForm.planCode?.trim();

    const name =
      this.planForm.name?.trim();

    const description =
      this.planForm.description?.trim();

    const premium =
      Number(this.planForm.premium);

    const coverageAmount =
      Number(this.planForm.coverageAmount);

    const durationYears =
      Number(this.planForm.durationYears);

    const benefits =
      this.planForm.benefits || [];

    if (!planCode) {
      return false;
    }

    if (!name) {
      return false;
    }

    if (!description) {
      return false;
    }

    if (
      !Number.isFinite(premium) ||
      premium <= 0
    ) {
      return false;
    }

    if (
      !Number.isFinite(coverageAmount) ||
      coverageAmount <= 0
    ) {
      return false;
    }

    if (
      !Number.isInteger(durationYears) ||
      durationYears <= 0
    ) {
      return false;
    }

    if (
      benefits.length === 0
    ) {
      return false;
    }

    return true;
  }

  // ==============================
  // SAVE PLAN
  // ==============================

  savePlan(): void {

    // Plan Code
    if (
      !this.planForm.planCode ||
      !this.planForm.planCode.trim()
    ) {

      alert(
        'Plan Code is required.'
      );

      return;
    }

    // Plan Name
    if (
      !this.planForm.name ||
      !this.planForm.name.trim()
    ) {

      alert(
        'Plan Name is required.'
      );

      return;
    }

    // Description
    if (
      !this.planForm.description ||
      !this.planForm.description.trim()
    ) {

      alert(
        'Description is required.'
      );

      return;
    }

    // Premium
    const premium =
      Number(this.planForm.premium);

    if (
      !Number.isFinite(premium) ||
      premium <= 0
    ) {

      alert(
        'Premium amount must be greater than zero.'
      );

      return;
    }

    // Coverage
    const coverageAmount =
      Number(
        this.planForm.coverageAmount
      );

    if (
      !Number.isFinite(coverageAmount) ||
      coverageAmount <= 0
    ) {

      alert(
        'Coverage amount must be greater than zero.'
      );

      return;
    }

    // Duration
    const durationYears =
      Number(
        this.planForm.durationYears
      );

    if (
      !Number.isInteger(durationYears) ||
      durationYears <= 0
    ) {

      alert(
        'Duration must be a valid whole number greater than zero.'
      );

      return;
    }

    // Benefits
    const benefits =
      (this.planForm.benefits || [])
        .map(
          benefit =>
            benefit.trim()
        )
        .filter(
          benefit =>
            benefit.length > 0
        );

    if (
      benefits.length === 0
    ) {

      alert(
        'At least one benefit is required.'
      );

      return;
    }

    // Status
    const status =
      this.planForm.status === 'Inactive'
        ? 'Inactive'
        : 'Active';

    const request: PolicyPlanRequest = {

      planCode:
        this.planForm.planCode
          .trim()
          .toUpperCase(),

      name:
        this.planForm.name
          .trim(),

      description:
        this.planForm.description
          .trim(),

      premium,

      coverageAmount,

      durationYears,

      benefits,

      exclusions:
        (this.planForm.exclusions || [])
          .map(
            exclusion =>
              exclusion.trim()
          )
          .filter(
            exclusion =>
              exclusion.length > 0
          ),

      status

    };

    console.log(
      'POLICY PLAN REQUEST:',
      request
    );

    this.saving = true;

    this.errorMessage = '';

    // ==============================
    // UPDATE
    // ==============================

    if (
      this.isEditMode &&
      this.editingId !== null
    ) {

      this.policyPlanService
        .updatePlan(
          this.editingId,
          request
        )
        .subscribe({

          next: (
            updatedPlan: PolicyPlan
          ) => {

            console.log(
              'UPDATED POLICY PLAN:',
              updatedPlan
            );

            const index =
              this.policyPlans.findIndex(
                plan =>
                  plan.id ===
                  updatedPlan.id
              );

            if (index !== -1) {

              this.policyPlans[index] =
                updatedPlan;
            }

            this.saving = false;

            alert(
              'Policy plan updated successfully.'
            );

            this.closeForm();

            this.cdr.detectChanges();
          },

          error: (
            error: HttpErrorResponse
          ) => {

            console.error(
              'Update policy plan failed:',
              error
            );

            this.saving = false;

            const message =
              this.getErrorMessage(
                error,
                'Unable to update policy plan.'
              );

            this.errorMessage =
              message;

            alert(message);

            this.cdr.detectChanges();
          }

        });

      return;
    }

    // ==============================
    // CREATE
    // ==============================

    this.policyPlanService
      .createPlan(request)
      .subscribe({

        next: (
          newPlan: PolicyPlan
        ) => {

          console.log(
            'CREATED POLICY PLAN:',
            newPlan
          );

          this.policyPlans = [
            newPlan,
            ...this.policyPlans
          ];

          this.saving = false;

          alert(
            'Policy plan added successfully.'
          );

          this.closeForm();

          this.cdr.detectChanges();
        },

        error: (
          error: HttpErrorResponse
        ) => {

          console.error(
            'Create policy plan failed:',
            error
          );

          this.saving = false;

          const message =
            this.getErrorMessage(
              error,
              'Unable to add policy plan.'
            );

          this.errorMessage =
            message;

          alert(message);

          this.cdr.detectChanges();
        }

      });
  }

  // ==============================
  // DELETE PLAN
  // ==============================

  deletePlan(
    plan: PolicyPlan
  ): void {

    const confirmed =
      confirm(
        `Are you sure you want to delete "${plan.name}"?`
      );

    if (!confirmed) {
      return;
    }

    this.policyPlanService
      .deletePlan(plan.id)
      .subscribe({

        next: () => {

          console.log(
            'POLICY PLAN DELETED:',
            plan.id
          );

          this.policyPlans =
            this.policyPlans.filter(
              item =>
                item.id !== plan.id
            );

          alert(
            'Policy plan deleted successfully.'
          );

          this.cdr.detectChanges();
        },

        error: (
          error: HttpErrorResponse
        ) => {

          console.error(
            'Delete policy plan failed:',
            error
          );

          const message =
            this.getErrorMessage(
              error,
              'Unable to delete policy plan.'
            );

          alert(message);

          this.cdr.detectChanges();
        }

      });
  }

  // ==============================
  // VIEW PLAN
  // ==============================

  viewPlan(
    plan: PolicyPlan
  ): void {

    this.selectedPlan = plan;

    this.showDetails = true;

    this.cdr.detectChanges();
  }

  // ==============================
  // CLOSE DETAILS
  // ==============================

  closeDetails(): void {

    this.showDetails = false;

    this.selectedPlan = null;

    this.cdr.detectChanges();
  }

  // ==============================
  // BENEFITS
  // ==============================

  getBenefitsText(): string {

    return (
      this.planForm.benefits || []
    ).join(', ');
  }

  setBenefits(
    value: string
  ): void {

    this.planForm.benefits =
      value
        .split(',')
        .map(
          benefit =>
            benefit.trim()
        )
        .filter(
          benefit =>
            benefit.length > 0
        );
  }

  // ==============================
  // EXCLUSIONS
  // ==============================

  getExclusionsText(): string {

    return (
      this.planForm.exclusions || []
    ).join(', ');
  }

  setExclusions(
    value: string
  ): void {

    this.planForm.exclusions =
      value
        .split(',')
        .map(
          exclusion =>
            exclusion.trim()
        )
        .filter(
          exclusion =>
            exclusion.length > 0
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
        'Plan code already exists. Please use a different Plan Code.'
      );
    }

    if (
      error.status === 400
    ) {

      return (
        error.error?.message ||
        'Please enter valid policy plan details.'
      );
    }

    if (
      error.status === 404
    ) {

      return (
        error.error?.message ||
        'Policy plan was not found.'
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

      return 'You do not have permission to perform this action.';
    }

    return (
      error.error?.message ||
      defaultMessage
    );
  }

  // ==============================
  // RETRY
  // ==============================

  retry(): void {

    this.loadPlans();
  }
}

