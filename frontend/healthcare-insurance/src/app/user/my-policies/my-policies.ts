import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { DecimalPipe } from '@angular/common';
import { Router } from '@angular/router';

import { PurchasedPolicy } from '../../models/purchased-policy';
import { PolicyService } from '../../services/policy.service';

@Component({
  selector: 'app-my-policies',
  standalone: true,
  imports: [
    DecimalPipe
  ],
  templateUrl: './my-policies.html',
  styleUrl: './my-policies.css'
})
export class MyPolicies implements OnInit {

  policies: PurchasedPolicy[] = [];

  loading = true;

  errorMessage = '';

  constructor(
    private router: Router,
    private policyService: PolicyService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    console.log('My Policies Page Loaded');

    this.loadPolicies();
  }

  loadPolicies(): void {

    this.loading = true;

    this.errorMessage = '';

    console.log('Loading user policies...');

    const userId = this.getUserIdFromToken();

    console.log('User ID from JWT:', userId);

    if (!userId) {

      this.errorMessage =
        'User session not found. Please login again.';

      this.loading = false;

      this.changeDetectorRef.detectChanges();

      return;
    }

    console.log(
      'Calling GET /api/policies/user/' + userId
    );

    this.policyService
      .getUserPolicies(userId)
      .subscribe({

        next: (policies) => {

          console.log(
            'User policies received:',
            policies
          );

          this.policies = policies;

          this.loading = false;

          this.errorMessage = '';

          this.changeDetectorRef.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error loading user policies:',
            error
          );

          this.policies = [];

          this.loading = false;

          this.errorMessage =
            'Unable to load your policies. Please try again.';

          this.changeDetectorRef.detectChanges();
        }

      });
  }

  private getUserIdFromToken(): number | null {

    const token =
      localStorage.getItem('healthcare_token');

    console.log(
      'Healthcare token exists:',
      !!token
    );

    if (!token) {

      return null;
    }

    try {

      const payload =
        JSON.parse(
          atob(token.split('.')[1])
        );

      console.log(
        'JWT payload:',
        payload
      );

      if (!payload.userId) {

        return null;
      }

      return Number(
        payload.userId
      );

    } catch (error) {

      console.error(
        'Invalid JWT token:',
        error
      );

      localStorage.removeItem(
        'healthcare_token'
      );

      this.router.navigate([
        '/login'
      ]);

      return null;
    }
  }

  claimPolicy(
    policy: PurchasedPolicy
  ): void {

    console.log(
      'Starting claim for policy:',
      policy
    );

    this.router.navigate(
      ['/user/claims'],
      {
        queryParams: {
          policyId: policy.id
        }
      }
    );
  }
}