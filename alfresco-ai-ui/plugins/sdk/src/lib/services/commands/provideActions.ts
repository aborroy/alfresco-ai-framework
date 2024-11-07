import { Provider, Type } from '@angular/core';
import { Action, APP_ACTION } from './action';

export function provideActions(commands: Type<Action>[]): Array<Provider> {
  return commands.map((command) => ({
    provide: APP_ACTION,
    multi: true,
    useClass: command
  }));
}
