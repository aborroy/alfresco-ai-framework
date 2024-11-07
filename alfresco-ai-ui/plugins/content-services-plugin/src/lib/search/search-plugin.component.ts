import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ADF_SEARCH_CONFIGURATION,
  DocumentListModule,
  SearchInputComponent,
  SearchModule,
  SearchQueryBuilderService
} from '@alfresco/adf-content-services';
import { DataTableModule, NotificationService, PaginationComponent, TemplateModule } from '@alfresco/adf-core';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { Pagination, ResultSetPaging } from '@alfresco/js-api';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { MatIconModule } from '@angular/material/icon';
import { DefaultSearchConfiguration } from './search.config';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'lib-search-plugin',
  standalone: true,
  imports: [
    CommonModule,
    SearchModule,
    DataTableModule,
    DocumentListModule,
    TemplateModule,
    MatProgressBarModule,
    MatButtonModule,
    MatIconModule,
    SearchInputComponent,
    PaginationComponent
  ],
  providers: [
    // TODO: Search not working without this line
    SearchQueryBuilderService,
    { provide: ADF_SEARCH_CONFIGURATION, useValue: DefaultSearchConfiguration }
  ],
  templateUrl: './search-plugin.component.html',
  styleUrls: ['./search-plugin.component.css']
})
export class SearchPluginComponent implements OnInit, OnDestroy {
  private queryBuilder = inject(SearchQueryBuilderService);
  private notifications = inject(NotificationService);
  private router = inject(Router);
  private activeRoute = inject(ActivatedRoute);

  private onDestroy$ = new Subject<boolean>();

  isLoading = false;
  sorting = ['name', 'asc'];
  data?: ResultSetPaging;

  config = {
    fields: ['cm:name', 'cm:title', 'cm:description', 'TEXT', 'TAG']
  };

  constructor() {
    // TODO: investigate why needed in ACA
    // TODO: ADF bug, should have default value
    this.queryBuilder.paging = {
      skipCount: 0,
      maxItems: 25
    };
  }

  ngOnInit(): void {
    this.queryBuilder.resetToDefaults();
    this.sorting = this.getSorting();

    this.queryBuilder.updated.pipe(takeUntil(this.onDestroy$)).subscribe((query) => {
      if (query) {
        this.sorting = this.getSorting();
        this.isLoading = true;
        this.queryBuilder.execute(query);
      }
    });

    this.queryBuilder.executed.pipe(takeUntil(this.onDestroy$)).subscribe((data) => {
      // TODO: investigate why needed in ACA
      this.queryBuilder.paging.skipCount = 0;

      this.data = data;
      this.isLoading = false;
    });

    this.queryBuilder.error.pipe(takeUntil(this.onDestroy$)).subscribe((err: any) => {
      this.onSearchError(err);
    });

    // this.columns = this.extensions.documentListPresets.searchResults || [];
  }

  onSearchError(error: { message: never }) {
    const { statusCode } = JSON.parse(error.message).error;
    this.notifications.showError(`Search error: ${statusCode}`);

    // TODO: errors should be moved from ACA to ADF
    // const messageKey = `APP.BROWSE.SEARCH.ERRORS.${statusCode}`;
    // let translated = this.translationService.instant(messageKey);

    // if (translated === messageKey) {
    //   translated = this.translationService.instant(`APP.BROWSE.SEARCH.ERRORS.GENERIC`);
    // }

    // this.store.dispatch(new SnackbarErrorAction(translated));
  }

  /**
   * Returns total number of search results, or 0
   */
  get totalResults(): number {
    return this.data?.list?.pagination?.totalItems || 0;
  }

  onPaginationChanged({ maxItems, skipCount }: Pagination) {
    this.queryBuilder.paging = {
      maxItems,
      skipCount
    };
    this.queryBuilder.update();
  }

  ngOnDestroy(): void {
    this.onDestroy$.next(true);
    this.onDestroy$.complete();
  }

  private getSorting(): string[] {
    const primary = this.queryBuilder.getPrimarySorting();

    if (primary) {
      return [primary.key, primary.ascending ? 'asc' : 'desc'];
    }

    return ['name', 'asc'];
  }

  onSearchQueryChanged(searchTerm: string) {
    this.isLoading = true;

    if (searchTerm) {
      this.queryBuilder.userQuery = searchTerm;
      this.queryBuilder.update();
    } else {
      this.queryBuilder.userQuery = '';
      this.queryBuilder.executed.next({
        list: { pagination: { totalItems: 0 }, entries: [] }
      });
      this.isLoading = false;
    }
  }

  showPreview(event: any) {
    const entry = event.value.entry;

    if (entry && entry.isFile) {
      void this.router.navigate(['.', { outlets: { viewer: ['viewer', entry.id] } }], { relativeTo: this.activeRoute });
    }
  }
}
