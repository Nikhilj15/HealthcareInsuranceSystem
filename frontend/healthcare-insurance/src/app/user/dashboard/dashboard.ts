import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { DecimalPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { PolicyPlan } from '../../models/policy-plan';
import { PolicyPlanService } from '../../services/policy-plan.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [
    RouterLink,
    DecimalPipe
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class UserDashboard implements OnInit {

  userName = 'User';

  policyPlans: PolicyPlan[] = [];

  loading = true;

  errorMessage = '';

  constructor(
    private router: Router,
    private policyPlanService: PolicyPlanService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    console.log('User Dashboard Loaded');

    this.loadPolicyPlans();
  }

  loadPolicyPlans(): void {

    this.loading = true;

    this.errorMessage = '';

    console.log('Loading active policy plans...');

    this.policyPlanService
      .getActivePlans()
      .pipe(

        finalize(() => {

          console.log(
            'Policy plan loading completed'
          );

          this.loading = false;

          this.changeDetectorRef.detectChanges();

        })

      )
      .subscribe({

        next: (plans) => {

          console.log(
            'Policy plans received:',
            plans
          );

          this.policyPlans = plans;

          this.errorMessage = '';

          this.changeDetectorRef.detectChanges();

        },

        error: (error) => {

          console.error(
            'Error loading policy plans:',
            error
          );

          this.policyPlans = [];

          this.errorMessage =
            'Unable to load insurance plans. Please try again.';

          this.loading = false;

          this.changeDetectorRef.detectChanges();

        }

      });
  }

  viewDetails(plan: PolicyPlan): void {

    console.log(
      'Opening policy plan:',
      plan
    );

    this.router.navigate([
      '/user/policy',
      plan.id
    ]);
  }

  logout(): void {

    const confirmed = confirm(
      'Are you sure you want to logout?'
    );

    if (!confirmed) {
      return;
    }

    localStorage.removeItem(
      'healthcare_token'
    );

    this.router.navigate([
      '/login'
    ]);
  }
}