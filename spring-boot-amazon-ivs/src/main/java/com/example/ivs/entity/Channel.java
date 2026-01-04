package com.example.ivs.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "channels")
public class Channel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String channelArn;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String streamKey;
    
    @Column(nullable = false)
    private String playbackUrl;
    
    @Column(nullable = false)
    private String ingestEndpoint;
    
    @Enumerated(EnumType.STRING)
    private ChannelType type = ChannelType.STANDARD;
    
    @Enumerated(EnumType.STRING)
    private LatencyMode latencyMode = LatencyMode.LOW;
    
    @Enumerated(EnumType.STRING)
    private ChannelStatus status = ChannelStatus.OFFLINE;
    
    @Column
    private Boolean authorized = false;
    
    @Column
    private Boolean recordingConfigurationArn;
    
    @Column
    private String tags;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime lastStreamStartTime;
    
    @Column
    private LocalDateTime lastStreamEndTime;
    
    @Column
    private Long totalViewers = 0L;
    
    @Column
    private Long currentViewers = 0L;

    public Channel() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Channel(String channelArn, String name, String streamKey, String playbackUrl, String ingestEndpoint) {
        this();
        this.channelArn = channelArn;
        this.name = name;
        this.streamKey = streamKey;
        this.playbackUrl = playbackUrl;
        this.ingestEndpoint = ingestEndpoint;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChannelArn() { return channelArn; }
    public void setChannelArn(String channelArn) { this.channelArn = channelArn; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStreamKey() { return streamKey; }
    public void setStreamKey(String streamKey) { this.streamKey = streamKey; }

    public String getPlaybackUrl() { return playbackUrl; }
    public void setPlaybackUrl(String playbackUrl) { this.playbackUrl = playbackUrl; }

    public String getIngestEndpoint() { return ingestEndpoint; }
    public void setIngestEndpoint(String ingestEndpoint) { this.ingestEndpoint = ingestEndpoint; }

    public ChannelType getType() { return type; }
    public void setType(ChannelType type) { this.type = type; }

    public LatencyMode getLatencyMode() { return latencyMode; }
    public void setLatencyMode(LatencyMode latencyMode) { this.latencyMode = latencyMode; }

    public ChannelStatus getStatus() { return status; }
    public void setStatus(ChannelStatus status) { this.status = status; }

    public Boolean getAuthorized() { return authorized; }
    public void setAuthorized(Boolean authorized) { this.authorized = authorized; }

    public Boolean getRecordingConfigurationArn() { return recordingConfigurationArn; }
    public void setRecordingConfigurationArn(Boolean recordingConfigurationArn) { this.recordingConfigurationArn = recordingConfigurationArn; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastStreamStartTime() { return lastStreamStartTime; }
    public void setLastStreamStartTime(LocalDateTime lastStreamStartTime) { this.lastStreamStartTime = lastStreamStartTime; }

    public LocalDateTime getLastStreamEndTime() { return lastStreamEndTime; }
    public void setLastStreamEndTime(LocalDateTime lastStreamEndTime) { this.lastStreamEndTime = lastStreamEndTime; }

    public Long getTotalViewers() { return totalViewers; }
    public void setTotalViewers(Long totalViewers) { this.totalViewers = totalViewers; }

    public Long getCurrentViewers() { return currentViewers; }
    public void setCurrentViewers(Long currentViewers) { this.currentViewers = currentViewers; }

    public enum ChannelType {
        BASIC, STANDARD
    }

    public enum LatencyMode {
        NORMAL, LOW
    }

    public enum ChannelStatus {
        CREATING, LIVE, OFFLINE, ERROR
    }
}