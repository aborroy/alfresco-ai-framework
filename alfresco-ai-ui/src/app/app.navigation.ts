import { MainNavigationEntry } from '@hx-devkit/sdk';

/** Default global application sidebar entries */
export const mainNavigationEntries: Array<MainNavigationEntry> = [
  {
    text: 'Dashboard',
    icon: 'text_snippet',
    action: ['router.navigate', ['/dashboard']]
  },
];
