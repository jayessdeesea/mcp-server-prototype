package user.jakecarr;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import user.jakecarr.model.FileMetadata;
import user.jakecarr.resources.FileContentResource;
import user.jakecarr.resources.FileMetadataResource;
import user.jakecarr.util.FileSystemUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Main server class for the Prototype MCP Server.
 * This server provides resources for accessing file metadata and content.
 */
public class PrototypeMCPServer {

    /**
     * Main method to start the server.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Configure java.util.logging to use Log4j2
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
        
        // Set up logging
        Logger logger = LogManager.getLogger(PrototypeMCPServer.class);
        logger.info("Initializing Prototype MCP Server");
        
        // Create ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        
        // Create resources
        FileContentResource fileContentResource = new FileContentResource();
        FileMetadataResource fileMetadataResource = new FileMetadataResource();
        
        try {
            // Create server info
            McpSchema.Implementation serverInfo = new McpSchema.Implementation("filesystem-mcp-server", "1.0.0");
            
            // Create transport provider
            StdioServerTransportProvider transportProvider = new StdioServerTransportProvider();
            
            // Create server using the builder pattern
            McpSyncServer server = McpServer.sync(transportProvider)
                .serverInfo(serverInfo)
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
                            List<FileMetadata> files = FileSystemUtils.listFiles(path, recursive);
                            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(files);
                            
                            List<McpSchema.Content> content = new ArrayList<>();
                            content.add(new McpSchema.TextContent(
                                null,
                                null,
                                json
                            ));
                            
                            return new McpSchema.CallToolResult(content, false);
                        } catch (Exception e) {
                            logger.error("Error listing files: {}", e.getMessage(), e);
                            
                            List<McpSchema.Content> content = new ArrayList<>();
                            content.add(new McpSchema.TextContent(
                                null,
                                null,
                                "Error listing files: " + e.getMessage()
                            ));
                            
                            return new McpSchema.CallToolResult(content, true);
                        }
                    }
                )
                .build();
                
            logger.info("Server started successfully");
            
            // Create a latch to keep the main thread alive
            final CountDownLatch latch = new CountDownLatch(1);
            
            // Add a shutdown hook to release the latch when the JVM is shutting down
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down server...");
                try {
                    // Any cleanup code can go here
                    server.close();
                } catch (Exception e) {
                    logger.error("Error during shutdown", e);
                } finally {
                    latch.countDown();
                }
                logger.info("Server shutdown complete");
            }));
            
            logger.info("Server is running. Press Ctrl+C to stop.");
            
            // Block the main thread until the latch is counted down (which happens in the shutdown hook)
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.warn("Server interrupted", e);
                Thread.currentThread().interrupt();
            }
            
        } catch (Exception e) {
            logger.error("Failed to start server", e);
            System.exit(1);
        }
    }
    
    /**
     * Create the JSON schema for the list_files tool.
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
