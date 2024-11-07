import { Component, Input, ViewChild, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AppSidebarComponent, AppToolbarComponent } from '../../components';

import { BaseAppLayoutComponent } from '../base/base-app-layout.component';

export type StandardLayoutSettings = {
  showToolbar?: boolean;
  showSidebar?: boolean;
};

@Component({
  selector: 'app-standard-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, AppToolbarComponent, AppSidebarComponent],
  templateUrl: 'standard-layout.component.html',
  styleUrls: ['standard-layout.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class StandardLayoutComponent extends BaseAppLayoutComponent {
  @Input()
  showToolbar = true;

  @Input()
  showSidebar = true;

  @ViewChild('sidebar')
  sidebar?: AppSidebarComponent;

  applySettings(settings: StandardLayoutSettings) {
    if (settings.showSidebar !== undefined) {
      this.showSidebar = settings.showSidebar;
    }

    if (settings.showToolbar !== undefined) {
      this.showToolbar = settings.showToolbar;
    }
  }
}
