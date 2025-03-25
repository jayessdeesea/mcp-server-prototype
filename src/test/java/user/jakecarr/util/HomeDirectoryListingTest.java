package user.jakecarr.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import user.jakecarr.model.FileMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class specifically for testing file listing in the home directory.
 * This test is designed to diagnose why listing files in the home directory
 * might not be working as expected.
 */
public class HomeDirectoryListingTest {
    
    private FileSystemUtils fileSystemUtils;
    private String homeDir;
    
    @BeforeEach
    public void setUp() {
        fileSystemUtils = new FileSystemUtils();
        homeDir = System.getProperty("user.home");
    }

    @Test
    @Timeout(10) // 10 seconds timeout
    public void testListFilesInHomeDirectory() throws IOException {
        System.out.println("Home directory path: " + homeDir);
        
        // Verify the home directory exists and is readable
        Path homePath = Paths.get(homeDir);
        assertTrue(Files.exists(homePath), "Home directory should exist");
        assertTrue(Files.isDirectory(homePath), "Home directory should be a directory");
        assertTrue(Files.isReadable(homePath), "Home directory should be readable");
        
        // List files in the home directory (non-recursive)
        List<FileMetadata> files = fileSystemUtils.listFiles(homeDir, false);
        
        // Print the results for debugging
        System.out.println("Found " + files.size() + " files/directories in home directory:");
        for (FileMetadata file : files) {
            System.out.println("- " + file.getName() + " (Path: " + file.getPath() + ")");
        }
        
        // Verify that files were found
        assertFalse(files.isEmpty(), "Home directory should not be empty");
    }
    
    @Test
    @Timeout(10) // 10 seconds timeout
    public void testListFilesWithSpecificPermissions() throws IOException {
        System.out.println("Home directory path: " + homeDir);
        
        // List files in the home directory (non-recursive)
        List<FileMetadata> files = fileSystemUtils.listFiles(homeDir, false);
        
        // Check permissions on each file
        System.out.println("File permissions in home directory:");
        for (FileMetadata file : files) {
            System.out.println(file.getName() + 
                " - Readable: " + file.isReadable() + 
                ", Writable: " + file.isWritable() + 
                ", Executable: " + file.isExecutable() + 
                ", Hidden: " + file.isHidden());
        }
    }
    
    @Test
    @Timeout(10) // 10 seconds timeout
    public void testDirectListingWithJavaAPI() throws IOException {
        System.out.println("Home directory path: " + homeDir);
        
        // Use Java's Files API directly to list files
        Path homePath = Paths.get(homeDir);
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
    public void testListFilesWithExceptionHandling() {
        System.out.println("Home directory path: " + homeDir);
        
        try {
            // List files in the home directory (non-recursive)
            List<FileMetadata> files = fileSystemUtils.listFiles(homeDir, false);
            
            // Print the results for debugging
            System.out.println("Found " + files.size() + " files/directories in home directory");
            assertFalse(files.isEmpty(), "Home directory should not be empty");
            
        } catch (Exception e) {
            // Print detailed exception information
            System.err.println("Exception when listing home directory: " + e.getMessage());
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }
}
