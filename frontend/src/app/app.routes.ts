import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent) },
  { path: 'home', loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent) },
  {
    path: 'auth',
    children: [
      { path: 'login', loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent) },
      { path: 'register', loadComponent: () => import('./features/auth/register.component').then(m => m.RegisterComponent) }
    ]
  },
  {
    path: 'delivery',
    canActivate: [authGuard],
    children: [
      { path: '', loadComponent: () => import('./features/delivery/shipment-list.component').then(m => m.ShipmentListComponent) },
      { path: 'create', loadComponent: () => import('./features/delivery/create-shipment.component').then(m => m.CreateShipmentComponent) }
    ]
  },
  {
    path: 'tracking',
    loadComponent: () => import('./features/tracking/tracking-page.component').then(m => m.TrackingPageComponent)
  },
  {
    path: 'tracking/:id',
    loadComponent: () => import('./features/tracking/tracking-page.component').then(m => m.TrackingPageComponent)
  },
  {
    path: 'admin',
    canActivate: [authGuard],
    children: [
      { path: '', loadComponent: () => import('./features/admin/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
      { path: 'tracking', loadComponent: () => import('./features/admin/admin-tracking.component').then(m => m.AdminTrackingComponent) }
    ]
  }
];
