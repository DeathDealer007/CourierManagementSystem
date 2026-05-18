import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen flex flex-col">
      <!-- Hero Section -->
      <section class="relative py-20 overflow-hidden">
        <div class="absolute inset-0 -z-10 bg-[radial-gradient(45%_45%_at_50%_50%,rgba(99,102,241,0.1)_0%,rgba(255,255,255,0)_100%)]"></div>
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h1 class="text-5xl md:text-7xl font-extrabold text-slate-900 tracking-tight mb-6">
            Smart Courier <span class="text-primary-600">Reimagined.</span>
          </h1>
          <p class="text-xl text-slate-600 max-w-2xl mx-auto mb-10">
            The next-generation logistics platform powered by microservices. 
            Track, manage, and scale your delivery operations with real-time intelligence.
          </p>
          <div class="flex justify-center space-x-4">
            <a routerLink="/delivery" class="btn-primary px-8 py-4 rounded-2xl text-lg shadow-xl shadow-primary-500/20 hover:scale-105 transition-transform">
              Get Started
            </a>
            <a routerLink="/tracking" class="btn-primary !bg-white !text-slate-900 border border-slate-200 px-8 py-4 rounded-2xl text-lg hover:bg-slate-50 transition-colors">
              Track Package
            </a>
          </div>
        </div>
      </section>

      <!-- Features Grid -->
      <section class="py-20 bg-slate-50/50">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="text-center mb-16">
            <h2 class="text-3xl font-bold text-slate-900 mb-4">Powerful Features</h2>
            <p class="text-slate-600">Everything you need to manage modern courier logistics.</p>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div class="glass-morphism p-8 rounded-3xl hover:translate-y-[-8px] transition-transform duration-300">
              <div class="w-12 h-12 bg-primary-100 rounded-2xl flex items-center justify-center text-primary-600 mb-6">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h3 class="text-lg font-bold text-slate-900 mb-2">Real-Time Tracking</h3>
              <p class="text-sm text-slate-600">Live updates powered by RabbitMQ events and live status polling.</p>
            </div>

            <div class="glass-morphism p-8 rounded-3xl hover:translate-y-[-8px] transition-transform duration-300">
              <div class="w-12 h-12 bg-indigo-100 rounded-2xl flex items-center justify-center text-indigo-600 mb-6">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
              </div>
              <h3 class="text-lg font-bold text-slate-900 mb-2">Shipment Portal</h3>
              <p class="text-sm text-slate-600">Create and manage your deliveries through an intuitive dashboard.</p>
            </div>

            <div class="glass-morphism p-8 rounded-3xl hover:translate-y-[-8px] transition-transform duration-300">
              <div class="w-12 h-12 bg-emerald-100 rounded-2xl flex items-center justify-center text-emerald-600 mb-6">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                </svg>
              </div>
              <h3 class="text-lg font-bold text-slate-900 mb-2">Role-Based Access</h3>
              <p class="text-sm text-slate-600">Secure JWT-based authentication with distinct user and admin roles.</p>
            </div>

            <div class="glass-morphism p-8 rounded-3xl hover:translate-y-[-8px] transition-transform duration-300">
              <div class="w-12 h-12 bg-amber-100 rounded-2xl flex items-center justify-center text-amber-600 mb-6">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 12l3-3 3 3 4-4M8 21l4-4 4 4M3 4h18M4 4h16v12a1 1 0 01-1 1H5a1 1 0 01-1-1V4z" />
                </svg>
              </div>
              <h3 class="text-lg font-bold text-slate-900 mb-2">Admin Dashboard</h3>
              <p class="text-sm text-slate-600">Complete overview and control for system administrators.</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Footer -->
      <footer class="mt-auto py-10 border-t border-slate-100">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row justify-between items-center">
          <div class="text-slate-900 font-bold text-xl mb-4 md:mb-0">SmartCourier</div>
          <div class="text-slate-500 text-sm">
            © 2026 Smart Courier System. All rights reserved.
          </div>
          <div class="mt-4 md:mt-0 text-slate-600 font-medium">
            Created by <span class="text-primary-600 font-bold">Ankit</span>
          </div>
        </div>
      </footer>
    </div>
  `
})
export class HomeComponent {}
