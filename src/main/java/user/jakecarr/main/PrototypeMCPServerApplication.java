package user.jakecarr.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import user.jakecarr.config.AppConfig;
import user.jakecarr.config.FileSystemConfig;
import user.jakecarr.config.McpConfig;
import user.jakecarr.service.PrototypeMCPServerService;

/**
 * Main application class for the Prototype MCP Server.
 * This class uses Spring Boot to bootstrap the application and manage dependencies.
 */
@SpringBootApplication
@ComponentScan(basePackages = "user.jakecarr")
@Import({AppConfig.class, FileSystemConfig.class, McpConfig.class})
public class PrototypeMCPServerApplication implements ApplicationRunner {
    private static final Logger logger = LogManager.getLogger(PrototypeMCPServerApplication.class);
    
    private final PrototypeMCPServerService mcpServerService;
    
    /**
     * Constructor for Spring dependency injection.
     * 
     * @param mcpServerService The PrototypeMCPServerService dependency
     */
    public PrototypeMCPServerApplication(PrototypeMCPServerService mcpServerService) {
        this.mcpServerService = mcpServerService;
        logger.debug("PrototypeMCPServerApplication constructed");
    }
    
    /**
     * Main method to start the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Configure java.util.logging to use Log4j2
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
        
        SpringApplication app = new SpringApplication(PrototypeMCPServerApplication.class);
        // Disable banner and other unnecessary features
        app.setLogStartupInfo(false);
        app.run(args);
    }
    
    /**
     * Run method called by Spring Boot after the application context is loaded.
     * 
     * @param args Application arguments
     * @throws Exception If an error occurs
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Starting Prototype MCP Server");
        
        try {
            // Start the MCP server
            mcpServerService.start();
            
            // Create a non-daemon thread to keep the application running
            Thread keepAliveThread = new Thread(() -> {
                logger.info("MCP server is running. Press Ctrl+C to stop.");
                try {
                    // This will keep the thread alive indefinitely
                    Thread.currentThread().join();
                } catch (InterruptedException e) {
                    logger.info("Keep-alive thread interrupted");
                }
            });
            
            // Set as non-daemon so it keeps the application alive
            keepAliveThread.setDaemon(false);
            keepAliveThread.start();
        } catch (Exception e) {
            logger.error("Failed to start server", e);
            System.exit(1);
        }
    }
}
