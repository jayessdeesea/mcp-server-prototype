# Filesystem MCP Server Resources

The Filesystem MCP Server provides resources for accessing file metadata and content from the local filesystem.

## Resource Types

### 1. File Metadata Resource

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

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `name` | String | The name of the file or directory |
| `path` | String | The full path to the file or directory |
| `size` | Long | The size of the file in bytes (0 for directories) |
| `lastModified` | ISO-8601 String | The last modified time of the file or directory |
| `creationTime` | ISO-8601 String | The creation time of the file or directory |
| `isDirectory` | Boolean | Whether the path points to a directory |
| `isRegularFile` | Boolean | Whether the path points to a regular file |
| `isSymbolicLink` | Boolean | Whether the path points to a symbolic link |
| `isHidden` | Boolean | Whether the file or directory is hidden |
| `isReadable` | Boolean | Whether the file or directory is readable |
| `isWritable` | Boolean | Whether the file or directory is writable |
| `isExecutable` | Boolean | Whether the file or directory is executable |

#### Error Handling

The resource will return an error in the following cases:

- The file or directory does not exist
- The path is invalid
- The file or directory cannot be accessed due to permissions
- An I/O error occurs while reading the file metadata

### 2. File Content Resource

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

#### MIME Type Detection

The server attempts to detect the MIME type of the file based on its extension:

| Extension | MIME Type |
|-----------|-----------|
| .txt | text/plain |
| .html, .htm | text/html |
| .css | text/css |
| .js | application/javascript |
| .json | application/json |
| .xml | application/xml |
| .md | text/markdown |
| .csv | text/csv |
| .java | text/x-java-source |
| .py | text/x-python |
| .c, .cpp, .h | text/x-c |
| Other text files | text/plain |
| Binary files | application/octet-stream;base64 |

#### Text vs. Binary Detection

The server uses a simple heuristic to determine if a file is text or binary:

1. It reads the first 8KB of the file
2. If the file contains null bytes (0x00), it is considered binary
3. Otherwise, it is considered text

This heuristic works for most common file types but may not be accurate for all files.

#### Error Handling

The resource will return an error in the following cases:

- The file does not exist
- The path is invalid
- The file cannot be accessed due to permissions
- The path points to a directory (cannot read content of directories)
- An I/O error occurs while reading the file content

## Usage Examples

### Retrieving File Metadata

```java
ReadResourceRequest request = ReadResourceRequest.builder()
    .uri("file://metadata/C:/example/file.txt")
    .build();

ReadResourceResult result = client.readResource(request);
String metadataJson = result.getContent();
```

### Retrieving File Content

```java
ReadResourceRequest request = ReadResourceRequest.builder()
    .uri("file://content/C:/example/file.txt")
    .build();

ReadResourceResult result = client.readResource(request);
String content = result.getContent();
String mimeType = result.getMimeType();
```

## Implementation Details

The resources are implemented using the MCP SDK's resource specification pattern. The server registers resource handlers for the URI patterns and delegates the handling of requests to the appropriate resource handler.

The implementation uses Java's NIO.2 API for file operations, which provides a rich set of functionality for working with files and directories.
