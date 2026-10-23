
import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

interface PolicyPlan {
  id: number;
  name: string;
  planCode: string;
  description: string;
  premium: number;
  coverageAmount: number;
  durationYears: number;
  benefits: string[];
  exclusions: string[];
  status: string;
}

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    CurrencyPipe
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent implements OnInit {

  private readonly http = inject(HttpClient);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly currentYear = new Date().getFullYear();

  // Important: use the actual API URL.
  private readonly plansApiUrl =
    'http://localhost:8080/api/policy-plans';

  plans: PolicyPlan[] = [];

  isLoadingPlans = true;
  plansError = '';
  menuOpen = false;

  ngOnInit(): void {
    // Fetch plans as soon as the landing page initializes.
    this.loadPlans();
  }

  loadPlans(): void {
    this.isLoadingPlans = true;
    this.plansError = '';

    // Refresh the view immediately to show the loading state.
    this.cdr.detectChanges();

    this.http
      .get<PolicyPlan[]>(this.plansApiUrl)
      .pipe(
        finalize(() => {
          // Stop the loader after success or failure.
          this.isLoadingPlans = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (response: PolicyPlan[]) => {
          console.log('Policy plans API response:', response);

          if (!Array.isArray(response)) {
            this.plans = [];
            this.plansError =
              'Invalid response received from the server.';
            this.cdr.detectChanges();
            return;
          }

          this.plans = response.filter(
            (plan) =>
              plan &&
              String(plan.status ?? '').toUpperCase() === 'ACTIVE'
          );

          if (this.plans.length === 0) {
            this.plansError =
              'No active healthcare plans are available right now.';
          } else {
            this.plansError = '';
          }

          console.log('Active plans:', this.plans);
          console.log('Active plan count:', this.plans.length);

          // Update the UI as soon as the plans arrive.
          this.cdr.detectChanges();
        },

        error: (error) => {
          console.error(
            'Unable to load healthcare plans:',
            error
          );

          this.plans = [];

          if (error.status === 0) {
            this.plansError =
              'Unable to connect to the backend. Please check whether Spring Boot is running.';
          } else if (
            error.status === 401 ||
            error.status === 403
          ) {
            this.plansError =
              'Access denied. Please check the API security configuration.';
          } else {
            this.plansError =
              'Unable to load healthcare plans. Please try again.';
          }

          this.cdr.detectChanges();
        }
      });
  }

  retryLoadingPlans(): void {
    this.loadPlans();
  }

  closeMenu(): void {
    this.menuOpen = false;
  }

  scrollToSection(sectionId: string): void {
    this.closeMenu();

    document
      .getElementById(sectionId)
      ?.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
  }
}
