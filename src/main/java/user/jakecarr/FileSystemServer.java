package user.jakecarr;

import user.jakecarr.resources.DirectoryListingResource;
import user.jakecarr.resources.FileContentResource;
import user.jakecarr.resources.FileMetadataResource;
import user.jakecarr.util.FileSystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Server for file system operations.
 * This class provides a centralized interface for file system operations
 * used by the MCP server.
 */
public class FileSystemServer {
    private static final Logger logger = LogManager.getLogger(FileSystemServer.class);
    
    private final FileContentResource contentResource;
    private final FileMetadataResource metadataResource;
    private final DirectoryListingResource directoryListingResource;
    
    /**
     * Constructs a new FileSystemServer with the necessary resources.
     * 
     * @param contentResource The FileContentResource dependency
     * @param metadataResource The FileMetadataResource dependency
     * @param directoryListingResource The DirectoryListingResource dependency
     */
    public FileSystemServer(FileContentResource contentResource, 
                           FileMetadataResource metadataResource,
                           DirectoryListingResource directoryListingResource) {
        this.contentResource = contentResource;
        this.metadataResource = metadataResource;
        this.directoryListingResource = directoryListingResource;
        logger.debug("FileSystemServer constructed");
    }
    
    /**
     * Initialization method called by Spring after dependency injection.
     */
    @PostConstruct
    public void initialize() {
        logger.info("Initializing FileSystemServer");
    }
    
    /**
     * Cleanup method called by Spring before bean destruction.
     */
    @PreDestroy
    public void cleanup() {
        logger.info("Cleaning up FileSystemServer");
    }
    
    /**
     * Gets the file content resource.
     * 
     * @return the file content resource
     */
    public FileContentResource getContentResource() {
        return contentResource;
    }
    
    /**
     * Gets the file metadata resource.
     * 
     * @return the file metadata resource
     */
    public FileMetadataResource getMetadataResource() {
        return metadataResource;
    }
    
    /**
     * Gets the directory listing resource.
     * 
     * @return the directory listing resource
     */
    public DirectoryListingResource getDirectoryListingResource() {
        return directoryListingResource;
    }
    
    /**
     * This method is deprecated and will be removed in a future version.
     * FileSystemUtils is now injected directly where needed.
     * 
     * @return null
     * @deprecated Use dependency injection to get FileSystemUtils
     */
    @Deprecated
    public FileSystemUtils getFileSystemUtils() {
        return null;
    }
    
    /**
     * Checks if a file exists.
     * 
     * @param path the path to check
     * @return true if the file exists, false otherwise
     */
    public boolean fileExists(String path) {
        Path filePath = Paths.get(path);
        return Files.exists(filePath);
    }
    
    /**
     * Checks if a path is a directory.
     * 
     * @param path the path to check
     * @return true if the path is a directory, false otherwise
     */
    public boolean isDirectory(String path) {
        Path dirPath = Paths.get(path);
        return Files.isDirectory(dirPath);
    }
    
    /**
     * Creates a directory if it doesn't exist.
     * 
     * @param path the directory path to create
     * @return true if the directory was created or already exists, false otherwise
     */
    public boolean createDirectoryIfNotExists(String path) {
        try {
            Path dirPath = Paths.get(path);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                logger.info("Created directory: {}", path);
            }
            return true;
        } catch (IOException e) {
            logger.error("Failed to create directory: {}", path, e);
            return false;
        }
    }
}
