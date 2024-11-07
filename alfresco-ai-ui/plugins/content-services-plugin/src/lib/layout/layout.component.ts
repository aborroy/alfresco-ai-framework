import { Component, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'lib-content-services-plugin-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, MatButtonModule],
  template: `
    <router-outlet></router-outlet>
    <router-outlet name="viewer"></router-outlet>
  `,
  styles: [
    `
      router-outlet[name='viewer'] + * {
        width: 100%;
        height: 100%;
        z-index: 999999;
        position: fixed;
        top: 0;
        right: 0;
      }
    `
  ],
  encapsulation: ViewEncapsulation.None
})
export class LayoutComponent {}
