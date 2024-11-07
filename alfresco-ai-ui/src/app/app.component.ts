import { Component, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-root',
  encapsulation: ViewEncapsulation.None,
  template: `<router-outlet></router-outlet>`
})
export class AppComponent {}
