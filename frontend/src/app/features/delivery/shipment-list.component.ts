import { Component, OnInit, inject } from '@angular/core';
import { DeliveryService } from '../../core/services/delivery.service';
import { Delivery } from '../../core/models/delivery.model';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SpinnerComponent } from '../../shared/components/spinner.component';

@Component({
  selector: 'app-shipment-list',
  standalone: true,
  imports: [CommonModule, RouterLink, SpinnerComponent],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="flex justify-between items-center mb-8">
        <h1 class="text-3xl font-bold text-slate-900">My Shipments</h1>
        <a routerLink="/delivery/create" class="btn-primary">New Shipment</a>
      </div>

      <div *ngIf="loading" class="flex justify-center py-12">
        <app-spinner></app-spinner>
      </div>

      <div *ngIf="!loading && deliveries.length === 0" class="text-center py-12 glass-morphism rounded-2xl">
        <p class="text-slate-500">No shipments found. Start by creating one!</p>
      </div>

      <div *ngIf="!loading && deliveries.length > 0" class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <div *ngFor="let delivery of deliveries" class="glass-morphism rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow">
          <div class="flex justify-between items-start mb-4">
            <span class="text-xs font-semibold px-2 py-1 rounded-full bg-primary-100 text-primary-700">
              {{ delivery.status }}
            </span>
            <span class="text-sm font-medium text-slate-500">Tracking: {{ delivery.trackingNumber }}</span>
          </div>
          <h3 class="text-lg font-bold text-slate-900 mb-2">{{ delivery.serviceType }} Service</h3>
          <p class="text-sm text-slate-600 mb-1">Amount: \${{ delivery.totalAmount }}</p>
          <p *ngIf="delivery.expectedDeliveryDate" class="text-sm text-primary-600 font-medium mb-4">
            Expected: {{ delivery.expectedDeliveryDate | date:'mediumDate' }}
          </p>
          <div class="border-t border-slate-100 pt-4 flex justify-between items-center">
            <div class="flex space-x-4">
              <a [routerLink]="['/tracking', delivery.trackingNumber]" class="text-primary-600 text-sm font-bold hover:text-primary-800 transition-colors">Track Status</a>
            </div>
            <button class="text-slate-400 hover:text-slate-600">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path d="M10 6a2 2 0 110-4 2 2 0 010 4zM10 12a2 2 0 110-4 2 2 0 010 4zM10 18a2 2 0 110-4 2 2 0 010 4z" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ShipmentListComponent implements OnInit {
  private deliveryService = inject(DeliveryService);
  deliveries: Delivery[] = [];
  loading = true;

  ngOnInit() {
    this.loadDeliveries();
  }

  loadDeliveries() {
    this.loading = true;
    this.deliveryService.getMyDeliveries().subscribe({
      next: (data) => {
        this.deliveries = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
