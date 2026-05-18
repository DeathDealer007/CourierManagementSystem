import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { TrackingService } from '../../core/services/tracking.service';
import { Tracking } from '../../core/models/tracking.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SpinnerComponent } from '../../shared/components/spinner.component';
import { ActivatedRoute } from '@angular/router';
import { DeliveryService } from '../../core/services/delivery.service';
import { Delivery } from '../../core/models/delivery.model';

@Component({
  selector: 'app-tracking-page',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent],
  template: `
    <div class="max-w-4xl mx-auto px-4 py-8">
      <div class="text-center mb-12">
        <h1 class="text-4xl font-extrabold text-slate-900 mb-4">Track Your Shipment</h1>
        <p class="text-slate-600">Enter your tracking number below to see real-time updates.</p>
        
        <div class="mt-8 flex max-w-lg mx-auto">
          <input [(ngModel)]="trackingNumber" type="text" placeholder="Enter Tracking Number" 
                 class="input-field rounded-r-none border-r-0 focus:ring-0">
          <button (click)="onTrack()" class="btn-primary rounded-l-none px-8">Track</button>
        </div>

        <div *ngIf="deliveryInfo" class="mt-8 p-6 glass-morphism rounded-2xl border-none shadow-sm flex flex-wrap justify-center gap-8 animate-in fade-in duration-500">
          <div *ngIf="deliveryInfo.createdAt" class="text-center">
            <p class="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1">Created At</p>
            <p class="text-sm font-bold text-slate-700">{{ deliveryInfo.createdAt | date:'medium' }}</p>
          </div>
          <div *ngIf="deliveryInfo.deliveredAt" class="text-center">
            <p class="text-xs font-semibold text-green-400 uppercase tracking-wider mb-1">Delivered At</p>
            <p class="text-sm font-bold text-green-700">{{ deliveryInfo.deliveredAt | date:'medium' }}</p>
          </div>
          <div *ngIf="deliveryInfo.expectedDeliveryDate && !deliveryInfo.deliveredAt" class="text-center">
            <p class="text-xs font-semibold text-primary-400 uppercase tracking-wider mb-1">Expected Delivery</p>
            <p class="text-sm font-bold text-primary-700">{{ deliveryInfo.expectedDeliveryDate | date:'mediumDate' }}</p>
          </div>
        </div>
      </div>

      <div *ngIf="loading" class="flex justify-center py-12">
        <app-spinner></app-spinner>
      </div>

      <div *ngIf="!loading && history.length > 0" class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
        <div class="glass-morphism p-8 rounded-3xl relative overflow-hidden">
          <!-- Delivered Badge -->
          <div *ngIf="isDelivered()" class="absolute top-0 right-0 bg-green-500 text-white px-6 py-2 rounded-bl-2xl font-bold text-sm shadow-sm">
            ✓ DELIVERED
          </div>

          <div class="absolute left-12 top-0 bottom-0 w-0.5" [class.bg-green-200]="isDelivered()" [class.bg-slate-200]="!isDelivered()"></div>
          
          <div *ngFor="let item of history; let i = index" class="relative pl-16 mb-12 last:mb-0">
            <div class="absolute left-[3.15rem] top-1 w-6 h-6 rounded-full border-4 border-white shadow-sm z-10"
                 [class.bg-green-500]="isDelivered()" [class.bg-primary-600]="!isDelivered()"></div>
            
            <div class="glass-morphism p-6 rounded-2xl border-none shadow-sm hover:shadow-md transition-all duration-300"
                 [class.bg-green-50]="isDelivered() && i === 0"
                 [class.border-l-4]="isDelivered() && i === 0"
                 [class.border-green-500]="isDelivered() && i === 0">
              <div class="flex justify-between items-start mb-2">
                <h3 class="text-lg font-bold" [class.text-green-900]="isDelivered()" [class.text-slate-900]="!isDelivered()">
                  {{ item.status }}
                </h3>
                <span class="text-sm text-slate-400">{{ item.timestamp | date:'medium' }}</span>
              </div>
              <p class="text-slate-600 mb-1"><span class="font-medium">Location:</span> {{ item.location }}</p>
              <p class="text-slate-500 text-sm">{{ item.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <div *ngIf="!loading && searched && history.length === 0" class="text-center py-12 glass-morphism rounded-3xl">
        <p class="text-slate-500">No tracking information found for this number.</p>
      </div>
    </div>
  `
})
export class TrackingPageComponent implements OnDestroy {
  private trackingService = inject(TrackingService);
  private deliveryService = inject(DeliveryService);
  private route = inject(ActivatedRoute);

  trackingNumber = '';
  history: Tracking[] = [];
  deliveryInfo: Delivery | null = null;
  loading = false;
  searched = false;

  constructor() {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.trackingNumber = params['id'];
        this.onTrack();
      }
    });
  }

  private pollInterval: any;

  ngOnDestroy() {
    if (this.pollInterval) clearInterval(this.pollInterval);
  }

  onTrack() {
    if (!this.trackingNumber) return;
    
    this.loading = true;
    this.searched = true;
    this.fetchHistory();

    // Polling for real-time updates
    if (this.pollInterval) clearInterval(this.pollInterval);
    this.pollInterval = setInterval(() => {
      this.fetchHistory(true);
    }, 5000);
  }

  fetchHistory(isPolling = false) {
    const cleanId = this.trackingNumber.trim();
    
    // Fetch delivery details for timestamps
    if (!isPolling) {
      this.deliveryService.getDeliveryByTracking(cleanId).subscribe({
        next: (data) => this.deliveryInfo = data,
        error: () => this.deliveryInfo = null
      });
    }

    this.trackingService.getTrackingHistory(cleanId).subscribe({
      next: (data) => {
        this.history = data.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
        this.loading = false;
        
        // Stop polling if delivered or failed
        const latest = this.history[0];
        if (latest && (latest.status === 'DELIVERED' || latest.status === 'FAILED')) {
          clearInterval(this.pollInterval);
        }
      },
      error: () => {
        if (!isPolling) {
          this.loading = false;
          this.history = [];
        }
      }
    });
  }
  isDelivered(): boolean {
    return this.history.length > 0 && this.history[0].status === 'DELIVERED';
  }
}
