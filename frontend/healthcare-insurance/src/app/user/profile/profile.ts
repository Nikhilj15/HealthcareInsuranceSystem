import { CommonModule } from '@angular/common';
import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { finalize } from 'rxjs';

interface ProfileResponse {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  dateOfBirth?: string;
  gender?: string;
  address?: string;
  role?: string;
  status?: string;
  createdAt?: string;
}

interface UpdateProfileRequest {
  fullName: string;
  phone: string;
  dateOfBirth?: string;
  gender?: string;
  address?: string;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {

  private apiUrl = 'http://localhost:8080/api/users';

  isEditing = false;

  savedMessage = '';

  errorMessage = '';

  loading = true;

  saving = false;

  profile: ProfileResponse = {
    id: 0,
    fullName: '',
    email: '',
    phone: '',
    dateOfBirth: '',
    gender: '',
    address: '',
    role: '',
    status: '',
    createdAt: ''
  };

  private originalProfile: ProfileResponse | null = null;


  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}


  // ==========================================
  // INIT
  // ==========================================

  ngOnInit(): void {

    console.log('Profile component initialized');

    this.loadProfile();
  }


  // ==========================================
  // LOAD PROFILE
  // ==========================================

  loadProfile(): void {

    this.loading = true;

    this.errorMessage = '';

    console.log('Calling profile API...');

    this.http
      .get<ProfileResponse>(
        `${this.apiUrl}/me`
      )
      .pipe(
        finalize(() => {

          this.loading = false;

          console.log(
            'Loading state:',
            this.loading
          );

          this.cdr.detectChanges();
        })
      )
      .subscribe({

        next: (response: ProfileResponse) => {

          console.log(
            'Profile API response:',
            response
          );

          this.profile = {
            id: response.id,
            fullName: response.fullName ?? '',
            email: response.email ?? '',
            phone: response.phone ?? '',
            dateOfBirth:
              response.dateOfBirth ?? '',
            gender:
              response.gender ?? '',
            address:
              response.address ?? '',
            role:
              response.role ?? '',
            status:
              response.status ?? '',
            createdAt:
              response.createdAt ?? ''
          };


          this.originalProfile = {
            ...this.profile
          };


          this.savedMessage = '';

          this.errorMessage = '';


          console.log(
            'Profile assigned:',
            this.profile
          );


          /*
           * IMPORTANT:
           * Force Angular UI refresh
           */
          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Profile API error:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to load profile. Please try again.';

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // EDIT PROFILE
  // ==========================================

  editProfile(): void {

    this.isEditing = true;

    this.savedMessage = '';

    this.errorMessage = '';

    this.originalProfile = {
      ...this.profile
    };

    this.cdr.detectChanges();
  }


  // ==========================================
  // CANCEL EDIT
  // ==========================================

  cancelEdit(): void {

    if (this.originalProfile) {

      this.profile = {
        ...this.originalProfile
      };
    }

    this.isEditing = false;

    this.savedMessage = '';

    this.errorMessage = '';

    this.cdr.detectChanges();
  }


  // ==========================================
  // SAVE PROFILE
  // ==========================================

  saveChanges(): void {

    this.savedMessage = '';

    this.errorMessage = '';


    // ==============================
    // FULL NAME VALIDATION
    // ==============================

    if (
      !this.profile.fullName ||
      this.profile.fullName.trim().length < 2
    ) {

      this.errorMessage =
        'Full name must contain at least 2 characters.';

      return;
    }


    // ==============================
    // PHONE VALIDATION
    // ==============================

    if (
      !this.profile.phone ||
      !/^[6-9][0-9]{9}$/.test(
        this.profile.phone.trim()
      )
    ) {

      this.errorMessage =
        'Please enter a valid 10 digit Indian mobile number.';

      return;
    }


    // ==============================
    // DOB VALIDATION
    // ==============================

    if (
      this.profile.dateOfBirth &&
      new Date(
        this.profile.dateOfBirth
      ) > new Date()
    ) {

      this.errorMessage =
        'Date of birth cannot be in the future.';

      return;
    }


    const request: UpdateProfileRequest = {

      fullName:
        this.profile.fullName.trim(),

      phone:
        this.profile.phone.trim(),

      dateOfBirth:
        this.profile.dateOfBirth || undefined,

      gender:
        this.profile.gender
          ? this.profile.gender.trim()
          : undefined,

      address:
        this.profile.address
          ? this.profile.address.trim()
          : undefined
    };


    this.saving = true;


    console.log(
      'Updating profile:',
      request
    );


    this.http
      .put<ProfileResponse>(
        `${this.apiUrl}/me`,
        request
      )
      .pipe(
        finalize(() => {

          this.saving = false;

          this.cdr.detectChanges();
        })
      )
      .subscribe({

        next: (response: ProfileResponse) => {

          console.log(
            'Profile update response:',
            response
          );


          this.profile = {

            id: response.id,

            fullName:
              response.fullName ?? '',

            email:
              response.email ?? '',

            phone:
              response.phone ?? '',

            dateOfBirth:
              response.dateOfBirth ?? '',

            gender:
              response.gender ?? '',

            address:
              response.address ?? '',

            role:
              response.role ?? '',

            status:
              response.status ?? '',

            createdAt:
              response.createdAt ?? ''
          };


          this.originalProfile = {
            ...this.profile
          };


          this.isEditing = false;

          this.errorMessage = '';

          this.savedMessage =
            'Profile updated successfully.';


          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Profile update failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to update profile. Please try again.';

          this.cdr.detectChanges();
        }

      });
  }
}