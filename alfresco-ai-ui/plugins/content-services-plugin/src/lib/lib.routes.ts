import { Route } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { DocumentsComponent } from './documents/documents.component';
import { TrashcanComponent } from './trashcan/trashcan.component';
import { SearchPluginComponent } from './search/search-plugin.component';
import { LayoutComponent } from './layout/layout.component';
import { PreviewComponent } from './preview/preview.component';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './login/AuthGuard';

export const contentServicesPluginRoutes: Route[] = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: '',
        component: HomeComponent
      },
      {
        path: 'documents',
        canActivate: [AuthGuard],
        children: [
          {
            path: '',
            component: DocumentsComponent
          },
          {
            path: 'viewer/:nodeId',
            outlet: 'viewer',
            component: PreviewComponent
          }
        ]
      },
      {
        path: 'search',
        canActivate: [AuthGuard],
        children: [
          {
            path: '',
            component: SearchPluginComponent
          },
          {
            path: 'viewer/:nodeId',
            outlet: 'viewer',
            component: PreviewComponent
          }
        ]
      },
      {
        path: 'trashcan',
        canActivate: [AuthGuard],
        component: TrashcanComponent
      }
    ]
  },
  {
    path: 'login',
    component: LoginComponent
  }
];
