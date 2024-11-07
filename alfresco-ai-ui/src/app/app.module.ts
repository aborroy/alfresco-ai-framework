import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatIconRegistry } from '@angular/material/icon';
import { AuthModule, CoreModule, TranslateLoaderService, provideTranslations } from '@alfresco/adf-core';
import { ContentModule } from '@alfresco/adf-content-services';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { appRoutes } from './app.routes';
import { AppComponent } from './app.component';

import { registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es';
registerLocaleData(localeEs);

import { provideMainNavigation, provideMainActions, provideAppConfig, provideActions } from '@hx-devkit/sdk';
import { mainNavigationEntries } from './app.navigation';
import { appToolbar } from './app.toolbar';
import { appConfig } from './app.config';
import { appActions } from './app.actions';

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    RouterModule.forRoot(appRoutes, { useHash: false, enableTracing: true }),
    AuthModule.forRoot({ useHash: false }),
    CoreModule.forRoot(),
    ContentModule.forRoot(),
    TranslateModule.forRoot({
      loader: { provide: TranslateLoader, useClass: TranslateLoaderService }
    })
  ],
  declarations: [AppComponent],
  providers: [
    provideTranslations('app', 'assets'),
    provideMainActions(appToolbar),
    provideMainNavigation(mainNavigationEntries),
    provideAppConfig(appConfig),
    provideActions(appActions)
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(matIconRegistry: MatIconRegistry) {
    matIconRegistry.setDefaultFontSetClass('material-icons-outlined');
  }
}
