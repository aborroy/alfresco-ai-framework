import { CommonModule } from '@angular/common';
import { Component, inject, Input, ViewChild, ViewEncapsulation } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenav, MatSidenavModule } from '@angular/material/sidenav';
import { TranslateModule } from '@ngx-translate/core';
import { MainActionEntry, MainNavigationEntry } from '../../types';
import { ActionService } from '../../services';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, MatSidenavModule, MatListModule, MatIconModule, TranslateModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AppSidebarComponent {
  private commandService = inject(ActionService);

  @ViewChild('sidenav') sidenav!: MatSidenav;

  @Input()
  entries!: Array<MainNavigationEntry> | null;

  toggle() {
    this.sidenav.toggle();
  }

  runAction(entry: MainActionEntry) {
    if (entry.action) {
      const [command, params] = entry.action;

      if (command) {
        this.commandService.execute(command, params);
      }
    }
  }
}
