package user.jakecarr.resources;

import io.modelcontextprotocol.spec.McpError;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import user.jakecarr.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for FileContentResource.
 */
public class FileContentResourceTest {

    @TempDir
    Path tempDir;

    private FileContentResource resource;
    private FileSystemUtils fileSystemUtils;
    private Path textFile;
    private Path binaryFile;
    private Path directory;

    @BeforeEach
    public void setUp() throws IOException {
        fileSystemUtils = new FileSystemUtils();
        resource = new FileContentResource(fileSystemUtils);

        // Create a text file
        textFile = tempDir.resolve("content-test.txt");
        String textContent = "This is a test file with text content.";
        Files.writeString(textFile, textContent);

        // Create a binary file
        binaryFile = tempDir.resolve("content-test.bin");
        byte[] binaryContent = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04 };
        Files.write(binaryFile, binaryContent);

        // Create a directory
        directory = tempDir.resolve("content-test-dir");
        Files.createDirectory(directory);
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testHandleRequestForTextFile() throws IOException {
        // Create a request for text file content
        String uri = "file://content/" + textFile.toString();
        McpSchema.ReadResourceRequest request = Mockito.mock(McpSchema.ReadResourceRequest.class);
        when(request.uri()).thenReturn(uri);

        // Handle the request
        McpSchema.ReadResourceResult result = resource.handleRequest(request);

        // Verify the result
        assertNotNull(result, "Result should not be null");
        List<McpSchema.ResourceContents> contents = result.contents();
        assertNotNull(contents, "Contents should not be null");
        assertEquals(1, contents.size(), "Should have one content item");

        McpSchema.ResourceContents content = contents.get(0);
        assertEquals(uri, content.uri(), "URI should match");
        assertEquals("text/plain", content.mimeType(), "MIME type should be text/plain");

        // Verify the content
        String fileContent = ((McpSchema.TextResourceContents) content).text();
        assertEquals("This is a test file with text content.", fileContent, "File content should match");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testHandleRequestForBinaryFile() throws IOException {
        // Create a request for binary file content
        String uri = "file://content/" + binaryFile.toString();
        McpSchema.ReadResourceRequest request = Mockito.mock(McpSchema.ReadResourceRequest.class);
        when(request.uri()).thenReturn(uri);

        // Handle the request
        McpSchema.ReadResourceResult result = resource.handleRequest(request);

        // Verify the result
        assertNotNull(result, "Result should not be null");
        List<McpSchema.ResourceContents> contents = result.contents();
        assertNotNull(contents, "Contents should not be null");
        assertEquals(1, contents.size(), "Should have one content item");

        McpSchema.ResourceContents content = contents.get(0);
        assertEquals(uri, content.uri(), "URI should match");
        assertEquals("application/octet-stream;base64", content.mimeType(), "MIME type should indicate base64 encoding");

        // Verify the content (base64 encoded)
        String base64Content = ((McpSchema.TextResourceContents) content).text();
        byte[] decodedContent = Base64.getDecoder().decode(base64Content);
        assertArrayEquals(new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04 }, decodedContent, "Binary content should match");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testHandleRequestForDirectory() {
        // Create a request for directory content
        String uri = "file://content/" + directory.toString();
        McpSchema.ReadResourceRequest request = Mockito.mock(McpSchema.ReadResourceRequest.class);
        when(request.uri()).thenReturn(uri);

        // Handle the request and expect an exception
        Exception exception = assertThrows(McpError.class, () -> {
            resource.handleRequest(request);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Error handling file content request"), 
                "Exception message should indicate an error handling the request");
        assertTrue(exception.getMessage().contains("Cannot read content of a directory"), 
                "Exception message should indicate that the path is a directory");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testHandleRequestForNonExistentFile() {
        // Create a request for a non-existent file
        String nonExistentPath = tempDir.resolve("non-existent-file.txt").toString();
        String uri = "file://content/" + nonExistentPath;
        McpSchema.ReadResourceRequest request = Mockito.mock(McpSchema.ReadResourceRequest.class);
        when(request.uri()).thenReturn(uri);

        // Handle the request and expect an exception
        Exception exception = assertThrows(McpError.class, () -> {
            resource.handleRequest(request);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Error handling file content request"), 
                "Exception message should indicate an error handling the request");
        assertTrue(exception.getMessage().contains("File does not exist"), 
                "Exception message should indicate that the file does not exist");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testHandleRequestWithInvalidUri() {
        // Create a request with an invalid URI
        String invalidUri = "invalid://content/path";
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

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testMimeTypeDetection() throws IOException {
        // Create files with different extensions
        Path htmlFile = tempDir.resolve("test.html");
        Path cssFile = tempDir.resolve("test.css");
        Path jsFile = tempDir.resolve("test.js");
        Path jsonFile = tempDir.resolve("test.json");
        Path xmlFile = tempDir.resolve("test.xml");
        Path mdFile = tempDir.resolve("test.md");
        Path csvFile = tempDir.resolve("test.csv");
        Path javaFile = tempDir.resolve("test.java");
        Path pyFile = tempDir.resolve("test.py");
        Path cFile = tempDir.resolve("test.c");

        // Create the files with some content
        Files.writeString(htmlFile, "<html><body>Test</body></html>");
        Files.writeString(cssFile, "body { color: red; }");
        Files.writeString(jsFile, "function test() { return true; }");
        Files.writeString(jsonFile, "{\"test\": true}");
        Files.writeString(xmlFile, "<root><test>true</test></root>");
        Files.writeString(mdFile, "# Test\nThis is a test.");
        Files.writeString(csvFile, "name,value\ntest,true");
        Files.writeString(javaFile, "public class Test { }");
        Files.writeString(pyFile, "def test(): return True");
        Files.writeString(cFile, "int main() { return 0; }");

        // Test each file and verify the MIME type
        testMimeTypeForFile(htmlFile, "text/html");
        testMimeTypeForFile(cssFile, "text/css");
        testMimeTypeForFile(jsFile, "application/javascript");
        testMimeTypeForFile(jsonFile, "application/json");
        testMimeTypeForFile(xmlFile, "application/xml");
        testMimeTypeForFile(mdFile, "text/markdown");
        testMimeTypeForFile(csvFile, "text/csv");
        testMimeTypeForFile(javaFile, "text/x-java-source");
        testMimeTypeForFile(pyFile, "text/x-python");
        testMimeTypeForFile(cFile, "text/x-c");
    }

    /**
     * Helper method to test MIME type detection for a file.
     * 
     * @param file The file to test
     * @param expectedMimeType The expected MIME type
     */
    private void testMimeTypeForFile(Path file, String expectedMimeType) throws IOException {
        // Create a request for the file
        String uri = "file://content/" + file.toString();
        McpSchema.ReadResourceRequest request = Mockito.mock(McpSchema.ReadResourceRequest.class);
        when(request.uri()).thenReturn(uri);

        // Handle the request
        McpSchema.ReadResourceResult result = resource.handleRequest(request);

        // Verify the MIME type
        List<McpSchema.ResourceContents> contents = result.contents();
        McpSchema.ResourceContents content = contents.get(0);
        assertEquals(expectedMimeType, content.mimeType(), 
                "MIME type for " + file.getFileName() + " should be " + expectedMimeType);
    }
}
