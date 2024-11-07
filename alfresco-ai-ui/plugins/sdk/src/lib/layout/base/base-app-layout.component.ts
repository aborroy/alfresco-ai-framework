import { Component, OnInit, inject } from '@angular/core';
import { NavigationService } from '../../services';
import { ActivatedRoute } from '@angular/router';

export type LayoutSettings = NonNullable<object>;

@Component({
  template: `<div>Base Layout, extends this component to provide your own</div>`
})
export abstract class BaseAppLayoutComponent implements OnInit {
  private navigationService = inject(NavigationService);
  private activatedRoute = inject(ActivatedRoute);

  navigation$ = this.navigationService.mainNavigation$;
  mainActionsEntries$ = this.navigationService.mainActions$;

  ngOnInit() {
    this.activatedRoute.data.subscribe((data) => {
      const settings = data['layout'] as LayoutSettings;
      if (settings) {
        this.applySettings(settings);
      }
    });
  }

  abstract applySettings(settings: LayoutSettings): void;
}
