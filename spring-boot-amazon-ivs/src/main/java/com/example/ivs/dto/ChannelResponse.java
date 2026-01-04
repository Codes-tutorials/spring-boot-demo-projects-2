package com.example.ivs.dto;

import com.example.ivs.entity.Channel;
import java.time.LocalDateTime;

public class ChannelResponse {
    
    private Long id;
    private String channelArn;
    private String name;
    private String description;
    private String streamKey;
    private String playbackUrl;
    private String ingestEndpoint;
    private Channel.ChannelType type;
    private Channel.LatencyMode latencyMode;
    private Channel.ChannelStatus status;
    private Boolean authorized;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastStreamStartTime;
    private LocalDateTime lastStreamEndTime;
    private Long totalViewers;
    private Long currentViewers;

    public ChannelResponse() {}

    public ChannelResponse(Channel channel) {
        this.id = channel.getId();
        this.channelArn = channel.getChannelArn();
        this.name = channel.getName();
        this.description = channel.getDescription();
        this.streamKey = channel.getStreamKey();
        this.playbackUrl = channel.getPlaybackUrl();
        this.ingestEndpoint = channel.getIngestEndpoint();
        this.type = channel.getType();
        this.latencyMode = channel.getLatencyMode();
        this.status = channel.getStatus();
        this.authorized = channel.getAuthorized();
        this.tags = channel.getTags();
        this.createdAt = channel.getCreatedAt();
        this.updatedAt = channel.getUpdatedAt();
        this.lastStreamStartTime = channel.getLastStreamStartTime();
        this.lastStreamEndTime = channel.getLastStreamEndTime();
        this.totalViewers = channel.getTotalViewers();
        this.currentViewers = channel.getCurrentViewers();
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

    public Channel.ChannelType getType() { return type; }
    public void setType(Channel.ChannelType type) { this.type = type; }

    public Channel.LatencyMode getLatencyMode() { return latencyMode; }
    public void setLatencyMode(Channel.LatencyMode latencyMode) { this.latencyMode = latencyMode; }

    public Channel.ChannelStatus getStatus() { return status; }
    public void setStatus(Channel.ChannelStatus status) { this.status = status; }

    public Boolean getAuthorized() { return authorized; }
    public void setAuthorized(Boolean authorized) { this.authorized = authorized; }

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
}