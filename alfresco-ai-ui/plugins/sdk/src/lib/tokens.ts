import { InjectionToken, Provider } from '@angular/core';

/**
 * Application configuration properties
 */
export type AppConfigProps = {
  // application name, supports i18n keys
  name?: string;
  // application description, supports i18n keys
  description?: string;
  // application copyright information, supports i18n keys
  copyright?: string;
};

export const APP_CONFIG = new InjectionToken<AppConfigProps>('Application configuration');
export const APP_NAME = new InjectionToken<string>('Application title');
export const APP_DESCRIPTION = new InjectionToken<string>('Application description');
export const APP_COPYRIGHT = new InjectionToken<string>('Application copyright');

/**
 * Provides runtime configuration settings for the application.
 * Generates a set of injection tokens for supported application settings.
 *
 * @param cfg configuration properties
 */
export function provideAppConfig(cfg: AppConfigProps): Array<Provider> {
  const result: Provider[] = [{ provide: APP_CONFIG, useValue: cfg }];

  if (cfg.name) {
    result.push({ provide: APP_NAME, useValue: cfg.name });
  }

  if (cfg.description) {
    result.push({ provide: APP_DESCRIPTION, useValue: cfg.description });
  }

  if (cfg.copyright) {
    result.push({ provide: APP_COPYRIGHT, useValue: cfg.copyright });
  }

  return result;
}
