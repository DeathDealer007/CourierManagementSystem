import { Component, OnInit, inject } from '@angular/core';
import { AdminService } from '../../core/services/admin.service';
import { TrackingService } from '../../core/services/tracking.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SpinnerComponent } from '../../shared/components/spinner.component';

@Component({
  selector: 'app-admin-tracking',
  standalone: true,
  imports: [CommonModule, FormsModule, SpinnerComponent],
  template: `
    <div class="space-y-8 animate-in fade-in duration-700">
      <!-- Header Section -->
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-6 mb-8">
        <div>
          <h1 class="text-4xl font-extrabold text-slate-900 tracking-tight">Shipment Command Center</h1>
          <p class="text-slate-500 mt-2 text-lg">Monitor and manage tracking across the entire logistics network.</p>
        </div>
        
        <div class="flex flex-col sm:flex-row gap-3">
          <div class="relative group">
            <input [(ngModel)]="searchQuery" (keyup.enter)="onSearch()" type="text" 
                   placeholder="Search Tracking ID..." 
                   class="w-full sm:w-72 pl-12 pr-4 py-3 bg-white border border-slate-200 rounded-2xl focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all outline-none shadow-sm group-hover:shadow-md">
            <svg class="absolute left-4 top-3.5 h-5 w-5 text-slate-400 group-focus-within:text-primary-500 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <button *ngIf="searchQuery" (click)="clearSearch()" class="absolute right-3 top-3 text-slate-300 hover:text-slate-500">
              <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
            </button>
          </div>
          <button (click)="onSearch()" class="btn-primary px-8 py-3 rounded-2xl shadow-lg shadow-primary-500/20 hover:shadow-primary-500/30 transition-all">
            Find Shipment
          </button>
        </div>
      </div>

      <div *ngIf="loading" class="flex flex-col items-center justify-center py-20 bg-white/50 rounded-3xl border border-slate-100">
        <app-spinner></app-spinner>
        <p class="mt-4 text-slate-400 font-medium animate-pulse">Syncing logistics data...</p>
      </div>

      <div *ngIf="!loading" class="grid grid-cols-1 xl:grid-cols-3 gap-8">
        <!-- Main Shipment List -->
        <div class="xl:col-span-2 space-y-6">
          <div class="glass-morphism rounded-[2rem] overflow-hidden border border-white/40 shadow-xl shadow-slate-200/50">
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-slate-100">
                <thead class="bg-slate-50/80">
                  <tr>
                    <th class="px-8 py-5 text-left text-xs font-bold text-slate-400 uppercase tracking-widest">Tracking Number</th>
                    <th class="px-8 py-5 text-left text-xs font-bold text-slate-400 uppercase tracking-widest">Service Type</th>
                    <th class="px-8 py-5 text-left text-xs font-bold text-slate-400 uppercase tracking-widest">Current Status</th>
                    <th class="px-8 py-5 text-left text-xs font-bold text-slate-400 uppercase tracking-widest">Revenue</th>
                    <th class="px-8 py-5 text-right text-xs font-bold text-slate-400 uppercase tracking-widest">Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-50 bg-white/40">
                  <tr *ngFor="let d of filteredDeliveries" 
                      (click)="selectDelivery(d)"
                      class="hover:bg-primary-50/50 transition-all cursor-pointer group">
                    <td class="px-8 py-5 whitespace-nowrap">
                      <div class="flex items-center">
                        <div class="w-2 h-2 rounded-full mr-3" [class]="getStatusDotClass(d.status)"></div>
                        <span class="text-sm font-bold text-slate-800 group-hover:text-primary-600 transition-colors">{{ d.trackingNumber }}</span>
                      </div>
                    </td>
                    <td class="px-8 py-5 whitespace-nowrap">
                      <span class="text-sm font-medium text-slate-600 bg-slate-100 px-3 py-1 rounded-lg">{{ d.serviceType }}</span>
                    </td>
                    <td class="px-8 py-5 whitespace-nowrap">
                      <span [class]="getStatusClass(d.status)" class="px-3 py-1 rounded-xl text-xs font-bold tracking-wide shadow-sm">
                        {{ d.status.replace('_', ' ') }}
                      </span>
                    </td>
                    <td class="px-8 py-5 whitespace-nowrap">
                      <span class="text-sm font-bold text-slate-900">\${{ d.totalAmount.toFixed(2) }}</span>
                    </td>
                    <td class="px-8 py-5 whitespace-nowrap text-right">
                      <button class="p-2 rounded-xl text-slate-400 group-hover:text-primary-500 group-hover:bg-primary-50 transition-all">
                        <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" /></svg>
                      </button>
                    </td>
                  </tr>
                  <tr *ngIf="filteredDeliveries.length === 0">
                    <td colspan="5" class="px-8 py-20 text-center">
                      <div class="flex flex-col items-center">
                        <div class="bg-slate-50 p-4 rounded-full mb-4">
                          <svg class="h-10 w-10 text-slate-300" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
                        </div>
                        <h3 class="text-lg font-bold text-slate-900">No shipments found</h3>
                        <p class="text-slate-500">Try adjusting your search criteria or tracking ID.</p>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- Sidebar: Tracking History -->
        <div class="space-y-6">
          <div *ngIf="!selectedDelivery" class="h-full flex flex-col items-center justify-center p-10 border-2 border-dashed border-slate-200 rounded-[2rem] text-center bg-slate-50/30">
            <div class="bg-white p-6 rounded-3xl shadow-sm mb-6">
              <svg class="h-12 w-12 text-primary-200" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 15l-2 5L9 9l11 4-5 2zm0 0l5 5M7.188 2.239l.777 2.897M5.136 7.965l-2.898-.777M13.95 4.05l-2.122 2.122m-5.657 5.656l-2.12 2.122" /></svg>
            </div>
            <h3 class="text-xl font-bold text-slate-900">Live Insight Panel</h3>
            <p class="text-slate-500 mt-2 max-w-xs">Select a shipment from the list to view its real-time tracking trajectory and event logs.</p>
          </div>

          <div *ngIf="selectedDelivery" id="historyView" class="glass-morphism p-8 rounded-[2rem] border border-white/60 shadow-2xl shadow-primary-500/5 animate-in slide-in-from-right-10 duration-500 sticky top-24">
            <div class="flex justify-between items-start mb-10">
              <div>
                <span class="text-[10px] font-black text-primary-500 uppercase tracking-[0.2em] bg-primary-50 px-3 py-1 rounded-full">Selected Asset</span>
                <h2 class="text-2xl font-black text-slate-900 mt-3">{{ selectedDelivery.trackingNumber }}</h2>
                <div class="flex flex-col gap-2 mt-4">
                  <div class="flex items-center text-xs font-semibold text-slate-400">
                    <svg class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                    Created: {{ selectedDelivery.createdAt | date:'medium' }}
                  </div>
                  <div *ngIf="selectedDelivery.deliveredAt" class="flex items-center text-xs font-bold text-green-600">
                    <svg class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /></svg>
                    Delivered: {{ selectedDelivery.deliveredAt | date:'medium' }}
                  </div>
                </div>
              </div>
              <button (click)="selectedDelivery = null" class="p-2 bg-slate-100 text-slate-400 hover:text-slate-600 hover:bg-slate-200 rounded-xl transition-all">
                <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
              </button>
            </div>

            <div *ngIf="historyLoading" class="flex flex-col items-center justify-center py-12">
              <app-spinner></app-spinner>
              <p class="mt-4 text-xs font-bold text-slate-400 uppercase tracking-widest">Fetching History...</p>
            </div>

            <div *ngIf="!historyLoading && history.length === 0" class="text-center py-16 bg-slate-50/50 rounded-3xl border border-dashed border-slate-200">
              <p class="text-slate-400 font-medium">No tracking events recorded.</p>
            </div>

            <div *ngIf="!historyLoading && history.length > 0" class="space-y-0 relative">
              <!-- Vertical Line -->
              <div class="absolute left-6 top-2 bottom-2 w-0.5 bg-slate-100"></div>

              <div *ngFor="let item of history; let first = first" class="relative pl-14 pb-10 last:pb-0 group">
                <!-- Status Node -->
                <div class="absolute left-[1.15rem] top-1 w-3.5 h-3.5 rounded-full z-10 ring-4 ring-white transition-transform group-hover:scale-125"
                     [class]="getHistoryNodeClass(item.status)"></div>
                
                <div class="transition-all duration-300">
                  <div class="flex justify-between items-start mb-1">
                    <h4 class="text-sm font-black tracking-tight" [class]="getHistoryTextClass(item.status)">{{ item.status.replace('_', ' ') }}</h4>
                    <span class="text-[10px] font-bold text-slate-300">{{ item.timestamp | date:'MMM d, h:mm a' }}</span>
                  </div>
                  <p class="text-xs font-bold text-slate-700 mb-2 flex items-center">
                    <svg class="h-3 w-3 mr-1 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                    {{ item.location }}
                  </p>
                  <div class="p-3 bg-slate-50 rounded-xl text-xs text-slate-500 border border-slate-100 group-hover:border-primary-100 group-hover:bg-primary-50/30 transition-all">
                    {{ item.description }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class AdminTrackingComponent implements OnInit {
  private adminService = inject(AdminService);
  private trackingService = inject(TrackingService);

  deliveries: any[] = [];
  filteredDeliveries: any[] = [];
  selectedDelivery: any = null;
  history: any[] = [];
  searchQuery = '';
  loading = true;
  historyLoading = false;

  ngOnInit() {
    this.adminService.getAllDeliveries().subscribe({
      next: (data) => {
        this.deliveries = data;
        this.filteredDeliveries = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onSearch() {
    this.selectedDelivery = null; // Clear selection on new search
    if (!this.searchQuery) {
      this.filteredDeliveries = this.deliveries;
    } else {
      this.filteredDeliveries = this.deliveries.filter(d => 
        d.trackingNumber.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    }
  }

  clearSearch() {
    this.searchQuery = '';
    this.onSearch();
  }

  selectDelivery(delivery: any) {
    this.selectedDelivery = delivery;
    this.fetchHistory(delivery.trackingNumber);
  }

  fetchHistory(trackingNumber: string) {
    this.historyLoading = true;
    this.trackingService.getTrackingHistory(trackingNumber).subscribe({
      next: (data) => {
        this.history = data.sort((a: any, b: any) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
        this.historyLoading = false;
      },
      error: () => {
        this.history = [];
        this.historyLoading = false;
      }
    });
  }

  getStatusClass(status: string): string {
    switch(status) {
      case 'DELIVERED': return 'bg-green-100 text-green-700 border border-green-200';
      case 'OUT_FOR_DELIVERY': return 'bg-blue-100 text-blue-700 border border-blue-200';
      case 'IN_TRANSIT': return 'bg-yellow-100 text-yellow-700 border border-yellow-200';
      case 'FAILED': return 'bg-red-100 text-red-700 border border-red-200';
      default: return 'bg-slate-100 text-slate-700 border border-slate-200';
    }
  }

  getStatusDotClass(status: string): string {
    switch(status) {
      case 'DELIVERED': return 'bg-green-500';
      case 'OUT_FOR_DELIVERY': return 'bg-blue-500';
      case 'IN_TRANSIT': return 'bg-yellow-500';
      case 'FAILED': return 'bg-red-500';
      default: return 'bg-slate-400';
    }
  }

  getHistoryNodeClass(status: string): string {
    switch(status) {
      case 'DELIVERED': return 'bg-green-500 ring-green-100';
      case 'OUT_FOR_DELIVERY': return 'bg-blue-500 ring-blue-100';
      case 'IN_TRANSIT': return 'bg-yellow-500 ring-yellow-100';
      case 'FAILED': return 'bg-red-500 ring-red-100';
      default: return 'bg-slate-400 ring-slate-100';
    }
  }

  getHistoryTextClass(status: string): string {
    switch(status) {
      case 'DELIVERED': return 'text-green-600';
      case 'OUT_FOR_DELIVERY': return 'text-blue-600';
      case 'IN_TRANSIT': return 'text-yellow-600';
      case 'FAILED': return 'text-red-600';
      default: return 'text-slate-600';
    }
  }
}
