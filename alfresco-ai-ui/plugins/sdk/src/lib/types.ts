export type UiAction = [string] | [string, unknown[]];

export type MainNavigationEntry = {
  text: string;
  icon?: string;
  action: UiAction;
};

export type MainActionEntry = {
  text: string;
  icon?: string;
  action: UiAction;
};
