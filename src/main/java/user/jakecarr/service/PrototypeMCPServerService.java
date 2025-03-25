package user.jakecarr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpError;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import user.jakecarr.FileSystemServer;
import user.jakecarr.model.FileMetadata;
import user.jakecarr.resources.DirectoryListingResource;
import user.jakecarr.resources.FileContentResource;
import user.jakecarr.resources.FileMetadataResource;
import user.jakecarr.util.FileSystemUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing the MCP server lifecycle and tools.
 */
@Service
public class PrototypeMCPServerService {
    private static final Logger logger = LogManager.getLogger(PrototypeMCPServerService.class);
    
    private final ApplicationContext applicationContext;
    private final FileSystemServer fileSystemServer;
    private final FileSystemUtils fileSystemUtils;
    private final ObjectMapper objectMapper;
    private McpSyncServer mcpServer;
    
    /**
     * Constructor for Spring dependency injection.
     * 
     * @param applicationContext The Spring application context
     * @param fileSystemServer The FileSystemServer dependency
     * @param fileSystemUtils The FileSystemUtils dependency
     * @param objectMapper The ObjectMapper dependency
     */
    @Autowired
    public PrototypeMCPServerService(ApplicationContext applicationContext,
                           FileSystemServer fileSystemServer,
                           FileSystemUtils fileSystemUtils,
                           ObjectMapper objectMapper) {
        this.applicationContext = applicationContext;
        this.fileSystemServer = fileSystemServer;
        this.fileSystemUtils = fileSystemUtils;
        this.objectMapper = objectMapper;
        logger.debug("PrototypeMCPServerService constructed");
    }
    
    /**
     * Initialize the MCP server.
     * This method is called by Spring after the bean is constructed.
     */
    @PostConstruct
    public void initialize() {
        logger.info("Initializing MCP server");
        
        try {
            // Create server info
            McpSchema.Implementation serverInfo = new McpSchema.Implementation("filesystem-mcp-server", "1.0.0");
            
            // Create transport provider
            StdioServerTransportProvider transportProvider = new StdioServerTransportProvider();
            
            // Get resources
            FileMetadataResource metadataResource = fileSystemServer.getMetadataResource();
            FileContentResource contentResource = fileSystemServer.getContentResource();
            DirectoryListingResource directoryListingResource = fileSystemServer.getDirectoryListingResource();
            
            // Create server using the builder pattern
            mcpServer = McpServer.sync(transportProvider)
                .serverInfo(serverInfo)
                // Register the list_files tool
                .tool(
                    new McpSchema.Tool(
                        "list_files",
                        "List files in a directory",
                        createListFilesSchema()
                    ),
                    (exchange, toolArgs) -> {
                        String path = (String) toolArgs.get("path");
                        Boolean recursive = toolArgs.containsKey("recursive") ? (Boolean) toolArgs.get("recursive") : false;
                        
                        try {
                            List<FileMetadata> files = fileSystemUtils.listFiles(path, recursive);
                            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(files);
                            
                            List<McpSchema.Content> content = new ArrayList<>();
                            content.add(new McpSchema.TextContent(json));
                            
                            return new McpSchema.CallToolResult(content, false);
                        } catch (Exception e) {
                            logger.error("Error listing files: {}", e.getMessage(), e);
                            
                            List<McpSchema.Content> content = new ArrayList<>();
                            content.add(new McpSchema.TextContent("Error listing files: " + e.getMessage()));
                            
                            return new McpSchema.CallToolResult(content, true);
                        }
                    }
                )
                // Register the get_file_metadata tool
                .tool(
                    new McpSchema.Tool(
                        "get_file_metadata",
                        "Get metadata for a file or directory",
                        createFileMetadataSchema()
                    ),
                    (exchange, toolArgs) -> {
                        String path = (String) toolArgs.get("path");
                        
                        try {
                            FileMetadata metadata = fileSystemUtils.getFileMetadata(path);
                            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata);
                            
                            List<McpSchema.Content> content = new ArrayList<>();
                            content.add(new McpSchema.TextContent(json));
                            
                            return new McpSchema.CallToolResult(content, false);
                        } catch (Exception e) {
                            logger.error("Error getting file metadata: {}", e.getMessage(), e);
                            
                            List<McpSchema.Content> content = new ArrayList<>();
                            content.add(new McpSchema.TextContent("Error getting file metadata: " + e.getMessage()));
                            
                            return new McpSchema.CallToolResult(content, true);
                        }
                    }
                )
                // Register the get_file_content tool
                .tool(
                    new McpSchema.Tool(
                        "get_file_content",
                        "Get content of a file",
                        createFileContentSchema()
                    ),
                    (exchange, toolArgs) -> {
                        String path = (String) toolArgs.get("path");
                        
                        try {
                            String content;
                            String mimeType;
                            
                            if (fileSystemUtils.isTextFile(path)) {
                                content = fileSystemUtils.readTextFile(path);
                                mimeType = "text/plain";
                            } else {
                                content = fileSystemUtils.readBinaryFile(path);
                                mimeType = "application/octet-stream;base64";
                            }
                            
                            List<McpSchema.Content> contentList = new ArrayList<>();
                            contentList.add(new McpSchema.TextContent(
                                content
                            ));
                            
                            return new McpSchema.CallToolResult(contentList, false);
                        } catch (Exception e) {
                            logger.error("Error reading file content: {}", e.getMessage(), e);
                            
                            List<McpSchema.Content> content = new ArrayList<>();
                            content.add(new McpSchema.TextContent("Error reading file content: " + e.getMessage()));
                            
                            return new McpSchema.CallToolResult(content, true);
                        }
                    }
                )
                .build();
            
            logger.info("MCP server initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize MCP server", e);
            throw new RuntimeException("Failed to initialize MCP server", e);
        }
    }
    
    /**
     * Create the JSON schema for the get_file_metadata tool.
     * 
     * @return The JSON schema
     */
    private static McpSchema.JsonSchema createFileMetadataSchema() {
        // Create input schema for the tool
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> path = new HashMap<>();
        path.put("type", "string");
        path.put("description", "Path to the file or directory");
        
        properties.put("path", path);
        
        List<String> required = List.of("path");
        
        return new McpSchema.JsonSchema("object", properties, required, null);
    }
    
    /**
     * Create the JSON schema for the get_file_content tool.
     * 
     * @return The JSON schema
     */
    private static McpSchema.JsonSchema createFileContentSchema() {
        // Create input schema for the tool
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> path = new HashMap<>();
        path.put("type", "string");
        path.put("description", "Path to the file");
        
        properties.put("path", path);
        
        List<String> required = List.of("path");
        
        return new McpSchema.JsonSchema("object", properties, required, null);
    }
    
    /**
     * Start the MCP server.
     * 
     * @throws Exception If an error occurs
     */
    public void start() throws Exception {
        logger.info("Starting MCP server");
        
        // The server is already initialized in the initialize() method
        // We don't need to block here since we're using a keep-alive thread in the application
        
        logger.info("MCP server started successfully");
    }
    
    /**
     * Shutdown the MCP server.
     * This method is called by Spring before the bean is destroyed.
     */
    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down MCP server");
        try {
            if (mcpServer != null) {
                mcpServer.close();
                mcpServer = null;
            }
        } catch (Exception e) {
            logger.error("Error during shutdown", e);
        }
        logger.info("MCP server shutdown complete");
    }
    
    /**
     * Create the JSON schema for the list_files tool.
     * 
     * @return The JSON schema
     */
    private static McpSchema.JsonSchema createListFilesSchema() {
        // Create input schema for the tool
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> path = new HashMap<>();
        path.put("type", "string");
        path.put("description", "Directory path to list files from");
        
        Map<String, Object> recursive = new HashMap<>();
        recursive.put("type", "boolean");
        recursive.put("description", "Whether to list files recursively");
        
        properties.put("path", path);
        properties.put("recursive", recursive);
        
        List<String> required = List.of("path");
        
        return new McpSchema.JsonSchema("object", properties, required, null);
    }
}
