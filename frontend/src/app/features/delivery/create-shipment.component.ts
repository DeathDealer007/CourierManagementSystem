import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { Router } from '@angular/router';
import { DeliveryService } from '../../core/services/delivery.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-shipment',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  template: `
    <div class="max-w-4xl mx-auto px-4 py-8">
      <h1 class="text-3xl font-bold text-slate-900 mb-8">Create New Shipment</h1>
      
      <form [formGroup]="deliveryForm" (ngSubmit)="onSubmit()" class="space-y-8">
        <!-- Parcel Details -->
        <div class="glass-morphism p-6 rounded-2xl">
          <h2 class="text-xl font-bold text-slate-800 mb-4">Parcel Information</h2>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4" formGroupName="parcel">
            <div>
              <label class="block text-sm font-medium text-slate-700">Weight (kg)</label>
              <input formControlName="weight" type="number" class="input-field mt-1">
            </div>
            <div>
              <label class="block text-sm font-medium text-slate-700">Dimensions (LxWxH)</label>
              <input formControlName="dimensions" type="text" class="input-field mt-1" placeholder="30x20x10 cm">
            </div>
            <div class="flex items-center mt-6">
              <input formControlName="fragile" type="checkbox" class="h-4 w-4 text-primary-600 rounded">
              <label class="ml-2 text-sm text-slate-700">Fragile</label>
            </div>
          </div>
        </div>

        <!-- Service & Status -->
        <div class="glass-morphism p-6 rounded-2xl">
          <h2 class="text-xl font-bold text-slate-800 mb-4">Service Selection</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700">Service Type</label>
              <select formControlName="serviceType" class="input-field mt-1">
                <option value="STANDARD">Standard</option>
                <option value="EXPRESS">Express</option>
                <option value="OVERNIGHT">Overnight</option>
                <option value="SAME_DAY">Same Day</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-slate-700">Estimated Amount ($)</label>
              <input formControlName="totalAmount" type="number" class="input-field mt-1" readonly>
            </div>
          </div>
        </div>

        <!-- Addresses -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div class="glass-morphism p-6 rounded-2xl" *ngFor="let addr of [0, 1]; let i = index">
            <h2 class="text-xl font-bold text-slate-800 mb-4">{{ i === 0 ? 'Pickup (Sender)' : 'Destination (Receiver)' }}</h2>
            <div [formGroup]="getAddressGroup(i)" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-slate-700">Name</label>
                <input formControlName="name" type="text" class="input-field mt-1">
              </div>
              <div>
                <label class="block text-sm font-medium text-slate-700">Phone</label>
                <input formControlName="phone" type="text" class="input-field mt-1">
              </div>
              <div>
                <label class="block text-sm font-medium text-slate-700">Address</label>
                <input formControlName="address" type="text" class="input-field mt-1">
              </div>
              <div>
                <label class="block text-sm font-medium text-slate-700">City</label>
                <input formControlName="city" type="text" class="input-field mt-1">
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-end space-x-4">
          <button type="button" (click)="cancel()" class="px-6 py-3 text-slate-600 hover:text-slate-800 transition-colors">Cancel</button>
          <button type="submit" [disabled]="deliveryForm.invalid || loading" class="btn-primary px-8 py-3 rounded-xl">
            Create Shipment
          </button>
        </div>
      </form>
    </div>
  `
})
export class CreateShipmentComponent {
  private fb = inject(FormBuilder);
  private deliveryService = inject(DeliveryService);
  private router = inject(Router);

  loading = false;

  deliveryForm = this.fb.group({
    trackingNumber: ['TRACK' + Math.random().toString(36).substring(7).toUpperCase()],
    userId: [null], // Set by backend using X-User-Id header
    serviceType: ['STANDARD', Validators.required],
    status: ['CREATED'],
    totalAmount: [50.00], // Mock calculation
    parcel: this.fb.group({
      weight: [1.0, [Validators.required, Validators.min(0.1)]],
      dimensions: ['', Validators.required],
      fragile: [false]
    }),
    addresses: this.fb.array([
      this.fb.group({ type: ['SENDER'], name: ['', Validators.required], phone: ['', Validators.required], address: ['', Validators.required], city: ['', Validators.required] }),
      this.fb.group({ type: ['RECEIVER'], name: ['', Validators.required], phone: ['', Validators.required], address: ['', Validators.required], city: ['', Validators.required] })
    ])
  });

  constructor() {
    this.deliveryForm.get('parcel.weight')?.valueChanges.subscribe(() => this.calculateAmount());
    this.deliveryForm.get('parcel.fragile')?.valueChanges.subscribe(() => this.calculateAmount());
    this.deliveryForm.get('serviceType')?.valueChanges.subscribe(() => this.calculateAmount());
  }

  calculateAmount() {
    const weight = this.deliveryForm.get('parcel.weight')?.value || 0;
    const isFragile = this.deliveryForm.get('parcel.fragile')?.value || false;
    const serviceType = this.deliveryForm.get('serviceType')?.value || 'STANDARD';
    
    let rate = 50;
    switch(serviceType) {
      case 'EXPRESS': rate = isFragile ? 120 : 100; break;
      case 'OVERNIGHT': rate = isFragile ? 200 : 180; break;
      case 'SAME_DAY': rate = isFragile ? 300 : 280; break;
      default: rate = isFragile ? 70 : 50; // STANDARD
    }
    
    this.deliveryForm.patchValue({ totalAmount: weight * rate });
  }

  getAddressGroup(index: number) {
    return (this.deliveryForm.get('addresses') as FormArray).at(index) as any;
  }

  onSubmit() {
    if (this.deliveryForm.valid) {
      this.loading = true;
      this.deliveryService.createDelivery(this.deliveryForm.value as any).subscribe({
        next: () => {
          this.router.navigate(['/delivery']);
        },
        error: () => {
          this.loading = false;
          alert('Failed to create shipment.');
        }
      });
    }
  }

  cancel() {
    this.router.navigate(['/delivery']);
  }
}
