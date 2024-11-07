import { InjectionToken, Provider, Type } from '@angular/core';

export interface Widget {
  key: string;
  name: string;
  layout: {
    color?: string;
    cols: number;
    rows: number;
  };
  component?: Type<unknown>;
}

export const APP_WIDGET = new InjectionToken<Widget>('Application Widget');

export function provideWidgets(entries: Widget[]): Array<Provider> {
  return entries.map<Provider>((entry) => ({
    provide: APP_WIDGET,
    multi: true,
    useValue: entry
  }));
}
