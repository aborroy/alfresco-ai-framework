import { Routes } from '@angular/router';
import { StandardLayoutComponent } from '@hx-devkit/sdk';

/** Global application routes */
export const appRoutes: Routes = [
  // Using `Standard` layout for all child routes/components
  {
    path: '',
    component: StandardLayoutComponent,
    // optional: configuring the StandardLayoutComponent settings
    data: {
      layout: {
        showToolbar: true,
        showSidebar: true
      }
    },
    children: [
      {
        path: '',
        loadChildren: () => import('@/plugins/chat').then((m) => m.routes)
      }
    ]
  },
  // Redirect every undefined route to the root
  {
    path: '**',
    redirectTo: ''
  }
];
