package com.example.ivs.service;

import com.example.ivs.entity.Stream;
import com.example.ivs.repository.StreamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ivs.IvsClient;
import software.amazon.awssdk.services.ivs.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IvsStreamService {
    
    private static final Logger logger = LoggerFactory.getLogger(IvsStreamService.class);
    
    @Autowired
    private IvsClient ivsClient;
    
    @Autowired
    private StreamRepository streamRepository;

    public Optional<Stream> getStreamByChannelArn(String channelArn) {
        logger.info("Getting stream for channel: {}", channelArn);
        
        try {
            GetStreamRequest request = GetStreamRequest.builder()
                    .channelArn(channelArn)
                    .build();
            
            GetStreamResponse response = ivsClient.getStream(request);
            
            if (response.stream() != null) {
                software.amazon.awssdk.services.ivs.model.Stream ivsStream = response.stream();
                
                // Check if stream exists in database
                Optional<Stream> existingStream = streamRepository.findByStreamId(ivsStream.streamId());
                
                Stream stream;
                if (existingStream.isPresent()) {
                    stream = existingStream.get();
                } else {
                    stream = new Stream(channelArn, ivsStream.streamId());
                }
                
                // Update stream information
                stream.setState(ivsStream.state() == StreamState.LIVE ? 
                    Stream.StreamState.LIVE : Stream.StreamState.OFFLINE);
                stream.setHealth(ivsStream.health().toString());
                
                if (ivsStream.viewerCount() != null) {
                    stream.setViewerCount(ivsStream.viewerCount().longValue());
                }
                
                if (ivsStream.startTime() != null) {
                    stream.setStartTime(LocalDateTime.now()); // Convert from Instant
                }
                
                stream.setUpdatedAt(LocalDateTime.now());
                
                Stream savedStream = streamRepository.save(stream);
                logger.info("Stream information updated: {}", savedStream.getStreamId());
                
                return Optional.of(savedStream);
            }
            
        } catch (ResourceNotFoundException e) {
            logger.info("No active stream found for channel: {}", channelArn);
        } catch (Exception e) {
            logger.error("Error getting stream information: {}", e.getMessage(), e);
        }
        
        return Optional.empty();
    }

    public List<Stream> getStreamsByChannelArn(String channelArn) {
        logger.info("Getting all streams for channel: {}", channelArn);
        return streamRepository.findByChannelArnOrderByStartTimeDesc(channelArn);
    }

    public List<Stream> getLiveStreams() {
        logger.info("Getting all live streams");
        return streamRepository.findLiveStreamsOrderByViewers();
    }

    public Optional<Stream> getStreamById(Long id) {
        logger.info("Getting stream by ID: {}", id);
        return streamRepository.findById(id);
    }

    public Stream updateStreamMetadata(Long streamId, String title, String description) {
        logger.info("Updating stream metadata: {}", streamId);
        
        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new RuntimeException("Stream not found"));
        
        stream.setTitle(title);
        stream.setDescription(description);
        stream.setUpdatedAt(LocalDateTime.now());
        
        return streamRepository.save(stream);
    }

    public void stopStream(String channelArn) {
        logger.info("Stopping stream for channel: {}", channelArn);
        
        try {
            StopStreamRequest request = StopStreamRequest.builder()
                    .channelArn(channelArn)
                    .build();
            
            ivsClient.stopStream(request);
            
            // Update stream in database
            Optional<Stream> streamOpt = streamRepository.findByChannelArn(channelArn)
                    .stream()
                    .filter(s -> s.getState() == Stream.StreamState.LIVE)
                    .findFirst();
            
            if (streamOpt.isPresent()) {
                Stream stream = streamOpt.get();
                stream.setState(Stream.StreamState.OFFLINE);
                stream.setEndTime(LocalDateTime.now());
                
                if (stream.getStartTime() != null) {
                    long duration = java.time.Duration.between(stream.getStartTime(), stream.getEndTime()).getSeconds();
                    stream.setDuration(duration);
                }
                
                stream.setUpdatedAt(LocalDateTime.now());
                streamRepository.save(stream);
                
                logger.info("Stream stopped and updated: {}", stream.getStreamId());
            }
            
        } catch (Exception e) {
            logger.error("Error stopping stream: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to stop stream: " + e.getMessage());
        }
    }

    public List<Stream> getStreamHistory(String channelArn, int limit) {
        logger.info("Getting stream history for channel: {} (limit: {})", channelArn, limit);
        
        List<Stream> streams = streamRepository.findByChannelArnOrderByStartTimeDesc(channelArn);
        
        return streams.size() > limit ? streams.subList(0, limit) : streams;
    }

    public void syncStreamStatuses() {
        logger.info("Syncing all stream statuses");
        
        List<Stream> liveStreams = streamRepository.findByState(Stream.StreamState.LIVE);
        
        for (Stream stream : liveStreams) {
            try {
                GetStreamRequest request = GetStreamRequest.builder()
                        .channelArn(stream.getChannelArn())
                        .build();
                
                GetStreamResponse response = ivsClient.getStream(request);
                
                if (response.stream() != null) {
                    software.amazon.awssdk.services.ivs.model.Stream ivsStream = response.stream();
                    
                    stream.setState(ivsStream.state() == StreamState.LIVE ? 
                        Stream.StreamState.LIVE : Stream.StreamState.OFFLINE);
                    stream.setHealth(ivsStream.health().toString());
                    
                    if (ivsStream.viewerCount() != null) {
                        stream.setViewerCount(ivsStream.viewerCount().longValue());
                    }
                    
                    if (stream.getState() == Stream.StreamState.OFFLINE && stream.getEndTime() == null) {
                        stream.setEndTime(LocalDateTime.now());
                        
                        if (stream.getStartTime() != null) {
                            long duration = java.time.Duration.between(stream.getStartTime(), stream.getEndTime()).getSeconds();
                            stream.setDuration(duration);
                        }
                    }
                    
                    stream.setUpdatedAt(LocalDateTime.now());
                    streamRepository.save(stream);
                }
                
            } catch (ResourceNotFoundException e) {
                // Stream no longer exists, mark as offline
                stream.setState(Stream.StreamState.OFFLINE);
                if (stream.getEndTime() == null) {
                    stream.setEndTime(LocalDateTime.now());
                    
                    if (stream.getStartTime() != null) {
                        long duration = java.time.Duration.between(stream.getStartTime(), stream.getEndTime()).getSeconds();
                        stream.setDuration(duration);
                    }
                }
                stream.setUpdatedAt(LocalDateTime.now());
                streamRepository.save(stream);
                
            } catch (Exception e) {
                logger.error("Error syncing stream status for {}: {}", stream.getStreamId(), e.getMessage());
            }
        }
        
        logger.info("Finished syncing {} stream statuses", liveStreams.size());
    }
}