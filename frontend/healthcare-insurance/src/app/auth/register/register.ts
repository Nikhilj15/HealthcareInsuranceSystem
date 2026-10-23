import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  AuthService,
  RegisterRequest
} from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {

  currentStep = 1;

  fullName = '';
  email = '';
  phone = '';
  dateOfBirth = '';
  age = 0;
  gender = '';
  address = '';

  password = '';
  confirmPassword = '';

  diagnosis = '';
  treatment = '';
  doctor = '';
  hospital = '';
  medicalDate = '';
  medicalNotes = '';
  bloodGroup = '';
  existingDiseases = '';
  allergies = '';
  previousSurgeries = '';
  currentMedication = '';

  emailExists = false;

  isSubmitting = false;
  errorMessage = '';

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  checkEmail(): void {

    this.emailExists = false;
    this.errorMessage = '';
  }

  calculateAge(): void {

    if (!this.dateOfBirth) {
      this.age = 0;
      return;
    }

    const birthDate =
      new Date(this.dateOfBirth);

    const today =
      new Date();

    let calculatedAge =
      today.getFullYear() -
      birthDate.getFullYear();

    const monthDifference =
      today.getMonth() -
      birthDate.getMonth();

    if (
      monthDifference < 0 ||
      (
        monthDifference === 0 &&
        today.getDate() < birthDate.getDate()
      )
    ) {
      calculatedAge--;
    }

    this.age =
      calculatedAge > 0
        ? calculatedAge
        : 0;
  }

  nextStep(): void {

    this.errorMessage = '';

    const name =
      this.fullName.trim();

    const email =
      this.email.trim();

    const phone =
      this.phone.trim();

    const address =
      this.address.trim();

    // -----------------------------------------
    // Full name
    // -----------------------------------------

    if (!name) {
      alert('Please enter your full name.');
      return;
    }

    if (name.length < 2) {
      alert(
        'Full name must contain at least 2 characters.'
      );
      return;
    }

    if (name.length > 100) {
      alert(
        'Full name cannot exceed 100 characters.'
      );
      return;
    }

    // -----------------------------------------
    // Email
    // -----------------------------------------

    if (!email) {
      alert('Please enter your email.');
      return;
    }

    const emailPattern =
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(email)) {
      alert(
        'Please enter a valid email address.'
      );
      return;
    }

    if (email.length > 150) {
      alert(
        'Email cannot exceed 150 characters.'
      );
      return;
    }

    if (this.emailExists) {
      alert(
        'This email is already registered.'
      );
      return;
    }

    // -----------------------------------------
    // Phone
    // -----------------------------------------

    if (!phone) {
      alert('Please enter your phone number.');
      return;
    }

    if (!/^[6-9][0-9]{9}$/.test(phone)) {
      alert(
        'Please enter a valid 10 digit Indian mobile number.'
      );
      return;
    }

    // -----------------------------------------
    // Date of birth
    // -----------------------------------------

    if (!this.dateOfBirth) {
      alert('Please select your date of birth.');
      return;
    }

    const selectedDate =
      new Date(this.dateOfBirth);

    const today =
      new Date();

    today.setHours(0, 0, 0, 0);
    selectedDate.setHours(0, 0, 0, 0);

    if (selectedDate > today) {
      alert(
        'Date of birth cannot be in the future.'
      );
      return;
    }

    this.calculateAge();

    if (this.age < 1 || this.age > 120) {
      alert(
        'Please enter a valid date of birth.'
      );
      return;
    }

    // -----------------------------------------
    // Gender
    // -----------------------------------------

    if (!this.gender) {
      alert('Please select your gender.');
      return;
    }

    if (
      !['Male', 'Female', 'Other']
        .includes(this.gender)
    ) {
      alert(
        'Please select a valid gender.'
      );
      return;
    }

    // -----------------------------------------
    // Address
    // -----------------------------------------

    if (!address) {
      alert('Please enter your address.');
      return;
    }

    if (address.length > 500) {
      alert(
        'Address cannot exceed 500 characters.'
      );
      return;
    }

    // -----------------------------------------
    // Password
    // -----------------------------------------

    if (!this.password) {
      alert('Please enter a password.');
      return;
    }

    if (this.password.length < 6) {
      alert(
        'Password must contain at least 6 characters.'
      );
      return;
    }

    if (this.password.length > 100) {
      alert(
        'Password cannot exceed 100 characters.'
      );
      return;
    }

    if (!this.confirmPassword) {
      alert(
        'Please confirm your password.'
      );
      return;
    }

    if (
      this.password !==
      this.confirmPassword
    ) {
      alert(
        'Passwords do not match.'
      );
      return;
    }

    this.currentStep = 2;
  }

  previousStep(): void {

    this.currentStep = 1;
    this.errorMessage = '';
  }

 register(): void {

  this.errorMessage = '';

  if (!this.password) {
    alert('Please enter a password.');
    return;
  }

  if (this.password.length < 6) {
    alert(
      'Password must contain at least 6 characters.'
    );
    return;
  }

  if (
    this.password !==
    this.confirmPassword
  ) {
    alert(
      'Passwords do not match.'
    );
    return;
  }

  if (this.medicalDate) {

    const treatmentDate =
      new Date(this.medicalDate);

    const today =
      new Date();

    today.setHours(0, 0, 0, 0);
    treatmentDate.setHours(0, 0, 0, 0);

    if (treatmentDate > today) {

      alert(
        'Medical treatment date cannot be in the future.'
      );

      return;
    }
  }

  if (this.isSubmitting) {
    return;
  }

  this.isSubmitting = true;

  const request: RegisterRequest = {

    fullName:
      this.fullName.trim(),

    email:
      this.email.trim().toLowerCase(),

    phone:
      this.phone.trim(),

    password:
      this.password,

    dateOfBirth:
      this.dateOfBirth,

    gender:
      this.gender,

    address:
      this.address.trim()
  };

  this.authService
    .register(request)
    .subscribe({

      next: (response: string) => {

        this.isSubmitting = false;

        console.log(
          'REGISTRATION SUCCESS:',
          response
        );

        alert(
          'Registration successful.'
        );

        this.router.navigate([
          '/login'
        ]);
      },

      error: (error) => {

        this.isSubmitting = false;

        console.error(
          'REGISTRATION ERROR:',
          error
        );

        if (error.status === 409) {

          this.errorMessage =
            'This email is already registered.';

          alert(
            this.errorMessage
          );

          return;
        }

        if (error.status === 400) {

          if (
            error.error?.message
          ) {

            this.errorMessage =
              error.error.message;

          } else {

            this.errorMessage =
              'Please check your registration details.';
          }

          alert(
            this.errorMessage
          );

          return;
        }

        if (error.status === 0) {

          this.errorMessage =
            'Unable to connect to server. Please make sure the backend is running.';

          alert(
            this.errorMessage
          );

          return;
        }

        this.errorMessage =
          'Registration failed. Please try again.';

        alert(
          this.errorMessage
        );
      }
    });
}
}