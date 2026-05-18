import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  template: `
    <nav class="bg-white border-b border-slate-200 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex">
            <div class="flex-shrink-0 flex items-center">
              <a routerLink="/home" class="text-2xl font-bold text-primary-600 tracking-tight">SmartCourier</a>
            </div>
            <div class="hidden sm:ml-6 sm:flex sm:space-x-8" *ngIf="authService.currentUser$ | async as user; else guestLinks">
              <a *ngIf="!hasAdminRole(user)" routerLink="/home" routerLinkActive="border-primary-500 text-slate-900" [routerLinkActiveOptions]="{exact: true}"
                 class="border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                Home
              </a>
              <a *ngIf="!hasAdminRole(user)" routerLink="/delivery" routerLinkActive="border-primary-500 text-slate-900" 
                 class="border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                Shipments
              </a>
              <a *ngIf="!hasAdminRole(user)" routerLink="/tracking" routerLinkActive="border-primary-500 text-slate-900"
                 class="border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                Track
              </a>
              <a *ngIf="hasAdminRole(user)" routerLink="/admin" routerLinkActive="border-primary-500 text-slate-900"
                 class="border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                Dashboard
              </a>
              <a *ngIf="hasAdminRole(user)" routerLink="/admin/tracking" routerLinkActive="border-primary-500 text-slate-900"
                 class="border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                Admin Tracking
              </a>
            </div>
            <ng-template #guestLinks>
              <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
                <a routerLink="/home" class="border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">Home</a>
              </div>
            </ng-template>
          </div>
          <div class="hidden sm:ml-6 sm:flex sm:items-center">
            <ng-container *ngIf="authService.currentUser$ | async as user; else loginBtn">
              <span class="text-sm font-medium text-slate-700 mr-4">
                Hi, <span class="text-primary-600 font-bold">{{ user.username }}</span>
              </span>
              <button (click)="logout()" class="btn-primary !bg-slate-100 !text-slate-700 hover:!bg-slate-200 shadow-none">Logout</button>
            </ng-container>
            <ng-template #loginBtn>
              <a routerLink="/auth/login" class="btn-primary">Login</a>
            </ng-template>
          </div>
        </div>
      </div>
    </nav>
  `
})
export class NavbarComponent {
  authService = inject(AuthService);
  private router = inject(Router);

  hasAdminRole(user: any): boolean {
    return !!user?.roles?.some((r: any) => r.name === 'ROLE_ADMIN');
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/home']);
  }
}
