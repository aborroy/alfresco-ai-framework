import { DirectiveModule, LanguageMenuModule } from '@alfresco/adf-core';
import { Component, Output, ViewEncapsulation, EventEmitter, Input, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MainActionEntry } from '../../types';
import { AppTitleComponent } from '../title';
import { ActionService } from '../../services/commands/action.service';

@Component({
  selector: 'app-toolbar',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatIconModule,
    MatMenuModule,
    MatButtonModule,
    TranslateModule,
    LanguageMenuModule,
    DirectiveModule,
    AppTitleComponent
  ],
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AppToolbarComponent {
  private commandService = inject(ActionService);

  @Input()
  entries!: Array<MainActionEntry> | null;

  @Input()
  showSidebar = true;

  @Output()
  toggleSidebar = new EventEmitter<void>();

  runAction(entry: MainActionEntry) {
    if (entry.action) {
      const [command, params] = entry.action;

      if (command) {
        this.commandService.execute(command, params);
      }
    }
  }
}
