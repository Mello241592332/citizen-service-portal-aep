package com.citizenportal.model.report;

import com.citizenportal.model.user.User;
import java.time.LocalDateTime;
import java.util.List;

public class Report {

    private String id;
    private String description;
    private ReportStatus status;
    private User user;
    private String protocol;
    private ReportType type;
    private String location;
    private boolean isAnonymous;
    private LocalDateTime createdAt;

    private Authority authority;              // N:1
    private List<Attachment> attachments;     // 1:N
    private List<Movement> movements;         // 1:N

    public Report(String id, String description, ReportStatus status, User user, String protocol,
                  ReportType type, String location, boolean isAnonymous, LocalDateTime createdAt,
                  Authority authority, List<Attachment> attachments, List<Movement> movements) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.user = user;
        this.protocol = protocol;
        this.type = type;
        this.location = location;
        this.isAnonymous = isAnonymous;
        this.createdAt = createdAt;
        this.authority = authority;
        this.attachments = attachments;
        this.movements = movements;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public ReportType getType() { return type; }
    public void setType(ReportType type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Authority getAuthority() { return authority; }
    public void setAuthority(Authority authority) { this.authority = authority; }

    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }

    public List<Movement> getMovements() { return movements; }
    public void setMovements(List<Movement> movements) { this.movements = movements; }
}