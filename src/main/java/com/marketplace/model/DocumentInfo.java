package com.marketplace.model;

public class DocumentInfo {
    private String id;
    private String fileName;
    private String s3Key;
    private String contentType;
    private Long fileSize;

    public DocumentInfo() {}

    public DocumentInfo(String id, String fileName, String s3Key, String contentType, Long fileSize) {
        this.id = id;
        this.fileName = fileName;
        this.s3Key = s3Key;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getS3Key() { return s3Key; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
} 