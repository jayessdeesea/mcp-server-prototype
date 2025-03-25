# Filesystem MCP Server

An MCP server that provides resources for accessing file metadata and content.

## Overview

The Filesystem MCP Server is a Model Context Protocol (MCP) server that provides resources for accessing file metadata and content. It allows clients to retrieve information about files and directories, as well as read the content of files.

## Features

- Get metadata for files and directories (name, path, size, timestamps, permissions, etc.)
- Read the content of text and binary files
- Automatic MIME type detection based on file extension
- Comprehensive logging
- Dependency injection using Spring Framework for better maintainability and testability

## Resources

The server provides the following resources:

- `file://metadata/{path}` - Get metadata for a file or directory
- `file://content/{path}` - Get content of a file

For more information, see the [resources documentation](docs/api/resources.md).

## Tools

The server provides the following tools:

- `list_files` - List files in a directory

For more information, see the [tools documentation](docs/api/tools.md).

## Building and Running

### Prerequisites

- Java 17 or higher
- Maven
- Spring Framework (automatically managed by Maven)

### Building

```bash
mvn clean package
```

This will create an executable JAR file in the `target` directory.

### Running

```bash
java -jar target/filesystem-mcp-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Documentation

- [Resources](docs/api/resources.md)
- [Tools](docs/api/tools.md)
- [Prompts](docs/api/prompts.md)
- [Spring Integration](docs/spring-integration.md)
- [Spring Best Practices](docs/spring-best-practices.md)
- [Dagger2 to Spring Migration](docs/dagger2-to-spring-migration.md)
- [Dagger2 Best Practices](docs/dagger2-best-practices.md) (for reference)

## Architecture

The server uses a layered architecture:

1. **Configuration Layer**: Spring configuration classes for dependency injection
2. **Service Layer**: Core services for file system operations
3. **Resource Layer**: MCP resources for exposing file system functionality
4. **Utility Layer**: Utility classes for common operations

## Dependency Injection

The server uses Spring Framework for dependency injection, which provides:

- Constructor injection for better testability
- Lifecycle management with `@PostConstruct` and `@PreDestroy`
- Configuration classes for explicit bean definitions
- Component scanning for automatic bean discovery

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
