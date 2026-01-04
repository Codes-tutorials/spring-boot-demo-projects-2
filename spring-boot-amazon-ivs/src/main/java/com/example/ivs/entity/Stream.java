package com.example.ivs.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "streams")
public class Stream {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String channelArn;
    
    @Column(nullable = false)
    private String streamId;
    
    @Column
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private StreamState state = StreamState.OFFLINE;
    
    @Column
    private String health = "UNKNOWN";
    
    @Column
    private Long viewerCount = 0L;
    
    @Column
    private LocalDateTime startTime;
    
    @Column
    private LocalDateTime endTime;
    
    @Column
    private Long duration; // in seconds
    
    @Column
    private String thumbnailUrl;
    
    @Column
    private String recordingUrl;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;

    public Stream() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Stream(String channelArn, String streamId) {
        this();
        this.channelArn = channelArn;
        this.streamId = streamId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChannelArn() { return channelArn; }
    public void setChannelArn(String channelArn) { this.channelArn = channelArn; }

    public String getStreamId() { return streamId; }
    public void setStreamId(String streamId) { this.streamId = streamId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public StreamState getState() { return state; }
    public void setState(StreamState state) { this.state = state; }

    public String getHealth() { return health; }
    public void setHealth(String health) { this.health = health; }

    public Long getViewerCount() { return viewerCount; }
    public void setViewerCount(Long viewerCount) { this.viewerCount = viewerCount; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getRecordingUrl() { return recordingUrl; }
    public void setRecordingUrl(String recordingUrl) { this.recordingUrl = recordingUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum StreamState {
        LIVE, OFFLINE
    }
}