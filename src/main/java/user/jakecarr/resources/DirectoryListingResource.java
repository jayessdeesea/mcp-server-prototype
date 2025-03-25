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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resource handler for directory listing.
 * This resource provides a list of files in a directory.
 */
public class DirectoryListingResource {
    private static final Logger logger = LogManager.getLogger(DirectoryListingResource.class);
    private static final String URI_PREFIX = "file://directory/";
    private static final Pattern RECURSIVE_PARAM_PATTERN = Pattern.compile("[?&]recursive=(true|false)");
    
    private final FileSystemUtils fileSystemUtils;
    private final ObjectMapper objectMapper;
    
    /**
     * Constructor for Spring dependency injection.
     * 
     * @param fileSystemUtils The FileSystemUtils dependency
     * @param objectMapper The ObjectMapper dependency
     */
    public DirectoryListingResource(FileSystemUtils fileSystemUtils, ObjectMapper objectMapper) {
        this.fileSystemUtils = fileSystemUtils;
        this.objectMapper = objectMapper;
        logger.debug("DirectoryListingResource constructed");
    }
    
    /**
     * Initialization method called by Spring after dependency injection.
     */
    @PostConstruct
    public void initialize() {
        logger.info("Initializing DirectoryListingResource");
    }
    
    /**
     * Cleanup method called by Spring before bean destruction.
     */
    @PreDestroy
    public void cleanup() {
        logger.info("Cleaning up DirectoryListingResource");
    }
    
    /**
     * Handle a request for directory listing.
     * 
     * @param request The request
     * @return The response
     */
    public McpSchema.ReadResourceResult handleRequest(McpSchema.ReadResourceRequest request) {
        String uri = request.uri();
        logger.debug("Handling directory listing request for URI: {}", uri);
        
        try {
            // Extract path from URI
            String uriWithoutParams = uri.contains("?") ? uri.substring(0, uri.indexOf("?")) : uri;
            String directoryPath = fileSystemUtils.extractPathFromUri(uriWithoutParams, URI_PREFIX);
            
            // Extract recursive parameter
            boolean recursive = false;
            Matcher matcher = RECURSIVE_PARAM_PATTERN.matcher(uri);
            if (matcher.find()) {
                recursive = Boolean.parseBoolean(matcher.group(1));
            }
            
            logger.debug("Listing directory: {}, recursive: {}", directoryPath, recursive);
            
            // Get file listing
            List<FileMetadata> files = fileSystemUtils.listFiles(directoryPath, recursive);
            String json = serializeFileList(files);
            
            logger.debug("Directory listing request handled successfully for URI: {}", uri);
            
            List<McpSchema.ResourceContents> contents = new ArrayList<>();
            contents.add(new McpSchema.TextResourceContents(
                uri,
                "application/json",
                json
            ));
            
            return new McpSchema.ReadResourceResult(contents);
        } catch (IOException e) {
            logger.error("Error handling directory listing request for URI: {}", uri, e);
            throw new McpError("Error handling directory listing request: " + e.getMessage());
        }
    }
    
    /**
     * Serialize a list of file metadata to JSON.
     * 
     * @param files The list of file metadata
     * @return The JSON string
     * @throws JsonProcessingException If an error occurs during serialization
     */
    private String serializeFileList(List<FileMetadata> files) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(files);
    }
}
