import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  Customer,
  CustomerService,
  MedicalHistory,
  MedicalHistoryRequest
} from '../services/customer.service';

@Component({
  selector: 'app-customers',
  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl: './customers.html',
  styleUrl: './customers.css'
})
export class Customers implements OnInit {

  customers: Customer[] = [];

  selectedCustomer: Customer | null = null;

  loading = false;
  errorMessage = '';

  medicalHistory: MedicalHistory[] = [];
  medicalHistoryLoading = false;
  medicalHistoryError = '';

  showMedicalHistoryForm = false;
  editingMedicalHistoryId: number | null = null;

  medicalHistoryForm: MedicalHistoryRequest = {
    condition: '',
    diagnosisDate: undefined,
    treatment: '',
    doctorName: '',
    hospitalName: '',
    status: 'Active',
    notes: ''
  };

  constructor(
    private customerService: CustomerService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  // =========================================================
  // CUSTOMERS
  // =========================================================

  loadCustomers(): void {

    this.loading = true;
    this.errorMessage = '';
    this.selectedCustomer = null;

    this.customerService.getAllCustomers().subscribe({

      next: (data: Customer[]) => {

        console.log('CUSTOMERS API RESPONSE:', data);

        this.customers = data;

        this.loading = false;

        console.log('UPDATED CUSTOMERS:', this.customers);
        console.log('CUSTOMER COUNT:', this.customers.length);

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Failed to load customers:',
          error
        );

        this.customers = [];

        this.loading = false;

        this.errorMessage =
          'Unable to load customers. Please try again.';

        this.cdr.detectChanges();
      }
    });
  }

  viewCustomer(customer: Customer): void {

    this.customerService
      .getCustomerById(customer.id)
      .subscribe({

        next: (data: Customer) => {

          console.log(
            'CUSTOMER DETAILS:',
            data
          );

          this.selectedCustomer = data;

          this.showMedicalHistoryForm = false;

          this.editingMedicalHistoryId = null;

          this.loadMedicalHistory(data.id);

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Failed to load customer details:',
            error
          );
        }
      });
  }

  toggleCustomerStatus(customer: Customer): void {

    const currentStatus =
      customer.status === 'ACTIVE'
        ? 'ACTIVE'
        : 'INACTIVE';

    const newStatus =
      currentStatus === 'ACTIVE'
        ? 'INACTIVE'
        : 'ACTIVE';

    const actionText =
      newStatus === 'INACTIVE'
        ? 'deactivate'
        : 'activate';

    const confirmed =
      window.confirm(
        `Are you sure you want to ${actionText} ${customer.fullName}?`
      );

    if (!confirmed) {
      return;
    }

    this.customerService
      .updateCustomerStatus(
        customer.id,
        newStatus
      )
      .subscribe({

        next: (updatedCustomer: Customer) => {

          console.log(
            'UPDATED CUSTOMER STATUS:',
            updatedCustomer
          );

          const index =
            this.customers.findIndex(
              item =>
                item.id === updatedCustomer.id
            );

          if (index !== -1) {

            this.customers[index] =
              updatedCustomer;
          }

          if (
            this.selectedCustomer &&
            this.selectedCustomer.id ===
              updatedCustomer.id
          ) {

            this.selectedCustomer =
              updatedCustomer;
          }

          this.cdr.detectChanges();

          window.alert(
            `Customer status changed to ${updatedCustomer.status}.`
          );
        },

        error: (error) => {

          console.error(
            'Failed to update customer status:',
            error
          );

          window.alert(
            'Unable to update customer status. Please try again.'
          );
        }
      });
  }

  // =========================================================
  // MEDICAL HISTORY
  // =========================================================

  loadMedicalHistory(
    customerId: number
  ): void {

    this.medicalHistoryLoading = true;

    this.medicalHistoryError = '';

    this.medicalHistory = [];

    this.customerService
      .getMedicalHistory(customerId)
      .subscribe({

        next: (data: MedicalHistory[]) => {

          console.log(
            'MEDICAL HISTORY:',
            data
          );

          this.medicalHistory = data;

          this.medicalHistoryLoading = false;

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Failed to load medical history:',
            error
          );

          this.medicalHistory = [];

          this.medicalHistoryLoading = false;

          this.medicalHistoryError =
            'Unable to load medical history.';

          this.cdr.detectChanges();
        }
      });
  }

  openAddMedicalHistory(): void {

    this.editingMedicalHistoryId = null;

    this.medicalHistoryForm = {

      condition: '',

      diagnosisDate: undefined,

      treatment: '',

      doctorName: '',

      hospitalName: '',

      status: 'Active',

      notes: ''
    };

    this.showMedicalHistoryForm = true;

    this.cdr.detectChanges();
  }

  openEditMedicalHistory(
    history: MedicalHistory
  ): void {

    this.editingMedicalHistoryId =
      history.id;

    this.medicalHistoryForm = {

      condition:
        history.condition || '',

      diagnosisDate:
        history.diagnosisDate || undefined,

      treatment:
        history.treatment || '',

      doctorName:
        history.doctorName || '',

      hospitalName:
        history.hospitalName || '',

      status:
        history.status || 'Active',

      notes:
        history.notes || ''
    };

    this.showMedicalHistoryForm = true;

    this.cdr.detectChanges();
  }

  cancelMedicalHistoryForm(): void {

    this.showMedicalHistoryForm = false;

    this.editingMedicalHistoryId = null;

    this.cdr.detectChanges();
  }

  saveMedicalHistory(): void {

    if (!this.selectedCustomer) {

      window.alert(
        'Please select a customer first.'
      );

      return;
    }

    if (
      !this.medicalHistoryForm.condition ||
      !this.medicalHistoryForm.condition.trim()
    ) {

      window.alert(
        'Medical condition is required.'
      );

      return;
    }

    /*
     * Important:
     * If diagnosisDate is empty, send undefined
     * instead of empty string.
     *
     * Spring Boot LocalDate accepts null/omitted value,
     * but empty string can cause JSON date parsing error.
     */

    const data: MedicalHistoryRequest = {

      condition:
        this.medicalHistoryForm.condition.trim(),

      diagnosisDate:
        this.medicalHistoryForm.diagnosisDate
          ? this.medicalHistoryForm.diagnosisDate
          : undefined,

      treatment:
        this.medicalHistoryForm.treatment
          ? this.medicalHistoryForm.treatment.trim()
          : '',

      doctorName:
        this.medicalHistoryForm.doctorName
          ? this.medicalHistoryForm.doctorName.trim()
          : '',

      hospitalName:
        this.medicalHistoryForm.hospitalName
          ? this.medicalHistoryForm.hospitalName.trim()
          : '',

      status:
        this.medicalHistoryForm.status || 'Active',

      notes:
        this.medicalHistoryForm.notes
          ? this.medicalHistoryForm.notes.trim()
          : ''
    };

    // =======================================================
    // UPDATE MEDICAL HISTORY
    // =======================================================

    if (
      this.editingMedicalHistoryId !== null
    ) {

      this.customerService
        .updateMedicalHistory(
          this.editingMedicalHistoryId,
          data
        )
        .subscribe({

          next: (updatedHistory) => {

            console.log(
              'UPDATED MEDICAL HISTORY:',
              updatedHistory
            );

            const index =
              this.medicalHistory.findIndex(
                item =>
                  item.id ===
                  updatedHistory.id
              );

            if (index !== -1) {

              this.medicalHistory[index] =
                updatedHistory;
            }

            this.showMedicalHistoryForm =
              false;

            this.editingMedicalHistoryId =
              null;

            this.cdr.detectChanges();

            window.alert(
              'Medical history updated successfully.'
            );
          },

          error: (error) => {

            console.error(
              'Failed to update medical history:',
              error
            );

            window.alert(
              'Unable to update medical history. Please try again.'
            );
          }
        });

      return;
    }

    // =======================================================
    // ADD MEDICAL HISTORY
    // =======================================================

    this.customerService
      .addMedicalHistory(
        this.selectedCustomer.id,
        data
      )
      .subscribe({

        next: (newHistory) => {

          console.log(
            'ADDED MEDICAL HISTORY:',
            newHistory
          );

          this.medicalHistory = [

            newHistory,

            ...this.medicalHistory
          ];

          this.showMedicalHistoryForm =
            false;

          this.editingMedicalHistoryId =
            null;

          this.cdr.detectChanges();

          window.alert(
            'Medical history added successfully.'
          );
        },

        error: (error) => {

          console.error(
            'Failed to add medical history:',
            error
          );

          window.alert(
            'Unable to add medical history. Please try again.'
          );
        }
      });
  }

  deleteMedicalHistory(
    history: MedicalHistory
  ): void {

    const confirmed =
      window.confirm(
        `Are you sure you want to delete "${history.condition}" medical history?`
      );

    if (!confirmed) {
      return;
    }

    this.customerService
      .deleteMedicalHistory(history.id)
      .subscribe({

        next: () => {

          console.log(
            'MEDICAL HISTORY DELETED:',
            history.id
          );

          this.medicalHistory =
            this.medicalHistory.filter(
              item =>
                item.id !== history.id
            );

          this.cdr.detectChanges();

          window.alert(
            'Medical history deleted successfully.'
          );
        },

        error: (error) => {

          console.error(
            'Failed to delete medical history:',
            error
          );

          window.alert(
            'Unable to delete medical history. Please try again.'
          );
        }
      });
  }

  // =========================================================
  // CLOSE CUSTOMER DETAILS
  // =========================================================

  closeDetails(): void {

    this.selectedCustomer = null;

    this.medicalHistory = [];

    this.showMedicalHistoryForm = false;

    this.editingMedicalHistoryId = null;

    this.cdr.detectChanges();
  }

  // =========================================================
  // RETRY
  // =========================================================

  retry(): void {

    this.loadCustomers();
  }
}