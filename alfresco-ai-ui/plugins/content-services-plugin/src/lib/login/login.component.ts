import { AppConfigModule, LoginModule } from '@alfresco/adf-core';
import { Component, inject, ViewEncapsulation } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { APP_COPYRIGHT } from '@hx-devkit/sdk';

@Component({
  standalone: true,
  imports: [LoginModule, AppConfigModule, TranslateModule],
  selector: 'lib-login',
  templateUrl: './login.component.html',
  encapsulation: ViewEncapsulation.None
})
export class LoginComponent {
  copyrightText = inject(APP_COPYRIGHT, { optional: true }) || '';
}
