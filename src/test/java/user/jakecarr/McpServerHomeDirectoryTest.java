package user.jakecarr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIf;
import user.jakecarr.model.FileMetadata;
import user.jakecarr.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class specifically for testing the MCP server's ability to list files in the home directory.
 * This test is designed to diagnose why the MCP server might not be returning results when
 * listing files in the home directory.
 */
public class McpServerHomeDirectoryTest {
    
    private String homeDir;
    private McpSyncClient client;
    private ObjectMapper objectMapper;
    private FileSystemUtils fileSystemUtils;
    private static final Path JAR_PATH = Paths.get("target/prototype-mcp-1.0-SNAPSHOT-jar-with-dependencies.jar");
    
    /**
     * Check if the jar file exists to determine if MCP client tests should run
     */
    public static boolean isJarFileAvailable() {
        return Files.exists(JAR_PATH);
    }
    
    @BeforeEach
    public void setUp() throws Exception {
        homeDir = System.getProperty("user.home");
        objectMapper = new ObjectMapper();
        fileSystemUtils = new FileSystemUtils();
        
        // Only initialize the client if the jar file exists
        if (isJarFileAvailable()) {
            // Create client info
            McpSchema.Implementation clientInfo = new McpSchema.Implementation("test-client", "1.0.0");
            
            // Create server parameters - using the local server jar
            ServerParameters serverParams = ServerParameters.builder("java")
                .args("-jar", JAR_PATH.toString())
                .build();
            
            // Create transport with server parameters
            StdioClientTransport transport = new StdioClientTransport(serverParams);
            
            // Create the client
            client = McpClient.sync(transport)
                .clientInfo(clientInfo)
                .build();
            
            // Initialize the client (connects to the server)
            client.initialize();
        } else {
            System.out.println("Jar file not found at: " + JAR_PATH.toAbsolutePath());
            client = null;
        }
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        if (client != null) {
            client.close();
        }
    }
    
    @Test
    @Timeout(10) // 10 seconds timeout
    public void testDirectJavaListingOfHomeDirectory() throws IOException {
        System.out.println("Home directory path: " + homeDir);
        
        // Verify the home directory exists and is readable
        Path homePath = Paths.get(homeDir);
        assertTrue(Files.exists(homePath), "Home directory should exist");
        assertTrue(Files.isDirectory(homePath), "Home directory should be a directory");
        assertTrue(Files.isReadable(homePath), "Home directory should be readable");
        
        // Use Java's Files API directly to list files
        List<Path> paths = Files.list(homePath).toList();
        
        // Print the results for debugging
        System.out.println("Found " + paths.size() + " files/directories using Java API:");
        for (Path path : paths) {
            System.out.println("- " + path.getFileName() + " (Full path: " + path + ")");
        }
        
        // Verify that files were found
        assertFalse(paths.isEmpty(), "Home directory should not be empty when using Java API");
    }
    
    @Test
    @Timeout(10) // 10 seconds timeout
    public void testFileSystemUtilsListingOfHomeDirectory() throws IOException {
        System.out.println("Home directory path: " + homeDir);
        
        // Verify the home directory exists and is readable
        Path homePath = Paths.get(homeDir);
        assertTrue(Files.exists(homePath), "Home directory should exist");
        assertTrue(Files.isDirectory(homePath), "Home directory should be a directory");
        assertTrue(Files.isReadable(homePath), "Home directory should be readable");
        
        try {
            // List files in the home directory (non-recursive)
            List<FileMetadata> files = fileSystemUtils.listFiles(homeDir, false);
            
            // Print the results for debugging
            System.out.println("Found " + files.size() + " files/directories using FileSystemUtils:");
            for (FileMetadata file : files) {
                System.out.println("- " + file.getName() + " (Path: " + file.getPath() + ")");
            }
            
            // Verify that files were found
            assertFalse(files.isEmpty(), "Home directory should not be empty when using FileSystemUtils");
        } catch (Exception e) {
            System.err.println("Exception when listing home directory with FileSystemUtils: " + e.getMessage());
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    @Timeout(10) // 10 seconds timeout
    @EnabledIf("isJarFileAvailable") // Only run this test if the jar file exists
    public void testMcpClientListingOfHomeDirectory() {
        System.out.println("Home directory path: " + homeDir);
        
        // Skip this test if the jar file doesn't exist or client initialization failed
        if (client == null) {
            System.out.println("Skipping MCP client test because client initialization failed");
            return;
        }
        
        try {
            // Create the tool arguments
            Map<String, Object> toolArgs = new HashMap<>();
            toolArgs.put("path", homeDir);
            toolArgs.put("recursive", false);
            
            // Call the list_files tool
            McpSchema.CallToolRequest toolRequest = new McpSchema.CallToolRequest("list_files", toolArgs);
            McpSchema.CallToolResult toolResponse = client.callTool(toolRequest);
            
            // Check for errors
            assertFalse(toolResponse.isError(), "Tool call should not result in an error");
            
            // Access the tool response content
            if (toolResponse.content() != null && !toolResponse.content().isEmpty()) {
                McpSchema.Content content = toolResponse.content().get(0);
                if (content instanceof McpSchema.TextContent textContent) {
                    System.out.println("MCP Client response for home directory listing:");
                    System.out.println(textContent.text());
                    
                    // Parse the JSON response
                    List<FileMetadata> files = objectMapper.readValue(
                        textContent.text(), 
                        new TypeReference<List<FileMetadata>>() {}
                    );
                    
                    // Verify that files were found
                    assertFalse(files.isEmpty(), "Home directory should not be empty when using MCP client");
                    
                    // Print the results in a more readable format
                    System.out.println("Found " + files.size() + " files/directories using MCP client:");
                    for (FileMetadata file : files) {
                        System.out.println("- " + file.getName() + " (Path: " + file.getPath() + ")");
                    }
                } else {
                    fail("Expected TextContent but got: " + content.getClass().getSimpleName());
                }
            } else {
                fail("No content in tool response");
            }
        } catch (Exception e) {
            System.err.println("Exception when listing home directory with MCP client: " + e.getMessage());
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }
}
