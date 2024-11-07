import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Action } from '../action';

export class RouterNavigateAction implements Action<any[], Promise<boolean>> {
  private router = inject(Router);

  get name(): string {
    return 'router.navigate';
  }

  execute(params?: any[]): Promise<boolean> {
    if (params) {
      return this.router.navigate(params);
    }
    return Promise.resolve(false);
  }
}
