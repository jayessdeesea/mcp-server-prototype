package user.jakecarr;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple test client to interact with the MCP server.
 */
public class McpClientTest {

    public static void main(String[] args) {
        try {
            // Create client info
            McpSchema.Implementation clientInfo = new McpSchema.Implementation("test-client", "1.0.0");
            
            // Create server parameters
            ServerParameters serverParams = ServerParameters.builder("java")
                .args("-jar", "C:/Users/jayes/Cline/MCP/prototype-mcp/prototype-mcp.jar")
                .build();
            
            // Create transport with server parameters
            StdioClientTransport transport = new StdioClientTransport(serverParams);
            
            // Create the client using the builder pattern
            McpSyncClient client = McpClient.sync(transport)
                .clientInfo(clientInfo)
                .build();
            
            try {
                // Initialize the client (connects to the server)
                client.initialize();
                
                // Call the list_files tool
                Map<String, Object> toolArgs = new HashMap<>();
                toolArgs.put("path", "C:/Users/jayes");
                toolArgs.put("recursive", false);
                
                McpSchema.CallToolRequest toolRequest = new McpSchema.CallToolRequest("list_files", toolArgs);
                McpSchema.CallToolResult toolResponse = client.callTool(toolRequest);
                
                // Access the tool response content
                if (toolResponse.content() != null && !toolResponse.content().isEmpty()) {
                    McpSchema.Content content = toolResponse.content().get(0);
                    if (content instanceof McpSchema.TextContent textContent) {
                        System.out.println("Files in home directory:");
                        System.out.println(textContent.text());
                    }
                }
            } finally {
                // Close the client
                client.close();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
