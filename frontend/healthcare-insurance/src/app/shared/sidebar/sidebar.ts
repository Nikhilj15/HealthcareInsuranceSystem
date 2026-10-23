import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',

  standalone: true,

  imports: [
    RouterLink,
    RouterLinkActive
  ],

  templateUrl: './sidebar.html',

  styleUrl: './sidebar.css'
})
export class Sidebar {

  showLogoutConfirmation = false;

  constructor(
    private router: Router
  ) {}

  logout(): void {
    this.showLogoutConfirmation = true;
  }

  confirmLogout(): void {

    this.showLogoutConfirmation = false;

    // Clear future authentication/session data
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminUser');

    // For now redirect to login placeholder
    this.router.navigate(['/login']);
  }

  cancelLogout(): void {
    this.showLogoutConfirmation = false;
  }
}