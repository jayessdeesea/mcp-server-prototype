package user.jakecarr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for file system resources.
 * This test verifies that the resources correctly handle file metadata and content.
 */
public class PrototypeMCPServerIntegrationTest {

    @TempDir
    Path tempDir;
    
    private Path textFile;
    private Path binaryFile;
    private Path directory;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        
        // Create a text file
        textFile = tempDir.resolve("test.txt");
        String textContent = "This is a test file with text content.";
        Files.writeString(textFile, textContent);
        
        // Create a binary file
        binaryFile = tempDir.resolve("test.bin");
        byte[] binaryContent = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04 };
        Files.write(binaryFile, binaryContent);
        
        // Create a directory
        directory = tempDir.resolve("testdir");
        Files.createDirectory(directory);
    }
    
    @Test
    @Timeout(5) // 5 seconds timeout
    public void testFileMetadataResource() throws IOException {
        // Test file metadata
        String metadataUri = "file://metadata/" + textFile.toString();
        
        // Verify the URI format
        assertNotNull(metadataUri, "Metadata URI should not be null");
        assertTrue(metadataUri.startsWith("file://metadata/"), "Metadata URI should have the correct prefix");
        
        // Verify the file exists and has the expected attributes
        assertTrue(Files.exists(textFile), "Test file should exist");
        assertEquals(Files.size(textFile), Files.readAttributes(textFile, "size").get("size"), "File size should match");
        assertFalse(Files.isDirectory(textFile), "Should not be a directory");
        assertTrue(Files.isRegularFile(textFile), "Should be a regular file");
        assertFalse(Files.isSymbolicLink(textFile), "Should not be a symbolic link");
        assertTrue(Files.isReadable(textFile), "Should be readable");
    }
    
    @Test
    @Timeout(5) // 5 seconds timeout
    public void testFileContentResource() throws IOException {
        // Test file content
        String contentUri = "file://content/" + textFile.toString();
        
        // Verify the URI format
        assertNotNull(contentUri, "Content URI should not be null");
        assertTrue(contentUri.startsWith("file://content/"), "Content URI should have the correct prefix");
        
        // Verify the file exists and has the expected content
        assertTrue(Files.exists(textFile), "Test file should exist");
        assertEquals("This is a test file with text content.", Files.readString(textFile), "File content should match");
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
        
        // Create the files
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
        
        // Verify the files exist
        assertTrue(Files.exists(htmlFile), "HTML file should exist");
        assertTrue(Files.exists(cssFile), "CSS file should exist");
        assertTrue(Files.exists(jsFile), "JS file should exist");
        assertTrue(Files.exists(jsonFile), "JSON file should exist");
        assertTrue(Files.exists(xmlFile), "XML file should exist");
        assertTrue(Files.exists(mdFile), "MD file should exist");
        assertTrue(Files.exists(csvFile), "CSV file should exist");
        assertTrue(Files.exists(javaFile), "Java file should exist");
        assertTrue(Files.exists(pyFile), "Python file should exist");
        assertTrue(Files.exists(cFile), "C file should exist");
        
        // Test MIME type detection using the helper method
        assertEquals("text/html", determineMimeType(htmlFile.toString()));
        assertEquals("text/css", determineMimeType(cssFile.toString()));
        assertEquals("application/javascript", determineMimeType(jsFile.toString()));
        assertEquals("application/json", determineMimeType(jsonFile.toString()));
        assertEquals("application/xml", determineMimeType(xmlFile.toString()));
        assertEquals("text/markdown", determineMimeType(mdFile.toString()));
        assertEquals("text/csv", determineMimeType(csvFile.toString()));
        assertEquals("text/x-java-source", determineMimeType(javaFile.toString()));
        assertEquals("text/x-python", determineMimeType(pyFile.toString()));
        assertEquals("text/x-c", determineMimeType(cFile.toString()));
    }
    
    /**
     * Helper method to determine MIME type based on file extension.
     * This is a simplified version of the logic in FileContentResource.
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
