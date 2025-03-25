package user.jakecarr.resources;

import user.jakecarr.util.FileSystemUtils;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Resource handler for file content.
 */
public class FileContentResource {
    private static final Logger logger = LogManager.getLogger(FileContentResource.class);
    private static final String URI_PREFIX = "file://content/";
    
    private final FileSystemUtils fileSystemUtils;
    
    /**
     * Constructor for Spring dependency injection.
     * 
     * @param fileSystemUtils The FileSystemUtils dependency
     */
    public FileContentResource(FileSystemUtils fileSystemUtils) {
        this.fileSystemUtils = fileSystemUtils;
        logger.debug("FileContentResource constructed");
    }
    
    /**
     * Initialization method called by Spring after dependency injection.
     */
    @PostConstruct
    public void initialize() {
        logger.info("Initializing FileContentResource");
    }
    
    /**
     * Cleanup method called by Spring before bean destruction.
     */
    @PreDestroy
    public void cleanup() {
        logger.info("Cleaning up FileContentResource");
    }
    
    /**
     * Handle a request for file content.
     * 
     * @param request The request
     * @return The response
     */
    public McpSchema.ReadResourceResult handleRequest(McpSchema.ReadResourceRequest request) {
        String uri = request.uri();
        logger.debug("Handling file content request for URI: {}", uri);
        
        try {
            String filePath = fileSystemUtils.extractPathFromUri(uri, URI_PREFIX);
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                logger.warn("File does not exist: {}", filePath);
                throw new IOException("File does not exist: " + filePath);
            }
            
            if (Files.isDirectory(path)) {
                logger.warn("Cannot read content of a directory: {}", filePath);
                throw new IOException("Cannot read content of a directory: " + filePath);
            }
            
            String content;
            String mimeType;
            
            if (fileSystemUtils.isTextFile(filePath)) {
                content = fileSystemUtils.readTextFile(filePath);
                mimeType = determineMimeType(filePath);
                logger.debug("Read text file: {}", filePath);
            } else {
                content = fileSystemUtils.readBinaryFile(filePath);
                mimeType = "application/octet-stream;base64";
                logger.debug("Read binary file: {}", filePath);
            }
            
            logger.debug("File content request handled successfully for URI: {}", uri);
            
            List<McpSchema.ResourceContents> contents = new ArrayList<>();
            contents.add(new McpSchema.TextResourceContents(
                uri,
                mimeType,
                content
            ));
            
            return new McpSchema.ReadResourceResult(contents);
        } catch (IOException e) {
            logger.error("Error handling file content request for URI: {}", uri, e);
            throw new McpError("Error handling file content request: " + e.getMessage());
        }
    }
    
    /**
     * Determine the MIME type of a file based on its extension.
     * 
     * @param filePath The file path
     * @return The MIME type
     */
    private String determineMimeType(String filePath) {
        String lowerCasePath = filePath.toLowerCase();
        
        if (lowerCasePath.endsWith(".txt")) {
            return "text/plain";
        } else if (lowerCasePath.endsWith(".html") || lowerCasePath.endsWith(".htm")) {
            return "text/html";
        } else if (lowerCasePath.endsWith(".css")) {
            return "text/css";
        } else if (lowerCasePath.endsWith(".js")) {
            return "application/javascript";
        } else if (lowerCasePath.endsWith(".json")) {
            return "application/json";
        } else if (lowerCasePath.endsWith(".xml")) {
            return "application/xml";
        } else if (lowerCasePath.endsWith(".md")) {
            return "text/markdown";
        } else if (lowerCasePath.endsWith(".csv")) {
            return "text/csv";
        } else if (lowerCasePath.endsWith(".java")) {
            return "text/x-java-source";
        } else if (lowerCasePath.endsWith(".py")) {
            return "text/x-python";
        } else if (lowerCasePath.endsWith(".c") || lowerCasePath.endsWith(".cpp") || lowerCasePath.endsWith(".h")) {
            return "text/x-c";
        } else {
            return "text/plain";
        }
    }
}
