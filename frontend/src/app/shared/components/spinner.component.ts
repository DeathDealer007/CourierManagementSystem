import { Component } from '@angular/core';

@Component({
  selector: 'app-spinner',
  standalone: true,
  template: `
    <div class="flex items-center justify-center space-x-2">
      <div class="w-4 h-4 rounded-full animate-bounce bg-primary-600"></div>
      <div class="w-4 h-4 rounded-full animate-bounce bg-primary-600 [animation-delay:-.3s]"></div>
      <div class="w-4 h-4 rounded-full animate-bounce bg-primary-600 [animation-delay:-.5s]"></div>
    </div>
  `
})
export class SpinnerComponent {}
