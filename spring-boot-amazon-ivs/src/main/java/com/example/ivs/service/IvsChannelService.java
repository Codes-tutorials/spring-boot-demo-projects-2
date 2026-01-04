package com.example.ivs.service;

import com.example.ivs.dto.ChannelRequest;
import com.example.ivs.dto.ChannelResponse;
import com.example.ivs.entity.Channel;
import com.example.ivs.repository.ChannelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ivs.IvsClient;
import software.amazon.awssdk.services.ivs.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IvsChannelService {
    
    private static final Logger logger = LoggerFactory.getLogger(IvsChannelService.class);
    
    @Autowired
    private IvsClient ivsClient;
    
    @Autowired
    private ChannelRepository channelRepository;

    public ChannelResponse createChannel(ChannelRequest request) {
        try {
            logger.info("Creating IVS channel: {}", request.getName());
            
            CreateChannelRequest.Builder channelRequestBuilder = CreateChannelRequest.builder()
                    .name(request.getName())
                    .type(ChannelType.fromValue(request.getType().name()))
                    .latencyMode(ChannelLatencyMode.fromValue(request.getLatencyMode().name()))
                    .authorized(request.getAuthorized());
            
            if (request.getRecordingConfigurationArn() != null) {
                channelRequestBuilder.recordingConfigurationArn(request.getRecordingConfigurationArn());
            }
            
            CreateChannelResponse response = ivsClient.createChannel(channelRequestBuilder.build());
            
            // Save channel to database
            Channel channel = new Channel(
                response.channel().arn(),
                request.getName(),
                response.streamKey().value(),
                response.channel().playbackUrl(),
                response.channel().ingestEndpoint()
            );
            
            channel.setDescription(request.getDescription());
            channel.setType(request.getType());
            channel.setLatencyMode(request.getLatencyMode());
            channel.setAuthorized(request.getAuthorized());
            channel.setTags(request.getTags());
            channel.setStatus(Channel.ChannelStatus.OFFLINE);
            
            Channel savedChannel = channelRepository.save(channel);
            
            logger.info("Channel created successfully: {}", savedChannel.getChannelArn());
            return new ChannelResponse(savedChannel);
            
        } catch (Exception e) {
            logger.error("Error creating channel: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create channel: " + e.getMessage());
        }
    }

    public List<ChannelResponse> getAllChannels() {
        logger.info("Fetching all channels");
        return channelRepository.findAll().stream()
                .map(ChannelResponse::new)
                .collect(Collectors.toList());
    }

    public Optional<ChannelResponse> getChannelById(Long id) {
        logger.info("Fetching channel by ID: {}", id);
        return channelRepository.findById(id)
                .map(ChannelResponse::new);
    }

    public Optional<ChannelResponse> getChannelByArn(String channelArn) {
        logger.info("Fetching channel by ARN: {}", channelArn);
        return channelRepository.findByChannelArn(channelArn)
                .map(ChannelResponse::new);
    }

    public List<ChannelResponse> getLiveChannels() {
        logger.info("Fetching live channels");
        return channelRepository.findLiveChannelsOrderByViewers().stream()
                .map(ChannelResponse::new)
                .collect(Collectors.toList());
    }

    public List<ChannelResponse> searchChannels(String keyword) {
        logger.info("Searching channels with keyword: {}", keyword);
        return channelRepository.searchChannels(keyword).stream()
                .map(ChannelResponse::new)
                .collect(Collectors.toList());
    }

    public ChannelResponse updateChannel(Long id, ChannelRequest request) {
        logger.info("Updating channel: {}", id);
        
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel not found"));
        
        try {
            // Update IVS channel
            UpdateChannelRequest updateRequest = UpdateChannelRequest.builder()
                    .arn(channel.getChannelArn())
                    .name(request.getName())
                    .latencyMode(ChannelLatencyMode.fromValue(request.getLatencyMode().name()))
                    .type(ChannelType.fromValue(request.getType().name()))
                    .authorized(request.getAuthorized())
                    .build();
            
            ivsClient.updateChannel(updateRequest);
            
            // Update local database
            channel.setName(request.getName());
            channel.setDescription(request.getDescription());
            channel.setType(request.getType());
            channel.setLatencyMode(request.getLatencyMode());
            channel.setAuthorized(request.getAuthorized());
            channel.setTags(request.getTags());
            channel.setUpdatedAt(LocalDateTime.now());
            
            Channel updatedChannel = channelRepository.save(channel);
            
            logger.info("Channel updated successfully: {}", updatedChannel.getChannelArn());
            return new ChannelResponse(updatedChannel);
            
        } catch (Exception e) {
            logger.error("Error updating channel: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update channel: " + e.getMessage());
        }
    }

    public void deleteChannel(Long id) {
        logger.info("Deleting channel: {}", id);
        
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel not found"));
        
        try {
            // Delete from IVS
            DeleteChannelRequest deleteRequest = DeleteChannelRequest.builder()
                    .arn(channel.getChannelArn())
                    .build();
            
            ivsClient.deleteChannel(deleteRequest);
            
            // Delete from database
            channelRepository.delete(channel);
            
            logger.info("Channel deleted successfully: {}", channel.getChannelArn());
            
        } catch (Exception e) {
            logger.error("Error deleting channel: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete channel: " + e.getMessage());
        }
    }

    public void syncChannelStatus(String channelArn) {
        logger.info("Syncing channel status: {}", channelArn);
        
        Optional<Channel> channelOpt = channelRepository.findByChannelArn(channelArn);
        if (channelOpt.isEmpty()) {
            logger.warn("Channel not found in database: {}", channelArn);
            return;
        }
        
        Channel channel = channelOpt.get();
        
        try {
            // Get stream information
            GetStreamRequest streamRequest = GetStreamRequest.builder()
                    .channelArn(channelArn)
                    .build();
            
            GetStreamResponse streamResponse = ivsClient.getStream(streamRequest);
            
            if (streamResponse.stream() != null) {
                software.amazon.awssdk.services.ivs.model.Stream ivsStream = streamResponse.stream();
                
                // Update channel status
                channel.setStatus(ivsStream.state() == StreamState.LIVE ? 
                    Channel.ChannelStatus.LIVE : Channel.ChannelStatus.OFFLINE);
                
                if (ivsStream.viewerCount() != null) {
                    channel.setCurrentViewers(ivsStream.viewerCount().longValue());
                }
                
                if (ivsStream.startTime() != null && channel.getStatus() == Channel.ChannelStatus.LIVE) {
                    channel.setLastStreamStartTime(LocalDateTime.now());
                }
                
                channel.setUpdatedAt(LocalDateTime.now());
                channelRepository.save(channel);
                
                logger.info("Channel status synced: {} - {}", channelArn, channel.getStatus());
            }
            
        } catch (ResourceNotFoundException e) {
            // Stream not found, channel is offline
            channel.setStatus(Channel.ChannelStatus.OFFLINE);
            channel.setCurrentViewers(0L);
            channel.setUpdatedAt(LocalDateTime.now());
            channelRepository.save(channel);
            
            logger.info("Channel is offline: {}", channelArn);
            
        } catch (Exception e) {
            logger.error("Error syncing channel status: {}", e.getMessage(), e);
        }
    }

    public void syncAllChannelStatuses() {
        logger.info("Syncing all channel statuses");
        
        List<Channel> channels = channelRepository.findAll();
        for (Channel channel : channels) {
            syncChannelStatus(channel.getChannelArn());
        }
        
        logger.info("Finished syncing {} channels", channels.size());
    }
}