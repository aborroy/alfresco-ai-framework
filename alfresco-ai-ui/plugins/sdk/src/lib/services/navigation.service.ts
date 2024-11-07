import { Inject, Injectable, InjectionToken, Optional, Provider } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { MainActionEntry, MainNavigationEntry } from '../types';

export const MAIN_NAVIGATION_TOKEN = new InjectionToken<Array<MainNavigationEntry>>('Injection token for application sidebar entries.');
export const MAIN_ACTIONS_TOKEN = new InjectionToken<Array<MainActionEntry>>('Injection token for application header entries.');

/**
 * Generates injection providers to extend the application sidebar menu.
 *
 * @param entries Sidebar entries to register
 * @returns Module providers with the sidebar entries
 */
export function provideMainNavigation(entries: MainNavigationEntry[]): Provider {
  return {
    provide: MAIN_NAVIGATION_TOKEN,
    useValue: entries
  };
}

/**
 * Generates injection providers to extend the application header menu.
 *
 * @param entries Header entries to register
 * @returns Module providers with the header entries
 */
export function provideMainActions(entries: MainActionEntry[]): Provider {
  return {
    provide: MAIN_ACTIONS_TOKEN,
    useValue: entries
  };
}

@Injectable({ providedIn: 'root' })
export class NavigationService {
  /** Application header entries */
  mainActions$: Observable<MainActionEntry[]>;

  /** Application sidebar entries */
  mainNavigation$: Observable<MainNavigationEntry[]>;

  constructor(
    @Optional() @Inject(MAIN_ACTIONS_TOKEN) mainActions: MainActionEntry[],
    @Optional() @Inject(MAIN_NAVIGATION_TOKEN) navigationEntries: MainNavigationEntry[]
  ) {
    this.mainActions$ = new BehaviorSubject(mainActions).asObservable();
    this.mainNavigation$ = new BehaviorSubject(navigationEntries).asObservable();
  }
}
