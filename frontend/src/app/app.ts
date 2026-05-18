import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/components/navbar.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, CommonModule],
  template: `
    <div class="min-h-screen flex flex-col">
      <app-navbar></app-navbar>
      <main class="flex-grow">
        <router-outlet></router-outlet>
      </main>
      <footer class="bg-white border-t border-slate-100 py-8">
        <div class="max-w-7xl mx-auto px-4 text-center text-slate-500 text-sm">
          &copy; 2026 SmartCourier Management System. All rights reserved.
        </div>
      </footer>
    </div>
  `,
  styles: []
})
export class App {
  title = 'SmartCourier Frontend';
}
