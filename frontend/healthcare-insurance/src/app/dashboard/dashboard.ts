import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { HttpClient } from '@angular/common/http';

import { DashboardStats } from '../models/dashboard';


interface RecentActivity {

  title: string;

  description: string;

  time: string;

  activityAt: string;
}


@Component({

  selector: 'app-dashboard',

  standalone: true,

  imports: [
    CommonModule
  ],

  templateUrl: './dashboard.html',

  styleUrl: './dashboard.css'
})


export class Dashboard implements OnInit {


  // =========================================================
  // DASHBOARD STATISTICS
  // =========================================================

  stats: DashboardStats = {

    totalCustomers: 0,

    totalPolicies: 0,

    activePolicies: 0,

    pendingClaims: 0,

    approvedClaims: 0,

    rejectedClaims: 0
  };


  // =========================================================
  // RECENT ACTIVITIES
  // =========================================================

  recentActivities: RecentActivity[] = [];


  constructor(

    private http: HttpClient,

    private cdr: ChangeDetectorRef

  ) {}


  // =========================================================
  // INITIAL LOAD
  // =========================================================

  ngOnInit(): void {

    this.loadDashboardStats();

    this.loadRecentActivities();
  }


  // =========================================================
  // LOAD DASHBOARD STATISTICS
  // =========================================================

  loadDashboardStats(): void {

    this.http.get<DashboardStats>(

      'http://localhost:8080/api/admin/dashboard/stats'

    )
    .subscribe({

      next: (data: DashboardStats) => {

        console.log(
          'API RESPONSE:',
          data
        );


        this.stats = {

          totalCustomers:
            data.totalCustomers,

          totalPolicies:
            data.totalPolicies,

          activePolicies:
            data.activePolicies,

          pendingClaims:
            data.pendingClaims,

          approvedClaims:
            data.approvedClaims,

          rejectedClaims:
            data.rejectedClaims

        };


        console.log(
          'UPDATED STATS:',
          this.stats
        );


        this.cdr.detectChanges();
      },


      error: (error) => {

        console.error(

          'Failed to load dashboard statistics:',

          error

        );

      }

    });
  }


  // =========================================================
  // LOAD RECENT ACTIVITIES
  // =========================================================

  loadRecentActivities(): void {

    this.http.get<RecentActivity[]>(

      'http://localhost:8080/api/admin/dashboard/recent-activities'

    )
    .subscribe({

      next: (data: RecentActivity[]) => {

        console.log(

          'RECENT ACTIVITIES:',

          data

        );


        this.recentActivities = data;


        this.cdr.detectChanges();
      },


      error: (error) => {

        console.error(

          'Failed to load recent activities:',

          error

        );


        this.recentActivities = [];


        this.cdr.detectChanges();
      }

    });
  }

}