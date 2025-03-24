package user.jakecarr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import io.modelcontextprotocol.spec.McpSchema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to check MCP SDK integration.
 */
public class McpSdkTest {

    @TempDir
    Path tempDir;

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testMcpSdkImports() {
        // This test is just to check if the MCP SDK is properly configured
        assertDoesNotThrow(() -> {
            // Check that we can import the MCP SDK classes
            McpSchema.Implementation serverInfo = new McpSchema.Implementation("test-server", "1.0.0");
            assertNotNull(serverInfo);
        });
    }
    
    @Test
    @Timeout(5) // 5 seconds timeout
    public void testResourceHandling() throws IOException {
        // Create a test file
        Path testFile = tempDir.resolve("resource-test.txt");
        String testContent = "Test content for resource handling";
        Files.writeString(testFile, testContent);
        
        // Verify the file exists and has the expected content
        assertTrue(Files.exists(testFile), "Test file should exist");
        assertEquals(testContent, Files.readString(testFile), "File content should match");
        
        // Test that we can construct a URI for the file
        String metadataUri = "file://metadata/" + testFile.toString();
        String contentUri = "file://content/" + testFile.toString();
        
        assertNotNull(metadataUri, "Metadata URI should not be null");
        assertNotNull(contentUri, "Content URI should not be null");
        assertTrue(metadataUri.startsWith("file://metadata/"), "Metadata URI should have the correct prefix");
        assertTrue(contentUri.startsWith("file://content/"), "Content URI should have the correct prefix");
    }
    
    @Test
    @Timeout(5) // 5 seconds timeout
    public void testUriExtraction() {
        // Test URI extraction logic
        String metadataUri = "file://metadata/C:/path/to/file.txt";
        String contentUri = "file://content/C:/path/to/file.txt";
        
        String metadataPrefix = "file://metadata/";
        String contentPrefix = "file://content/";
        
        String metadataPath = metadataUri.substring(metadataPrefix.length());
        String contentPath = contentUri.substring(contentPrefix.length());
        
        assertEquals("C:/path/to/file.txt", metadataPath, "Extracted path should match");
        assertEquals("C:/path/to/file.txt", contentPath, "Extracted path should match");
    }
    
}
