import { Injectable } from '@angular/core';
import { Config, DEFAULT_CONFIG } from './app.config';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private config: Config;

  constructor() {
    this.config = {
      ...DEFAULT_CONFIG,
      ...this.getWindowConfig()
    };
  }

  private getWindowConfig(): Partial<Config> {
    return (window as any)['__APP_CONFIG__'] || {};
  }

  getConfig(): Config {
    return this.config;
  }

  get chatServer(): string {
    return this.config.chatServer;
  }

  get alfrescoShareServer(): string {
    return this.config.alfrescoShareServer;
  }
}
