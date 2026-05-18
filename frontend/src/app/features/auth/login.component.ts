import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, CommonModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-slate-50 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8 glass-morphism p-8 rounded-2xl shadow-xl">
        <div>
          <h2 class="mt-6 text-center text-3xl font-extrabold text-slate-900">Welcome Back</h2>
          <p class="mt-2 text-center text-sm text-slate-600">
            Or
            <a routerLink="/auth/register" class="font-medium text-primary-600 hover:text-primary-500">create a new account</a>
          </p>
        </div>
        <form class="mt-8 space-y-6" [formGroup]="loginForm" (ngSubmit)="onSubmit()">
          <div class="rounded-md shadow-sm space-y-4">
            <div>
              <label for="username" class="block text-sm font-medium text-slate-700">Username</label>
              <input formControlName="username" type="text" required class="input-field mt-1" placeholder="Username">
            </div>
            <div>
              <label for="password" class="block text-sm font-medium text-slate-700">Password</label>
              <input formControlName="password" type="password" required class="input-field mt-1" placeholder="Password">
            </div>
          </div>

          <div>
            <button type="submit" [disabled]="loginForm.invalid || loading" class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-xl text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 transition-all">
              <span *ngIf="loading" class="animate-spin mr-2">...</span>
              Sign in
            </button>
          </div>
          
          <div class="relative py-4">
            <div class="absolute inset-0 flex items-center">
              <div class="w-full border-t border-slate-200"></div>
            </div>
            <div class="relative flex justify-center text-sm">
              <span class="px-2 bg-white text-slate-500">Quick Access</span>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <button type="button" (click)="loginAs('admin')" class="flex justify-center items-center px-4 py-2 border border-slate-300 rounded-xl text-sm font-medium text-slate-700 bg-white hover:bg-slate-50 transition-all">
              Admin Login
            </button>
            <button type="button" (click)="loginAs('user')" class="flex justify-center items-center px-4 py-2 border border-slate-300 rounded-xl text-sm font-medium text-slate-700 bg-white hover:bg-slate-50 transition-all">
              User Login
            </button>
          </div>
          
          <div *ngIf="error" class="text-red-500 text-sm text-center bg-red-50 p-2 rounded-lg">
            {{ error }}
          </div>
        </form>
      </div>
    </div>
  `
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  loginForm = this.fb.group({
    username: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(8), Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$')]]
  });

  loading = false;
  error = '';

  loginAs(type: 'admin' | 'user') {
    if (type === 'admin') {
      this.loginForm.patchValue({
        username: 'admin',
        password: 'Admin@123'
      });
    } else {
      this.loginForm.patchValue({
        username: 'user',
        password: 'User@123'
      });
    }
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.loading = true;
      this.error = '';
      this.authService.login(this.loginForm.value as any).subscribe({
        next: () => {
          const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/delivery';
          this.router.navigateByUrl(returnUrl);
        },
        error: (err) => {
          this.error = 'Invalid credentials. Please try again.';
          this.loading = false;
        }
      });
    }
  }
}
