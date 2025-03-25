package user.jakecarr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.jakecarr.FileSystemServer;
import user.jakecarr.resources.DirectoryListingResource;
import user.jakecarr.resources.FileContentResource;
import user.jakecarr.resources.FileMetadataResource;
import user.jakecarr.util.FileSystemUtils;

/**
 * Spring configuration class for file system related dependencies.
 */
@Configuration
public class FileSystemConfig {
    
    /**
     * Provides a FileSystemUtils instance.
     *
     * @return The FileSystemUtils instance
     */
    @Bean
    public FileSystemUtils fileSystemUtils() {
        return new FileSystemUtils();
    }
    
    /**
     * Provides a FileContentResource instance.
     *
     * @param fileSystemUtils The FileSystemUtils dependency
     * @return The FileContentResource instance
     */
    @Bean
    public FileContentResource fileContentResource(FileSystemUtils fileSystemUtils) {
        return new FileContentResource(fileSystemUtils);
    }
    
    /**
     * Provides a FileMetadataResource instance.
     *
     * @param fileSystemUtils The FileSystemUtils dependency
     * @param objectMapper The ObjectMapper dependency
     * @return The FileMetadataResource instance
     */
    @Bean
    public FileMetadataResource fileMetadataResource(FileSystemUtils fileSystemUtils, ObjectMapper objectMapper) {
        return new FileMetadataResource(fileSystemUtils, objectMapper);
    }
    
    /**
     * Provides a DirectoryListingResource instance.
     *
     * @param fileSystemUtils The FileSystemUtils dependency
     * @param objectMapper The ObjectMapper dependency
     * @return The DirectoryListingResource instance
     */
    @Bean
    public DirectoryListingResource directoryListingResource(FileSystemUtils fileSystemUtils, ObjectMapper objectMapper) {
        return new DirectoryListingResource(fileSystemUtils, objectMapper);
    }
    
    /**
     * Provides a FileSystemServer instance.
     *
     * @param contentResource The FileContentResource dependency
     * @param metadataResource The FileMetadataResource dependency
     * @param directoryListingResource The DirectoryListingResource dependency
     * @return The FileSystemServer instance
     */
    @Bean
    public FileSystemServer fileSystemServer(
            FileContentResource contentResource,
            FileMetadataResource metadataResource,
            DirectoryListingResource directoryListingResource) {
        return new FileSystemServer(contentResource, metadataResource, directoryListingResource);
    }
}
