import { Component, inject, ViewEncapsulation } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { APP_DESCRIPTION, APP_NAME } from '../../tokens';
import { NgIf } from '@angular/common';
import { RouterLinkWithHref } from '@angular/router';

@Component({
  selector: 'app-title',
  standalone: true,
  imports: [NgIf, TranslateModule, RouterLinkWithHref],
  templateUrl: 'app-title.component.html',
  styleUrls: ['app-title.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AppTitleComponent {
  appName = inject(APP_NAME, { optional: true });
  appDescription = inject(APP_DESCRIPTION, { optional: true });
}
