package user.jakecarr.util;

import user.jakecarr.model.FileMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for file system operations.
 */
public class FileSystemUtils {
    private static final Logger logger = LogManager.getLogger(FileSystemUtils.class);
    
    /**
     * Private constructor to prevent instantiation.
     */
    private FileSystemUtils() {
        // Utility class, do not instantiate
    }
    
    /**
     * Get metadata for a file.
     * 
     * @param filePath The path to the file
     * @return The file metadata
     * @throws IOException If an I/O error occurs
     */
    public static FileMetadata getFileMetadata(String filePath) throws IOException {
        logger.debug("Getting metadata for file: {}", filePath);
        
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            logger.warn("File does not exist: {}", filePath);
            throw new IOException("File does not exist: " + filePath);
        }
        
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        FileMetadata metadata = new FileMetadata();
        
        metadata.setName(path.getFileName().toString());
        metadata.setPath(path.toString());
        metadata.setSize(attrs.size());
        metadata.setLastModified(attrs.lastModifiedTime());
        metadata.setCreationTime(attrs.creationTime());
        metadata.setDirectory(Files.isDirectory(path));
        metadata.setRegularFile(Files.isRegularFile(path));
        metadata.setSymbolicLink(Files.isSymbolicLink(path));
        
        try {
            metadata.setHidden(Files.isHidden(path));
        } catch (IOException e) {
            logger.warn("Failed to determine if file is hidden: {}", filePath, e);
            metadata.setHidden(false);
        }
        
        metadata.setReadable(Files.isReadable(path));
        metadata.setWritable(Files.isWritable(path));
        metadata.setExecutable(Files.isExecutable(path));
        
        logger.debug("Metadata retrieved successfully for file: {}", filePath);
        return metadata;
    }
    
    /**
     * List files in a directory.
     * 
     * @param directoryPath The path to the directory
     * @param recursive Whether to list files recursively
     * @return A list of file metadata
     * @throws IOException If an I/O error occurs
     */
    public static List<FileMetadata> listFiles(String directoryPath, boolean recursive) throws IOException {
        logger.debug("Listing files in directory: {}, recursive: {}", directoryPath, recursive);
        
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            logger.warn("Directory does not exist: {}", directoryPath);
            throw new IOException("Directory does not exist: " + directoryPath);
        }
        
        if (!Files.isDirectory(path)) {
            logger.warn("Not a directory: {}", directoryPath);
            throw new IOException("Not a directory: " + directoryPath);
        }
        
        if (!Files.isReadable(path)) {
            logger.warn("Directory is not readable: {}", directoryPath);
            throw new IOException("Directory is not readable: " + directoryPath);
        }
        
        List<FileMetadata> files = new ArrayList<>();
        
        try (Stream<Path> stream = recursive ? Files.walk(path) : Files.list(path)) {
            files = stream
                .map(p -> {
                    try {
                        return getFileMetadata(p.toString());
                    } catch (IOException e) {
                        logger.warn("Failed to get metadata for file: {}", p, e);
                        return null;
                    }
                })
                .filter(metadata -> metadata != null)
                .collect(Collectors.toList());
        }
        
        logger.debug("Listed {} files in directory: {}", files.size(), directoryPath);
        return files;
    }
    
    /**
     * Read the content of a text file.
     * 
     * @param filePath The path to the file
     * @return The file content as a string
     * @throws IOException If an I/O error occurs
     */
    public static String readTextFile(String filePath) throws IOException {
        logger.debug("Reading text file: {}", filePath);
        
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            logger.warn("File does not exist: {}", filePath);
            throw new IOException("File does not exist: " + filePath);
        }
        
        if (!Files.isRegularFile(path)) {
            logger.warn("Not a regular file: {}", filePath);
            throw new IOException("Not a regular file: " + filePath);
        }
        
        if (!Files.isReadable(path)) {
            logger.warn("File is not readable: {}", filePath);
            throw new IOException("File is not readable: " + filePath);
        }
        
        String content = Files.readString(path, StandardCharsets.UTF_8);
        logger.debug("File read successfully: {}", filePath);
        return content;
    }
    
    /**
     * Read the content of a binary file and encode it as base64.
     * 
     * @param filePath The path to the file
     * @return The file content as a base64-encoded string
     * @throws IOException If an I/O error occurs
     */
    public static String readBinaryFile(String filePath) throws IOException {
        logger.debug("Reading binary file: {}", filePath);
        
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            logger.warn("File does not exist: {}", filePath);
            throw new IOException("File does not exist: " + filePath);
        }
        
        if (!Files.isRegularFile(path)) {
            logger.warn("Not a regular file: {}", filePath);
            throw new IOException("Not a regular file: " + filePath);
        }
        
        if (!Files.isReadable(path)) {
            logger.warn("File is not readable: {}", filePath);
            throw new IOException("File is not readable: " + filePath);
        }
        
        byte[] bytes = Files.readAllBytes(path);
        String base64 = Base64.getEncoder().encodeToString(bytes);
        logger.debug("File read and encoded successfully: {}", filePath);
        return base64;
    }
    
    /**
     * Determine if a file is a text file based on its content.
     * This is a simple heuristic and may not be accurate for all files.
     * 
     * @param filePath The path to the file
     * @return True if the file is likely a text file, false otherwise
     * @throws IOException If an I/O error occurs
     */
    public static boolean isTextFile(String filePath) throws IOException {
        logger.debug("Checking if file is a text file: {}", filePath);
        
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            logger.warn("File does not exist: {}", filePath);
            throw new IOException("File does not exist: " + filePath);
        }
        
        if (!Files.isRegularFile(path)) {
            logger.warn("Not a regular file: {}", filePath);
            throw new IOException("Not a regular file: " + filePath);
        }
        
        if (!Files.isReadable(path)) {
            logger.warn("File is not readable: {}", filePath);
            throw new IOException("File is not readable: " + filePath);
        }
        
        // Read the first 8KB of the file to determine if it's text
        byte[] bytes = new byte[8192];
        int bytesRead = Files.newInputStream(path).read(bytes);
        
        if (bytesRead <= 0) {
            // Empty file, consider it text
            return true;
        }
        
        // Check for null bytes, which are uncommon in text files
        for (int i = 0; i < bytesRead; i++) {
            if (bytes[i] == 0) {
                logger.debug("File contains null bytes, likely binary: {}", filePath);
                return false;
            }
        }
        
        logger.debug("File appears to be a text file: {}", filePath);
        return true;
    }
    
    /**
     * Extract the file path from a URI.
     * 
     * @param uri The URI
     * @param prefix The URI prefix to remove
     * @return The file path
     */
    public static String extractPathFromUri(String uri, String prefix) {
        if (uri == null || !uri.startsWith(prefix)) {
            throw new IllegalArgumentException("Invalid URI format: " + uri);
        }
        
        String path = uri.substring(prefix.length());
        
        // Handle URL-encoded characters
        path = path.replace("%20", " ")
                  .replace("%2F", "/")
                  .replace("%5C", "\\")
                  .replace("%3A", ":");
        
        logger.debug("Extracted path from URI: {} -> {}", uri, path);
        return path;
    }
}
