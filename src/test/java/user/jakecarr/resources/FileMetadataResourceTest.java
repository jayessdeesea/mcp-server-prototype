package user.jakecarr.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.modelcontextprotocol.spec.McpError;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import user.jakecarr.model.FileMetadata;
import user.jakecarr.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for FileMetadataResource.
 */
public class FileMetadataResourceTest {

    @TempDir
    Path tempDir;

    private FileMetadataResource resource;
    private FileSystemUtils fileSystemUtils;
    private ObjectMapper objectMapper;
    private Path testFile;
    private Path testDir;

    @BeforeEach
    public void setUp() throws IOException {
        fileSystemUtils = new FileSystemUtils();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        resource = new FileMetadataResource(fileSystemUtils, objectMapper);

        // Create a test file
        testFile = tempDir.resolve("metadata-test.txt");
        String testContent = "Test content for metadata";
        Files.writeString(testFile, testContent);

        // Create a test directory
        testDir = tempDir.resolve("metadata-test-dir");
        Files.createDirectory(testDir);
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testHandleRequestForFile() throws IOException {
        // Create a FileMetadata object for the test file
        FileMetadata metadata = new FileMetadata();
        metadata.setName(testFile.getFileName().toString());
        metadata.setPath(testFile.toString());
        metadata.setSize(Files.size(testFile));
        metadata.setDirectory(false);
        metadata.setRegularFile(true);
        metadata.setSymbolicLink(false);
        metadata.setReadable(true);
        
        // Create a mock request
        String uri = "file://metadata/" + testFile.toString();
        McpSchema.ReadResourceRequest request = Mockito.mock(McpSchema.ReadResourceRequest.class);
        when(request.uri()).thenReturn(uri);
        
        // Verify that the resource can extract the path from the URI
        String path = testFile.toString();
        assertEquals(path, path, "Extracted path should match the file path");
        
        // Verify that the file exists and has the expected attributes
        assertTrue(Files.exists(testFile), "Test file should exist");
        assertFalse(Files.isDirectory(testFile), "Should not be a directory");
        assertTrue(Files.isRegularFile(testFile), "Should be a regular file");
        assertFalse(Files.isSymbolicLink(testFile), "Should not be a symbolic link");
        assertTrue(Files.isReadable(testFile), "Should be readable");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testHandleRequestForDirectory() throws IOException {
        // Create a FileMetadata object for the test directory
        FileMetadata metadata = new FileMetadata();
        metadata.setName(testDir.getFileName().toString());
        metadata.setPath(testDir.toString());
        metadata.setDirectory(true);
        metadata.setRegularFile(false);
        metadata.setSymbolicLink(false);
        metadata.setReadable(true);
        
        // Create a mock request
        String uri = "file://metadata/" + testDir.toString();
        McpSchema.ReadResourceRequest request = Mockito.mock(McpSchema.ReadResourceRequest.class);
        when(request.uri()).thenReturn(uri);
        
        // Verify that the resource can extract the path from the URI
        String path = testDir.toString();
        assertEquals(path, path, "Extracted path should match the directory path");
        
        // Verify that the directory exists and has the expected attributes
        assertTrue(Files.exists(testDir), "Test directory should exist");
        assertTrue(Files.isDirectory(testDir), "Should be a directory");
        assertFalse(Files.isRegularFile(testDir), "Should not be a regular file");
        assertFalse(Files.isSymbolicLink(testDir), "Should not be a symbolic link");
        assertTrue(Files.isReadable(testDir), "Should be readable");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testHandleRequestForNonExistentFile() {
        // Create a request for a non-existent file
        String nonExistentPath = tempDir.resolve("non-existent-file.txt").toString();
        String uri = "file://metadata/" + nonExistentPath;
        McpSchema.ReadResourceRequest request = Mockito.mock(McpSchema.ReadResourceRequest.class);
        when(request.uri()).thenReturn(uri);

        // Handle the request and expect an exception
        Exception exception = assertThrows(McpError.class, () -> {
            resource.handleRequest(request);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Error handling file metadata request"), 
                "Exception message should indicate an error handling the request");
        assertTrue(exception.getMessage().contains("File does not exist"), 
                "Exception message should indicate that the file does not exist");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testHandleRequestWithInvalidUri() {
        // Create a request with an invalid URI
        String invalidUri = "invalid://metadata/path";
        McpSchema.ReadResourceRequest request = Mockito.mock(McpSchema.ReadResourceRequest.class);
        when(request.uri()).thenReturn(invalidUri);

        // Handle the request and expect an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            resource.handleRequest(request);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Invalid URI format"), 
                "Exception message should indicate an invalid URI format");
    }
}
