const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';
const IDENTITY_SERVICE_URL = process.env.IDENTITY_SERVICE_URL || process.env.BASE_URL || 'http://localhost:3000';

console.info(`ACS URL: ${BASE_URL}`);
console.info(`IDENTITY URL: ${IDENTITY_SERVICE_URL}`);

module.exports = {
  '/alfresco': {
    target: BASE_URL,
    pathRewrite: {
      '^/alfresco/alfresco': ''
    },
    secure: false,
    changeOrigin: true,
    onProxyReq: function (request) {
      if (request['method'] !== 'GET') {
        request.setHeader('origin', BASE_URL);
      }
    },
    // workaround for REPO-2260
    onProxyRes: function (proxyRes) {
      const header = proxyRes.headers['www-authenticate'];
      if (header?.startsWith('Basic')) {
        proxyRes.headers['www-authenticate'] = 'x' + header;
      }
    }
  },
  '/auth': {
    target: IDENTITY_SERVICE_URL,
    secure: false,
    logLevel: 'debug',
    changeOrigin: 'true'
  },
  '/search': {
    target: 'http://localhost:8081',
    secure: false,
    logLevel: 'debug',
    changeOrigin: 'true'
  }
};
