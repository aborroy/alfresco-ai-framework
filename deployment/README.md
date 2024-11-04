# Deployment of Alfresco AI Framework with Elasticsearch and Ollama

This repository contains Docker Compose configuration for deploying the Alfresco AI Framework with Elasticsearch and Ollama integration (via local program or Docker container).

## Prerequisites

- Docker and Docker Compose installed on your system
- At least 8GB of available RAM for Elasticsearch
- Sufficient disk space for Elasticsearch data and Ollama models

## Quick Start

1. Clone this repository

2. The default configuration in `.env` is, that requires [ollama](https://ollama.com/download) running as local program:
   ```
   ELASTICSEARCH_URIS=http://elasticsearch:9200
   OLLAMA_BASE_URL=http://host.docker.internal:11434
   ELASTICSEARCH_VERSION=8.15.3
   ```

3. Start the services:
   ```bash
   docker compose up -d
   ```

## Configuration Options

### Option 1: Using Local Ollama (Default)

The default configuration assumes Ollama is running locally on your machine. This is reflected in the `OLLAMA_BASE_URL` setting in `.env`:
```
OLLAMA_BASE_URL=http://host.docker.internal:11434
```

### Option 2: Running Ollama in Docker

To run Ollama as a Docker container instead of using a local installation:

1. Modify `compose.yaml` to include the Ollama service:
   ```yaml
   include:
     - elasticsearch.yaml
     - ollama.yaml  # Add this line
   services:
     alfresco-ai-framework:
       image: alfresco-ai-framework:latest
       environment:
         - spring.elasticsearch.uris=${ELASTICSEARCH_URIS}
         - spring.ai.ollama.base-url=${OLLAMA_BASE_URL}
       depends_on:
         elasticsearch:
           condition: service_healthy
         ollama:  # Add this dependency
           condition: service_started
       ports:
         - "8080:8080"
   ```

2. Update `.env` to point to the Ollama container:
   ```
   OLLAMA_BASE_URL=http://ollama:11434
   ```

## Services

### Alfresco AI Framework

- Port: 8080
- Dependencies: Elasticsearch, Ollama
- Configuration via environment variables

### Elasticsearch

- Version: 8.15.3 (configurable via ELASTICSEARCH_VERSION)
- Port: 9200
- Security: disabled for development
- Persistent volume: es-data

### Ollama (Optional Docker Configuration)

- Latest version (pulls automatically)
- Port: 11434
- Persistent volumes:
    - ollama-models: for storing downloaded models
    - ollama-data: for caching
- Environment variables:
    - OLLAMA_KEEP_ALIVE=24h
    - OLLAMA_HOST=0.0.0.0

## Health Checks

- Elasticsearch has a built-in health check that verifies cluster status
- The application will wait for Elasticsearch to be healthy before starting

## Volume Management

The setup includes persistent volumes for:
- Elasticsearch data: `es-data`
- Ollama models (when using Docker): `ollama-models`
- Ollama cache (when using Docker): `ollama-data`

To clean up volumes:
```bash
docker compose down -v
```

## Troubleshooting

1. If Elasticsearch fails to start, check:
    - System memory limits
    - Elasticsearch log output
    - Virtual memory settings (`sysctl -w vm.max_map_count=262144`)

2. If Ollama connection fails:
    - Verify the correct `OLLAMA_BASE_URL` setting
    - Check if Ollama is running (locally or in Docker)
    - Inspect Ollama logs for any errors

## Notes

- The setup uses a single-node Elasticsearch configuration suitable for development
- Security features are disabled by default for ease of development
- Adjust memory limits and other resources based on your requirements