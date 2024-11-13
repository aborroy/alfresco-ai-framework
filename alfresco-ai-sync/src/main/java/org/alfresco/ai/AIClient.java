package org.alfresco.ai;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * REST client for interacting with the Alfresco AI service.
 * This service handles document upload and deletion operations.
 */
@Service
public class AIClient {

    private static final String DOCUMENTS_ENDPOINT = "/documents";
    private static final String DOCUMENT_ID_PARAM = "documentId";
    private static final String FOLDERS_ENDPOINT = "/folders";
    private static final String FOLDER_ID_PARAM = "folderId";
    private static final String FILE_NAME_PARAM = "fileName";
    private static final String FILE_PARAM = "file";

    @Value("${alfresco.ai.base.url}")
    private String aiBaseUrl;

    /**
     * Uploads a document to the AI service.
     *
     * @param documentId   Unique identifier for the document (required)
     * @param syncFolderId Unique identifier for the synchronization folder (required)
     * @param fileName     Name of the file being uploaded (required)
     * @param inputStream  Input stream containing the file data (required)
     * @return Response from the AI service
     * @throws IllegalArgumentException if any required parameters are null or empty
     * @throws IOException if there's an error during the upload process
     */
    public String uploadDocument(String documentId, String syncFolderId, String fileName, InputStream inputStream) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = createUploadRequest(documentId, syncFolderId, fileName, inputStream);
            return httpClient.execute(httpPost, new BasicHttpClientResponseHandler());
        }
    }

    /**
     * Deletes a document from the AI service.
     *
     * @param documentId Unique identifier of the document to delete (required)
     * @return Response from the AI service
     * @throws IllegalArgumentException if documentId is null or empty
     * @throws IOException if there's an error during the deletion process
     */
    public String deleteDocument(String documentId) throws IOException {
        String encodedDocumentId = URLEncoder.encode(documentId, StandardCharsets.UTF_8);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete httpDelete = new HttpDelete(aiBaseUrl + DOCUMENTS_ENDPOINT + "?" + DOCUMENT_ID_PARAM + "=" + encodedDocumentId);
            return httpClient.execute(httpDelete, new BasicHttpClientResponseHandler());
        }
    }

    /**
     * Deletes all documents of a folder from the AI service.
     *
     * @param folderId Unique identifier of the folder to delete (required)
     * @return Response from the AI service
     * @throws IllegalArgumentException if documentId is null or empty
     * @throws IOException if there's an error during the deletion process
     */
    public String deleteDocumentsFromFolder(String folderId) throws IOException {
        String encodedFolderId = URLEncoder.encode(folderId, StandardCharsets.UTF_8);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete httpDelete = new HttpDelete(aiBaseUrl + FOLDERS_ENDPOINT + "?" + FOLDER_ID_PARAM + "=" + encodedFolderId);
            return httpClient.execute(httpDelete, new BasicHttpClientResponseHandler());
        }
    }

    private HttpPost createUploadRequest(String documentId, String folderId, String fileName, InputStream inputStream) {
        HttpPost httpPost = new HttpPost(aiBaseUrl + DOCUMENTS_ENDPOINT);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .addTextBody(DOCUMENT_ID_PARAM, documentId, ContentType.TEXT_PLAIN)
                .addTextBody(FOLDER_ID_PARAM, folderId, ContentType.TEXT_PLAIN)
                .addTextBody(FILE_NAME_PARAM, fileName, ContentType.TEXT_PLAIN)
                .addBinaryBody(
                        FILE_PARAM,
                        inputStream,
                        ContentType.APPLICATION_OCTET_STREAM,
                        fileName
                );
        httpPost.setEntity(builder.build());
        return httpPost;
    }

}