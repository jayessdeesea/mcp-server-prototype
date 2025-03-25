package user.jakecarr.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import user.jakecarr.model.FileMetadata;
import user.jakecarr.util.FileSystemUtils;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Resource handler for file metadata.
 */
public class FileMetadataResource {
    private static final Logger logger = LogManager.getLogger(FileMetadataResource.class);
    private static final String URI_PREFIX = "file://metadata/";
    
    private final FileSystemUtils fileSystemUtils;
    private final ObjectMapper objectMapper;
    
    /**
     * Constructor for Spring dependency injection.
     * 
     * @param fileSystemUtils The FileSystemUtils dependency
     * @param objectMapper The ObjectMapper dependency
     */
    public FileMetadataResource(FileSystemUtils fileSystemUtils, ObjectMapper objectMapper) {
        this.fileSystemUtils = fileSystemUtils;
        this.objectMapper = objectMapper;
        logger.debug("FileMetadataResource constructed");
    }
    
    /**
     * Initialization method called by Spring after dependency injection.
     */
    @PostConstruct
    public void initialize() {
        logger.info("Initializing FileMetadataResource");
    }
    
    /**
     * Cleanup method called by Spring before bean destruction.
     */
    @PreDestroy
    public void cleanup() {
        logger.info("Cleaning up FileMetadataResource");
    }
    
    /**
     * Handle a request for file metadata.
     * 
     * @param request The request
     * @return The response
     */
    public McpSchema.ReadResourceResult handleRequest(McpSchema.ReadResourceRequest request) {
        String uri = request.uri();
        logger.debug("Handling file metadata request for URI: {}", uri);
        
        try {
            String filePath = fileSystemUtils.extractPathFromUri(uri, URI_PREFIX);
            FileMetadata metadata = fileSystemUtils.getFileMetadata(filePath);
            String json = serializeMetadata(metadata);
            
            logger.debug("File metadata request handled successfully for URI: {}", uri);
            
            List<McpSchema.ResourceContents> contents = new ArrayList<>();
            contents.add(new McpSchema.TextResourceContents(
                uri,
                "application/json",
                json
            ));
            
            return new McpSchema.ReadResourceResult(contents);
        } catch (IOException e) {
            logger.error("Error handling file metadata request for URI: {}", uri, e);
            throw new McpError("Error handling file metadata request: " + e.getMessage());
        }
    }
    
    /**
     * Serialize file metadata to JSON.
     * 
     * @param metadata The file metadata
     * @return The JSON string
     * @throws JsonProcessingException If an error occurs during serialization
     */
    private String serializeMetadata(FileMetadata metadata) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata);
    }
}
