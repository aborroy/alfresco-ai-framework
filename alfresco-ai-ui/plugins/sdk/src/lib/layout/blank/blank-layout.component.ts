import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-blank-layout',
  standalone: true,
  imports: [RouterOutlet],
  template: '<router-outlet id="main-content-outlet"></router-outlet>'
})
export class BlankLayoutComponent {}
