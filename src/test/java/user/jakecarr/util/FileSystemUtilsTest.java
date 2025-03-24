package user.jakecarr.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import user.jakecarr.model.FileMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FileSystemUtils.
 */
public class FileSystemUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testGetFileMetadata() throws IOException {
        // Create a test file
        Path testFile = tempDir.resolve("metadata-test.txt");
        String testContent = "Test content for metadata";
        Files.writeString(testFile, testContent);

        // Get metadata
        FileMetadata metadata = FileSystemUtils.getFileMetadata(testFile.toString());

        // Verify metadata
        assertNotNull(metadata, "Metadata should not be null");
        assertEquals(testFile.getFileName().toString(), metadata.getName(), "File name should match");
        assertEquals(testFile.toString(), metadata.getPath(), "File path should match");
        assertEquals(testContent.length(), metadata.getSize(), "File size should match");
        assertNotNull(metadata.getLastModified(), "Last modified time should not be null");
        assertNotNull(metadata.getCreationTime(), "Creation time should not be null");
        assertFalse(metadata.isDirectory(), "Should not be a directory");
        assertTrue(metadata.isRegularFile(), "Should be a regular file");
        assertFalse(metadata.isSymbolicLink(), "Should not be a symbolic link");
        assertTrue(metadata.isReadable(), "Should be readable");
        assertTrue(metadata.isWritable(), "Should be writable");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testGetFileMetadataForDirectory() throws IOException {
        // Create a test directory
        Path testDir = tempDir.resolve("metadata-test-dir");
        Files.createDirectory(testDir);

        // Get metadata
        FileMetadata metadata = FileSystemUtils.getFileMetadata(testDir.toString());

        // Verify metadata
        assertNotNull(metadata, "Metadata should not be null");
        assertEquals(testDir.getFileName().toString(), metadata.getName(), "Directory name should match");
        assertEquals(testDir.toString(), metadata.getPath(), "Directory path should match");
        assertTrue(metadata.isDirectory(), "Should be a directory");
        assertFalse(metadata.isRegularFile(), "Should not be a regular file");
        assertFalse(metadata.isSymbolicLink(), "Should not be a symbolic link");
        assertTrue(metadata.isReadable(), "Should be readable");
        assertTrue(metadata.isWritable(), "Should be writable");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testGetFileMetadataForNonExistentFile() {
        // Try to get metadata for a non-existent file
        String nonExistentPath = tempDir.resolve("non-existent-file.txt").toString();

        // Expect an exception
        Exception exception = assertThrows(IOException.class, () -> {
            FileSystemUtils.getFileMetadata(nonExistentPath);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("File does not exist"), 
                "Exception message should indicate that the file does not exist");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testListFiles() throws IOException {
        // Create test files and directories
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");
        Path subDir = tempDir.resolve("subdir");
        Path subFile = subDir.resolve("subfile.txt");

        Files.writeString(file1, "File 1 content");
        Files.writeString(file2, "File 2 content");
        Files.createDirectory(subDir);
        Files.writeString(subFile, "Subfile content");

        // List files non-recursively
        List<FileMetadata> nonRecursiveFiles = FileSystemUtils.listFiles(tempDir.toString(), false);

        // Verify non-recursive listing
        assertNotNull(nonRecursiveFiles, "File list should not be null");
        assertEquals(3, nonRecursiveFiles.size(), "Should list 3 items (2 files + 1 directory)");

        // Verify that the subfile is not included
        boolean foundSubfile = nonRecursiveFiles.stream()
                .anyMatch(metadata -> metadata.getPath().equals(subFile.toString()));
        assertFalse(foundSubfile, "Subfile should not be included in non-recursive listing");

        // List files recursively
        List<FileMetadata> recursiveFiles = FileSystemUtils.listFiles(tempDir.toString(), true);

        // Verify recursive listing
        assertNotNull(recursiveFiles, "File list should not be null");
        assertEquals(5, recursiveFiles.size(), "Should list 5 items (3 files + 1 directory + parent directory)");

        // Verify that the subfile is included
        foundSubfile = recursiveFiles.stream()
                .anyMatch(metadata -> metadata.getPath().equals(subFile.toString()));
        assertTrue(foundSubfile, "Subfile should be included in recursive listing");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testListFilesForNonExistentDirectory() {
        // Try to list files in a non-existent directory
        String nonExistentPath = tempDir.resolve("non-existent-dir").toString();

        // Expect an exception
        Exception exception = assertThrows(IOException.class, () -> {
            FileSystemUtils.listFiles(nonExistentPath, false);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Directory does not exist"), 
                "Exception message should indicate that the directory does not exist");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testListFilesForFile() throws IOException {
        // Create a test file
        Path testFile = tempDir.resolve("not-a-dir.txt");
        Files.writeString(testFile, "This is not a directory");

        // Try to list files in a file (not a directory)
        Exception exception = assertThrows(IOException.class, () -> {
            FileSystemUtils.listFiles(testFile.toString(), false);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Not a directory"), 
                "Exception message should indicate that the path is not a directory");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testReadTextFile() throws IOException {
        // Create a test file
        Path testFile = tempDir.resolve("text-file.txt");
        String testContent = "This is a test text file.";
        Files.writeString(testFile, testContent);

        // Read the file
        String content = FileSystemUtils.readTextFile(testFile.toString());

        // Verify the content
        assertEquals(testContent, content, "File content should match");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testReadTextFileForNonExistentFile() {
        // Try to read a non-existent file
        String nonExistentPath = tempDir.resolve("non-existent-file.txt").toString();

        // Expect an exception
        Exception exception = assertThrows(IOException.class, () -> {
            FileSystemUtils.readTextFile(nonExistentPath);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("File does not exist"), 
                "Exception message should indicate that the file does not exist");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testReadTextFileForDirectory() throws IOException {
        // Create a test directory
        Path testDir = tempDir.resolve("not-a-file");
        Files.createDirectory(testDir);

        // Try to read a directory as a text file
        Exception exception = assertThrows(IOException.class, () -> {
            FileSystemUtils.readTextFile(testDir.toString());
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Not a regular file"), 
                "Exception message should indicate that the path is not a regular file");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testReadBinaryFile() throws IOException {
        // Create a test binary file
        Path testFile = tempDir.resolve("binary-file.bin");
        byte[] testContent = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04 };
        Files.write(testFile, testContent);

        // Read the file
        String base64Content = FileSystemUtils.readBinaryFile(testFile.toString());

        // Decode and verify the content
        byte[] decodedContent = Base64.getDecoder().decode(base64Content);
        assertArrayEquals(testContent, decodedContent, "Binary content should match");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testIsTextFile() throws IOException {
        // Create a text file
        Path textFile = tempDir.resolve("text-file.txt");
        String textContent = "This is a text file with no null bytes.";
        Files.writeString(textFile, textContent);

        // Create a binary file with null bytes
        Path binaryFile = tempDir.resolve("binary-file.bin");
        byte[] binaryContent = new byte[] { 0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x00, 0x57, 0x6F, 0x72, 0x6C, 0x64 };
        Files.write(binaryFile, binaryContent);

        // Test text file detection
        assertTrue(FileSystemUtils.isTextFile(textFile.toString()), 
                "Text file should be detected as text");
        assertFalse(FileSystemUtils.isTextFile(binaryFile.toString()), 
                "Binary file should not be detected as text");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testExtractPathFromUri() {
        // Test URI extraction
        String metadataUri = "file://metadata/C:/path/to/file.txt";
        String contentUri = "file://content/C:/path/to/file.txt";
        String encodedUri = "file://metadata/C:/path/with%20spaces/and%2Fslashes.txt";

        String metadataPrefix = "file://metadata/";
        String contentPrefix = "file://content/";

        // Extract paths
        String metadataPath = FileSystemUtils.extractPathFromUri(metadataUri, metadataPrefix);
        String contentPath = FileSystemUtils.extractPathFromUri(contentUri, contentPrefix);
        String encodedPath = FileSystemUtils.extractPathFromUri(encodedUri, metadataPrefix);

        // Verify extracted paths
        assertEquals("C:/path/to/file.txt", metadataPath, "Extracted metadata path should match");
        assertEquals("C:/path/to/file.txt", contentPath, "Extracted content path should match");
        assertEquals("C:/path/with spaces/and/slashes.txt", encodedPath, "Decoded path should match");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    public void testExtractPathFromInvalidUri() {
        // Test invalid URI
        String invalidUri = "invalid://metadata/path";
        String prefix = "file://metadata/";

        // Expect an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FileSystemUtils.extractPathFromUri(invalidUri, prefix);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Invalid URI format"), 
                "Exception message should indicate an invalid URI format");
    }
}
