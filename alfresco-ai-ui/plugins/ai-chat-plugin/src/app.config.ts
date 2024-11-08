export interface Config {
  chatServer: string;
  alfrescoShareServer: string;
}
export const DEFAULT_CONFIG: Config = {
  chatServer: 'http://localhost:9999',
  alfrescoShareServer: 'http://localhost:8080'
};
