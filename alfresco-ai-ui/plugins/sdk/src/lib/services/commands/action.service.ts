import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Action, APP_ACTION } from './action';

@Injectable({ providedIn: 'root' })
export class ActionService {
  // TODO: remove to break dependency on Material components here
  private snackBar = inject(MatSnackBar);
  private commands = inject<Action[]>(APP_ACTION, { optional: true }) || [];

  /**
   * Returns a list of all supported action names
   */
  getSupportedActions(): Array<string> {
    return this.commands.map((cmd) => cmd.name);
  }

  /**
   * Checks if the command with the given name is registered
   *
   * @param name Action name (case-insensitive)
   */
  hasCommand(name: string): boolean {
    return this.commands.some((cmd) => cmd.name.toLowerCase() === name.toLowerCase());
  }

  /**
   * Gets the registered command by name
   *
   * @param name Action name (case-insensitive)
   */
  getCommand<TParams = any, TResult = any>(name: string): Action<TParams, TResult> | undefined {
    return this.commands.find((cmd) => cmd.name.toLowerCase() === name.toLowerCase());
  }

  /**
   * Executes the command by name
   *
   * @param name Action name (case-insensitive)
   * @param params (optional) Action parameters
   */
  execute<TParams = any, TResult = any>(name: string, params?: TParams): TResult | undefined {
    const command = this.getCommand<TParams, TResult>(name);

    if (command) {
      return command.execute(params);
    } else {
      this.snackBar.open(`Unknown command: ${name}`);
      return undefined;
    }
  }
}
