package user.jakecarr.model;

import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Model class representing file metadata.
 */
public class FileMetadata {
    private String name;
    private String path;
    private long size;
    private Instant lastModified;
    private Instant creationTime;
    private boolean isDirectory;
    private boolean isRegularFile;
    private boolean isSymbolicLink;
    private boolean isHidden;
    private boolean isReadable;
    private boolean isWritable;
    private boolean isExecutable;

    /**
     * Default constructor.
     */
    public FileMetadata() {
    }

    /**
     * Get the file name.
     * 
     * @return The file name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the file name.
     * 
     * @param name The file name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the file path.
     * 
     * @return The file path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the file path.
     * 
     * @param path The file path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get the file size in bytes.
     * 
     * @return The file size
     */
    public long getSize() {
        return size;
    }

    /**
     * Set the file size.
     * 
     * @param size The file size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Get the last modified time.
     * 
     * @return The last modified time
     */
    public Instant getLastModified() {
        return lastModified;
    }

    /**
     * Set the last modified time.
     * 
     * @param lastModified The last modified time
     */
    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Set the last modified time from a FileTime object.
     * 
     * @param lastModified The last modified time as FileTime
     */
    public void setLastModified(FileTime lastModified) {
        if (lastModified != null) {
            this.lastModified = lastModified.toInstant();
        }
    }

    /**
     * Get the creation time.
     * 
     * @return The creation time
     */
    public Instant getCreationTime() {
        return creationTime;
    }

    /**
     * Set the creation time.
     * 
     * @param creationTime The creation time
     */
    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * Set the creation time from a FileTime object.
     * 
     * @param creationTime The creation time as FileTime
     */
    public void setCreationTime(FileTime creationTime) {
        if (creationTime != null) {
            this.creationTime = creationTime.toInstant();
        }
    }

    /**
     * Check if the file is a directory.
     * 
     * @return True if the file is a directory, false otherwise
     */
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * Set whether the file is a directory.
     * 
     * @param isDirectory True if the file is a directory, false otherwise
     */
    public void setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    /**
     * Check if the file is a regular file.
     * 
     * @return True if the file is a regular file, false otherwise
     */
    public boolean isRegularFile() {
        return isRegularFile;
    }

    /**
     * Set whether the file is a regular file.
     * 
     * @param isRegularFile True if the file is a regular file, false otherwise
     */
    public void setRegularFile(boolean isRegularFile) {
        this.isRegularFile = isRegularFile;
    }

    /**
     * Check if the file is a symbolic link.
     * 
     * @return True if the file is a symbolic link, false otherwise
     */
    public boolean isSymbolicLink() {
        return isSymbolicLink;
    }

    /**
     * Set whether the file is a symbolic link.
     * 
     * @param isSymbolicLink True if the file is a symbolic link, false otherwise
     */
    public void setSymbolicLink(boolean isSymbolicLink) {
        this.isSymbolicLink = isSymbolicLink;
    }

    /**
     * Check if the file is hidden.
     * 
     * @return True if the file is hidden, false otherwise
     */
    public boolean isHidden() {
        return isHidden;
    }

    /**
     * Set whether the file is hidden.
     * 
     * @param isHidden True if the file is hidden, false otherwise
     */
    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    /**
     * Check if the file is readable.
     * 
     * @return True if the file is readable, false otherwise
     */
    public boolean isReadable() {
        return isReadable;
    }

    /**
     * Set whether the file is readable.
     * 
     * @param isReadable True if the file is readable, false otherwise
     */
    public void setReadable(boolean isReadable) {
        this.isReadable = isReadable;
    }

    /**
     * Check if the file is writable.
     * 
     * @return True if the file is writable, false otherwise
     */
    public boolean isWritable() {
        return isWritable;
    }

    /**
     * Set whether the file is writable.
     * 
     * @param isWritable True if the file is writable, false otherwise
     */
    public void setWritable(boolean isWritable) {
        this.isWritable = isWritable;
    }

    /**
     * Check if the file is executable.
     * 
     * @return True if the file is executable, false otherwise
     */
    public boolean isExecutable() {
        return isExecutable;
    }

    /**
     * Set whether the file is executable.
     * 
     * @param isExecutable True if the file is executable, false otherwise
     */
    public void setExecutable(boolean isExecutable) {
        this.isExecutable = isExecutable;
    }

    /**
     * Format the last modified time as an ISO-8601 string.
     * 
     * @return The formatted last modified time
     */
    public String getFormattedLastModified() {
        if (lastModified == null) {
            return null;
        }
        return DateTimeFormatter.ISO_INSTANT.format(lastModified);
    }

    /**
     * Format the creation time as an ISO-8601 string.
     * 
     * @return The formatted creation time
     */
    public String getFormattedCreationTime() {
        if (creationTime == null) {
            return null;
        }
        return DateTimeFormatter.ISO_INSTANT.format(creationTime);
    }
}
