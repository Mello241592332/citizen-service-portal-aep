package com.citizenportal.model.report;

public class Attachment {
    private String id;
    private String fileName;
    private String fileType;

    public Attachment(String id, String fileName, String fileType) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
}