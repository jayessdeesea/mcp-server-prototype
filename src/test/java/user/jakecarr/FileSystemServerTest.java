package user.jakecarr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for file system operations.
 */
public class FileSystemServerTest {

    @TempDir
    Path tempDir;
    
    @Test
    @Timeout(5) // 5 seconds timeout
    public void testFileOperations() throws IOException {
        // Create a test file
        Path testFile = tempDir.resolve("test.txt");
        String testContent = "Test content";
        Files.writeString(testFile, testContent);
        
        // Create a test directory
        Path testDir = tempDir.resolve("testdir");
        Files.createDirectory(testDir);
        
        // Verify the file and directory exist
        assertTrue(Files.exists(testFile), "Test file should exist");
        assertTrue(Files.isRegularFile(testFile), "Test file should be a regular file");
        assertTrue(Files.exists(testDir), "Test directory should exist");
        assertTrue(Files.isDirectory(testDir), "Test directory should be a directory");
        
        // Read the file content
        String readContent = Files.readString(testFile);
        assertEquals(testContent, readContent, "File content should match");
    }
    
    @Test
    @Timeout(5) // 5 seconds timeout
    public void testFileMetadataExtraction() throws IOException {
        // Create a test file
        Path testFile = tempDir.resolve("metadata-test.txt");
        String testContent = "Test content for metadata";
        Files.writeString(testFile, testContent);
        
        // Get basic file attributes
        assertTrue(Files.exists(testFile), "Test file should exist");
        assertTrue(Files.isReadable(testFile), "Test file should be readable");
        assertFalse(Files.isDirectory(testFile), "Test file should not be a directory");
        assertEquals(testContent.length(), Files.size(testFile), "File size should match content length");
        assertNotNull(Files.getLastModifiedTime(testFile), "Last modified time should not be null");
    }
}
