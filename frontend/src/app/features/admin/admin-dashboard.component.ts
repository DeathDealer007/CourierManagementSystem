import { Component, OnInit, inject } from '@angular/core';
import { AdminService } from '../../core/services/admin.service';
import { CommonModule } from '@angular/common';
import { SpinnerComponent } from '../../shared/components/spinner.component';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  template: `
    <div class="max-w-20xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="flex justify-center">Admin Dashboard</h1>

      <div *ngIf="loading" class="flex justify-center py-12">
        <app-spinner></app-spinner>
      </div>

      <div *ngIf="!loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
        <div class="glass-morphism p-6 rounded-2xl border-l-4 border-primary-500 shadow-sm">
          <p class="text-sm font-medium text-slate-500 uppercase">Total Shipments</p>
          <p class="text-3xl font-bold text-slate-900 mt-1">{{ stats.totalShipments || 0 }}</p>
        </div>
        <div class="glass-morphism p-6 rounded-2xl border-l-4 border-green-500 shadow-sm">
          <p class="text-sm font-medium text-slate-500 uppercase">Delivered</p>
          <p class="text-3xl font-bold text-slate-900 mt-1">{{ stats.delivered || 0 }}</p>
        </div>
        <div class="glass-morphism p-6 rounded-2xl border-l-4 border-yellow-500 shadow-sm">
          <p class="text-sm font-medium text-slate-500 uppercase">Pending</p>
          <p class="text-3xl font-bold text-slate-900 mt-1">{{ stats.pending || 0 }}</p>
        </div>
        <div class="glass-morphism p-6 rounded-2xl border-l-4 border-blue-500 shadow-sm">
          <p class="text-sm font-medium text-slate-500 uppercase">Out for Delivery</p>
          <p class="text-3xl font-bold text-slate-900 mt-1">{{ stats.outForDelivery || 0 }}</p>
        </div>
        <div class="glass-morphism p-6 rounded-2xl border-l-4 border-red-500 shadow-sm">
          <p class="text-sm font-medium text-slate-500 uppercase">Failed</p>
          <p class="text-3xl font-bold text-slate-900 mt-1">{{ stats.failed || 0 }}</p>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- Active Shipments -->
        <div class="flex justify-center glass-morphism rounded-2xl p-6 shadow-sm">
          <h2 class="text-xl font-bold text-slate-800 mb-6">Active Shipments</h2>
          <div class="flex flex-none">
            <table class="min-w-full divide-y divide-slate-100">
              <thead>
                <tr>
                  <th class="px-4 py-3 text-left text-xs font-semibold text-slate-500 uppercase">Tracking #</th>
                  <th class="px-4 py-3 text-left text-xs font-semibold text-slate-500 uppercase">Status</th>
                  <th class="px-4 py-3 text-left text-xs font-semibold text-slate-500 uppercase">Amount</th>
                  <th class="px-4 py-3 text-left text-xs font-semibold text-slate-500 uppercase">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-50">
                <tr *ngFor="let d of activeShipments" class="hover:bg-slate-50/50 transition-colors">
                  <td class="px-4 py-4 text-sm font-medium text-slate-900">{{ d.trackingNumber }}</td>
                  <td class="px-4 py-4 text-sm text-slate-600">
                    <span class="px-2 py-1 text-xs rounded-full bg-slate-100">{{ d.status }}</span>
                  </td>
                  <td class="px-4 py-4 text-sm text-slate-600">\${{ d.totalAmount }}</td>
                  <td class="px-4 py-4 text-sm text-slate-600">
                    <select (change)="onStatusChange(d.id, $any($event.target).value)" 
                            class="text-xs border rounded p-1 bg-white focus:ring-1 focus:ring-primary-500">
                      <option value="" disabled selected>Update</option>
                      <option value="PICKED">Picked</option>
                      <option value="IN_TRANSIT">In Transit</option>
                      <option value="OUT_FOR_DELIVERY">Out for Delivery</option>
                      <option value="DELIVERED">Delivered</option>
                    </select>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- System Hubs -->
   
  `
})
export class AdminDashboardComponent implements OnInit {
  private adminService = inject(AdminService);
  
  stats: any = {};
  activeShipments: any[] = [];
  hubs: any[] = [];
  loading = true;

  ngOnInit() {
    this.refreshData();
    this.adminService.getAllHubs().subscribe(data => {
      this.hubs = data;
      this.loading = false;
    });
  }

  onStatusChange(id: number, status: string) {
    if (confirm(`Are you sure you want to change status to ${status}?`)) {
      this.adminService.updateDeliveryStatus(id, status).subscribe({
        next: () => {
          this.refreshData();
        },
        error: (err) => {
          alert('Failed to update status: ' + err.message);
        }
      });
    }
  }

  refreshData() {
    this.adminService.getDashboardStats().subscribe(data => {
      this.stats = data;
    });

    this.adminService.getAllDeliveries().subscribe(data => {
      // Filter out DELIVERED shipments to show only active ones
      this.activeShipments = data.filter((d: any) => d.status !== 'DELIVERED').slice(0, 10);
    });
  }
}
