import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, CommonModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-slate-50 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8 glass-morphism p-8 rounded-2xl shadow-xl">
        <div>
          <h2 class="mt-6 text-center text-3xl font-extrabold text-slate-900">Create Account</h2>
          <p class="mt-2 text-center text-sm text-slate-600">
            Already have an account?
            <a routerLink="/auth/login" class="font-medium text-primary-600 hover:text-primary-500">Sign in</a>
          </p>
        </div>
        <form class="mt-8 space-y-4" [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          <div>
            <label class="block text-sm font-medium text-slate-700">Username</label>
            <input formControlName="username" type="text" class="input-field mt-1">
            <span *ngIf="registerForm.get('username')?.touched && registerForm.get('username')?.invalid" class="text-xs text-red-500">Min 3 characters, alphanumeric only</span>
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700">Email</label>
            <input formControlName="email" type="email" class="input-field mt-1">
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700">Password</label>
            <input formControlName="password" type="password" class="input-field mt-1">
            <span *ngIf="registerForm.get('password')?.touched && registerForm.get('password')?.invalid" class="text-xs text-red-500">Min 8 chars, must include uppercase, lowercase, digit, special char</span>
          </div>
          
          <button type="submit" [disabled]="registerForm.invalid || loading" class="w-full btn-primary py-3 rounded-xl">
            Register
          </button>
          
          <div *ngIf="error" class="text-red-500 text-sm text-center">{{ error }}</div>
          <div *ngIf="success" class="text-green-500 text-sm text-center">{{ success }}</div>
        </form>
      </div>
    </div>
  `
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.pattern('^[a-zA-Z0-9_-]+$')]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$')]]
  });

  loading = false;
  error = '';
  success = '';

  onSubmit() {
    if (this.registerForm.valid) {
      this.loading = true;
      this.authService.register(this.registerForm.value as any).subscribe({
        next: () => {
          this.success = 'Registration successful! Redirecting to login...';
          setTimeout(() => this.router.navigate(['/auth/login']), 2000);
        },
        error: (err) => {
          this.error = 'Registration failed. User may already exist.';
          this.loading = false;
        }
      });
    }
  }
}
