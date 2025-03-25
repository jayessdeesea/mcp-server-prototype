# Filesystem MCP Server Resources (Deprecated)

> **Note:** The Filesystem MCP Server now uses tools instead of resources for accessing file metadata and content. Please refer to the [tools documentation](tools.md) for the current implementation.

This document describes the previous resource-based implementation, which is no longer actively used.

## Resource Types

### 1. File Metadata Resource (Deprecated)

The file metadata resource provides information about files and directories in the local filesystem.

#### URI Pattern

```
file://metadata/{path}
```

Where `{path}` is the path to the file or directory.

#### Example URIs

```
file://metadata/C:/Users/example/Documents/file.txt
file://metadata/home/user/Documents/file.txt
```

#### Response Format

The response is a JSON object with the following properties:

```json
{
  "name": "file.txt",
  "path": "C:/Users/example/Documents/file.txt",
  "size": 1024,
  "lastModified": "2025-03-23T10:30:00Z",
  "creationTime": "2025-03-20T15:45:00Z",
  "isDirectory": false,
  "isRegularFile": true,
  "isSymbolicLink": false,
  "isHidden": false,
  "isReadable": true,
  "isWritable": true,
  "isExecutable": false
}
```

### 2. File Content Resource (Deprecated)

The file content resource provides the content of files in the local filesystem.

#### URI Pattern

```
file://content/{path}
```

Where `{path}` is the path to the file.

#### Example URIs

```
file://content/C:/Users/example/Documents/file.txt
file://content/home/user/Documents/file.txt
```

#### Response Format

The response contains the content of the file with an appropriate MIME type.

For text files, the content is returned as a string with the appropriate text MIME type (e.g., `text/plain`, `text/html`, etc.).

For binary files, the content is returned as a base64-encoded string with the MIME type `application/octet-stream;base64`.

## Migration to Tools

The functionality previously provided by resources has been migrated to tools:

| Resource | Replacement Tool |
|----------|------------------|
| `file://metadata/{path}` | `get_file_metadata` |
| `file://content/{path}` | `get_file_content` |
| `file://directory/{path}` | `list_files` |

Please refer to the [tools documentation](tools.md) for details on how to use these tools.
