# Lab 1: Ingestion Pipeline

![Ingestion Pipeline Diagram](docs/alfresco-ai-framework-ingestion.png)

In this lab, you will learn how to populate a **Vector Database** (Elasticsearch) with selected content from the **Knowledge Base** stored in Alfresco. This involves extracting vectors from the content using the **Embedding** module `nomic-embed-text` via Ollama.

## Components

- **Alfresco** acts as the **Knowledge Base**, storing documents within folders that have the `cm:syndication` aspect (1)
- **alfresco-ai-sync** retrieves documents from the Alfresco Repository using the Alfresco REST API and uploads the content to the **Vector Database** through the AI RAG Framework REST API (2)
- **ai-rag-framework** exposes the REST API for ingestion and utilizes [Ollama](https://ollama.com/) to access the `nomic-embed-text` embedding model, which generates vector representations of the ingested documents and stores them in **Elasticsearch** (3)

## Configuration Files

- (1) The aspect and properties to be used can be configured in the [application.properties](https://github.com/aborroy/alfresco-ai-framework/blob/main/alfresco-ai-sync/src/main/resources/application.properties#L18) file of the **alfresco-ai-sync** application.  
- (2) Alfresco credentials, protocol, host, and port can be configured in the [application.properties](https://github.com/aborroy/alfresco-ai-framework/blob/main/alfresco-ai-sync/src/main/resources/application.properties#L25) file of the **alfresco-ai-sync** application.  
- (3) Ollama and Elasticsearch settings can be configured in the [application.yml](https://github.com/aborroy/alfresco-ai-framework/blob/main/ai-rag-framework/src/main/resources/application.yml#L13) file of the **ai-rag-framework** application.  

## Step 1: Populate the Knowledge Base

1. **Start Alfresco Community** by running the following command:

    ```sh
    cd alfresco-docker
    docker compose up --build --force-recreate
    ```

2. **Log into Alfresco Share** using the default credentials:

   Username: `admin`  
   Password: `admin`  
   Access Alfresco Share at: [http://localhost:8080/share](http://localhost:8080/share)

3. **Create the "Knowledge Base" folder** with the following rule:

    - **Name**: `Sync Folder`
    - **When**: Items are created or enter this folder
    - **If all criteria are met**: Content of type or sub-type is `Folder`
    - **Action**:  
      Set the property `cm:updated` to `01/01/1999 00:00`

    >> When using a different aspect than default `cm:syndication` (that includes the `cm:updated` propert), [application.properties](https://github.com/aborroy/alfresco-ai-framework/blob/main/alfresco-ai-sync/src/main/resources/application.properties#L18) file for the **alfresco-ai-sync** application needs to be updated

4. **Create a child folder** named `RAG` within the `Knowledge Base` folder and **add some documents** to it.

   > At this point, your **Knowledge Base** is populated and ready for synchronization.

## Step 2: Synchronize the Knowledge Base with the Vector Database

Follow the steps to synchronize the content from the Alfresco Knowledge Base to the Vector Database (Elasticsearch) using the **alfresco-ai-sync** application.

1. **Stop Alfresco Community** by pressing `Ctrl+C`

   Data won't be loss, as local Docker volumes are used

2. **Verify ollama is running** or start the program

   ```sh
   ollama -v
   ```
3. **Start the full stack** from root folder, including **alfresco-ai-sync** and **ai-rag-framework**

   ```sh
   cd ..
   docker compose up --build --force-recreate
   ```

4. After a while, initial synchronization of the folder is finished by the **alfresco-ai-sync** service

   ```sh
   docker logs ai-framework-alfresco-ai-sync-1
    Successfully initialized with folder ID: [5730a944-248d-43cf-b0a9-44248d23cfec]
    Starting initial sync process.
    Starting initial synchronization for folder: AlfrescoSyncFolder[id=5730a944-248d-43cf-b0a9-44248d23cfec, publishedDate=null, updatedDate=2024-11-15T13:17Z, docLastUpdatedDate=2024-11-15T13:17:41.487Z]
    Initial synchronization for folder AlfrescoSyncFolder[id=5730a944-248d-43cf-b0a9-44248d23cfec, publishedDate=null, updatedDate=2024-11-15T13:17Z, docLastUpdatedDate=2024-11-15T13:17:41.487Z] complete. Processed 7 documents
    Finished initial sync process.
```

**Spring AI for ingestion**

The service **ai-rag-framework** is performing the ingestion of the documents by using following pieces of code:

Configuration for Vector Database (elasticsearch), ollama and embedding model is defined in [application.yml](https://github.com/aborroy/alfresco-ai-framework/blob/main/ai-rag-framework/src/main/resources/application.yml)

```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
  ai:
    ollama:
      base-url: http://localhost:11434
      embedding:
        options:
          model: nomic-embed-text
    vectorstore:
      elasticsearch:
        initialize-schema: true
        index-name: alfresco-ai-document-index
        dimensions: 768
```

The document is processed using the [TikaDocumentReader](https://github.com/spring-projects/spring-ai/blob/main/document-readers/tika-reader/src/main/java/org/springframework/ai/reader/tika/TikaDocumentReader.java) to extract its text. The extracted text is then split into smaller parts, suitable for the embedding model's dimension constraints, using the [TokenTextSplitter](https://github.com/spring-projects/spring-ai/blob/main/spring-ai-core/src/main/java/org/springframework/ai/transformer/splitter/TokenTextSplitter.java). These parts are used to calculate vector embeddings. Finally, the collection of chunks for the document—each containing the vector embedding and corresponding text—is stored in the vector database using the [VectorStore](https://github.com/spring-projects/spring-ai/blob/main/spring-ai-core/src/main/java/org/springframework/ai/vectorstore/VectorStore.java) object.


```java
// Embedding and Splitting
List<Document> documents =
    TokenTextSplitter.builder().build().apply(new TikaDocumentReader(file).get());

// Storing in Vector Database
vectorStore.add(documents);
```        


## Step 3: Verify the Vector Database Population

Once synchronization is complete, verify that the Vector Database (Elasticsearch) is populated with the documents from the Alfresco Knowledge Base using Kibana.

Access Kibana Developer Tools at http://localhost:5601/app/dev_tools#/console and type following request:

```
GET /alfresco-ai-document-index/_search
{
  "hits": {
    "total": {
      "value": 7,
      "relation": "eq"
    },
    "hits": [
      {
        "_index": "alfresco-ai-document-index",
        "_id": "f6532062-dc95-42fa-babf-dafb09614564",
        "_source": {
          "embedding": [
            0.013943307,
            0.05599264,
            ...
          ],
          "content": "Contents 2. Introduction to ...",
          "id": "f6532062-dc95-42fa-babf-dafb09614564",
          "metadata": {
            "fileName": "file.pdf",
            "documentId": "536fe0b0-cb3f-43f4-afe0-b0cb3f43f42c",
            "source": "cryptography-0.pdf",
            "folderId": "5730a944-248d-43cf-b0a9-44248d23cfec"
          }
        }
      }
      ...
    ]
  }
}
``` 

This result represents a **search query response** from Elasticsearch for the `alfresco-ai-document-index`.

Every hit includes the content of the document, including structured fields:

- **`embedding`:** An array of vector numbers representing the document's embedding in a multidimensional vector space. These are used for similarity searches.
- **`content`:** Extracted textual content of the document, to be provided as context for chatting.
- **`fileName`:** Name of the original file (`file.pdf`) in Alfresco Repository.
- **`documentId`:** A unique identifier for this document in Alfresco Repository.
- **`folderId`:** ID of the sync folder in the Alfresco Repository where the document resides.