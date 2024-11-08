FROM node:18.16-alpine3.17 AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ENV NX_CACHE_DIRECTORY=/tmp
RUN npm run build --skip-nx-cache

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy the startup script
COPY ./entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Set default values for environment variables
ENV CHAT_SERVICE_SERVER=http://localhost:9999
ENV ALFRESCO_SHARE_SERVER=http://localhost:8080

EXPOSE 80

ENTRYPOINT ["/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
