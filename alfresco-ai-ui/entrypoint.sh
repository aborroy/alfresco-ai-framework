#!/bin/sh

# Write dynamic configuration to config.js
echo "window.__APP_CONFIG__ = { \
  chatServer: '${CHAT_SERVICE_SERVER}', \
  alfrescoShareServer: '${ALFRESCO_SHARE_SERVER}' \
};" > /usr/share/nginx/html/assets/config.js

# Start NGINX
exec "$@"

