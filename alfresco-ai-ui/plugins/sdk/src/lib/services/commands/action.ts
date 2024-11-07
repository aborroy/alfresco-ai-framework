import { InjectionToken } from '@angular/core';

/**
 * Describes a generic action implementation
 */
export interface Action<TParams = any, TResult = any> {
  /**
   * Returns unique name of the action
   */
  get name(): string;

  /**
   * Execute the action with the parameters.
   *
   * @param params parameters for action to use
   */
  execute: (params?: TParams) => TResult;
}

export const APP_ACTION = new InjectionToken<Action>('Application Action');
