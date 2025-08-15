package com.marketplace.dto;

public class DocumentDisplayDto {
    private String id;
    private String fileName;
    private String url;
    private String contentType;
    private Long fileSize;

    public DocumentDisplayDto() {}

    public DocumentDisplayDto(String id, String fileName, String url, String contentType, Long fileSize) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
} 