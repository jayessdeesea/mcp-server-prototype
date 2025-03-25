package user.jakecarr.config;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Configuration class for MCP-related beans.
 */
@Configuration
public class McpConfig {
    private static final Logger logger = LogManager.getLogger(McpConfig.class);

    /**
     * Creates a StdioServerTransportProvider bean.
     * 
     * @return The StdioServerTransportProvider
     */
    @Bean
    public StdioServerTransportProvider stdioServerTransportProvider() {
        logger.debug("Creating StdioServerTransportProvider");
        return new StdioServerTransportProvider();
    }

    /**
     * Creates a McpSchema.Implementation bean for server info.
     * 
     * @return The server info
     */
    @Bean
    public McpSchema.Implementation serverInfo() {
        logger.debug("Creating server info");
        return new McpSchema.Implementation("filesystem-mcp-server", "1.0.0");
    }

    /**
     * Creates a McpSyncServer bean.
     * This bean is prototype-scoped because it should be created when needed and closed explicitly.
     * 
     * @param transportProvider The transport provider
     * @param serverInfo The server info
     * @return The McpSyncServer
     */
    @Bean
    @Scope("prototype")
    public McpSyncServer mcpSyncServer(StdioServerTransportProvider transportProvider, 
                                      McpSchema.Implementation serverInfo) {
        logger.debug("Creating McpSyncServer");
        return McpServer.sync(transportProvider)
            .serverInfo(serverInfo)
            .build();
    }
}
