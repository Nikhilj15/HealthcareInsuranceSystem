
import { Routes } from '@angular/router';

import { Dashboard } from './dashboard/dashboard';
import { Customers } from './customers/customers';
import { Policies } from './policies/policies';
import { PolicyPlans } from './policy-plans/policy-plans';
import { Claims } from './claims/claims';

import { Login } from './auth/login/login';
import { Register } from './auth/register/register';

import { AdminLayout } from './layouts/admin-layout/admin-layout';
import { UserLayout } from './layouts/user-layout/user-layout';

import { UserDashboard } from './user/dashboard/dashboard';
import { PolicyDetails } from './user/policy-details/policy-details';
import { Profile } from './user/profile/profile';
import { MyPolicies } from './user/my-policies/my-policies';
import { Payment } from './user/payment/payment';
import { Claims as UserClaims } from './user/claims/claims';

import { authGuard } from './guards/auth-guard';
import { roleGuard } from './guards/role-guard';

import { LandingComponent } from './landing/landing.component';

export const routes: Routes = [

  // PUBLIC LANDING PAGE
  {
    path: '',
    component: LandingComponent,
    pathMatch: 'full'
  },
  {
    path: 'landing',
    component: LandingComponent
  },

  // AUTHENTICATION
  {
    path: 'login',
    component: Login
  },
  {
    path: 'register',
    component: Register
  },

  // ADMIN ROUTES
  {
    path: 'admin',
    component: AdminLayout,
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMIN' },

    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        component: Dashboard
      },
      {
        path: 'customers',
        component: Customers
      },
      {
        path: 'policies',
        component: Policies
      },
      {
        path: 'policy-plans',
        component: PolicyPlans
      },
      {
        path: 'claims',
        component: Claims
      }
    ]
  },

  // USER ROUTES
  {
    path: 'user',
    component: UserLayout,
    canActivate: [authGuard, roleGuard],
    data: { role: 'USER' },

    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        component: UserDashboard
      },
      {
        path: 'policy/:id',
        component: PolicyDetails
      },
      {
        path: 'policies',
        component: MyPolicies
      },
      {
        path: 'payment',
        component: Payment
      },
      {
        path: 'claims',
        component: UserClaims
      },
      {
        path: 'profile',
        component: Profile
      }
    ]
  },

  // KEEP WILDCARD LAST
  {
    path: '**',
    redirectTo: ''
  }
];
