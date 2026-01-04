package com.example.ivs.dto;

import com.example.ivs.entity.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChannelRequest {
    
    @NotBlank(message = "Channel name is required")
    @Size(min = 1, max = 128, message = "Channel name must be between 1 and 128 characters")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    private Channel.ChannelType type = Channel.ChannelType.STANDARD;
    
    private Channel.LatencyMode latencyMode = Channel.LatencyMode.LOW;
    
    private Boolean authorized = false;
    
    private String recordingConfigurationArn;
    
    private String tags;

    public ChannelRequest() {}

    public ChannelRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Channel.ChannelType getType() { return type; }
    public void setType(Channel.ChannelType type) { this.type = type; }

    public Channel.LatencyMode getLatencyMode() { return latencyMode; }
    public void setLatencyMode(Channel.LatencyMode latencyMode) { this.latencyMode = latencyMode; }

    public Boolean getAuthorized() { return authorized; }
    public void setAuthorized(Boolean authorized) { this.authorized = authorized; }

    public String getRecordingConfigurationArn() { return recordingConfigurationArn; }
    public void setRecordingConfigurationArn(String recordingConfigurationArn) { this.recordingConfigurationArn = recordingConfigurationArn; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}