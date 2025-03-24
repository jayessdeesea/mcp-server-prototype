# Filesystem MCP Server

An MCP server that provides resources for accessing file metadata and content.

## Overview

The Filesystem MCP Server is a Model Context Protocol (MCP) server that provides resources for accessing file metadata and content. It allows clients to retrieve information about files and directories, as well as read the content of files.

## Features

- Get metadata for files and directories (name, path, size, timestamps, permissions, etc.)
- Read the content of text and binary files
- Automatic MIME type detection based on file extension
- Comprehensive logging

## Resources

The server provides the following resources:

- `file://metadata/{path}` - Get metadata for a file or directory
- `file://content/{path}` - Get content of a file

For more information, see the [resources documentation](docs/api/resources.md).

## Building and Running

### Prerequisites

- Java 17 or higher
- Maven

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

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
