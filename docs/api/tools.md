# Filesystem MCP Server Tools

The Filesystem MCP Server provides tools for interacting with the file system.

## Understanding MCP Tools

In the Model Context Protocol (MCP), tools represent executable functionality that can perform actions or computations. Tools can modify state or interact with external systems.

Tools in MCP have:
- A unique name
- A description
- An input schema (defining the parameters they accept)
- A response format

## Available Tools

### File Management Tools

| Tool Name | Description | Parameters |
|-----------|-------------|------------|
| `list_files` | List files in a directory | `path`: Directory path to list files from<br>`recursive`: (Optional) Whether to list files recursively |
| `get_file_metadata` | Get metadata for a file or directory | `path`: Path to the file or directory |
| `get_file_content` | Get content of a file | `path`: Path to the file |

### Example Usage

#### List Files

```json
{
  "name": "list_files",
  "arguments": {
    "path": "/path/to/directory",
    "recursive": true
  }
}
```

Response:

```json
[
  {
    "name": "file1.txt",
    "path": "/path/to/directory/file1.txt",
    "size": 1024,
    "lastModified": "2025-03-24T09:00:00Z",
    "creationTime": "2025-03-23T09:00:00Z",
    "directory": false,
    "regularFile": true,
    "symbolicLink": false,
    "readable": true,
    "writable": true,
    "executable": false
  },
  {
    "name": "subdirectory",
    "path": "/path/to/directory/subdirectory",
    "lastModified": "2025-03-24T09:00:00Z",
    "creationTime": "2025-03-23T09:00:00Z",
    "directory": true,
    "regularFile": false,
    "symbolicLink": false,
    "readable": true,
    "writable": true,
    "executable": true
  }
]
```

#### Get File Metadata

```json
{
  "name": "get_file_metadata",
  "arguments": {
    "path": "/path/to/file.txt"
  }
}
```

Response:

```json
{
  "name": "file.txt",
  "path": "/path/to/file.txt",
  "size": 1024,
  "lastModified": "2025-03-24T09:00:00Z",
  "creationTime": "2025-03-23T09:00:00Z",
  "directory": false,
  "regularFile": true,
  "symbolicLink": false,
  "readable": true,
  "writable": true,
  "executable": false
}
```

#### Get File Content

```json
{
  "name": "get_file_content",
  "arguments": {
    "path": "/path/to/file.txt"
  }
}
```

Response:

```
This is the content of the file.
```

## Implementation Details

The tools are implemented using Spring Framework and the MCP SDK:

1. Tools are registered in the `PrototypeMCPServerService` class.
2. Each tool has a defined input schema using `McpSchema.JsonSchema`.
3. Tool handlers are implemented as lambda functions that process the input and return a result.
4. Error handling is implemented to provide meaningful error messages.
5. Comprehensive logging is provided for debugging and monitoring.

## Future Tools

In future versions, the server may provide additional tools:

### File Management Tools

| Tool Name | Description | Parameters |
|-----------|-------------|------------|
| `create_file` | Create a new file | `path`: Path where the file should be created<br>`content`: Content to write to the file |
| `create_directory` | Create a new directory | `path`: Path where the directory should be created |
| `delete_file` | Delete a file | `path`: Path to the file to delete |
| `delete_directory` | Delete a directory | `path`: Path to the directory to delete<br>`recursive`: Whether to delete recursively |
| `copy_file` | Copy a file | `source`: Path to the source file<br>`destination`: Path to the destination |
| `move_file` | Move or rename a file | `source`: Path to the source file<br>`destination`: Path to the destination |

### File Search Tools

| Tool Name | Description | Parameters |
|-----------|-------------|------------|
| `search_files` | Search for files matching criteria | `directory`: Directory to search in<br>`pattern`: Glob pattern to match<br>`recursive`: Whether to search recursively |
| `find_text` | Find files containing text | `directory`: Directory to search in<br>`text`: Text to search for<br>`recursive`: Whether to search recursively |

### File Monitoring Tools

| Tool Name | Description | Parameters |
|-----------|-------------|------------|
| `watch_file` | Watch a file for changes | `path`: Path to the file to watch<br>`duration`: How long to watch (in seconds) |
| `watch_directory` | Watch a directory for changes | `path`: Path to the directory to watch<br>`duration`: How long to watch (in seconds) |
