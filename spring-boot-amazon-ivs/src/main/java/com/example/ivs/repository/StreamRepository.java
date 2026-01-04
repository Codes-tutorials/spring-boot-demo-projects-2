package com.example.ivs.repository;

import com.example.ivs.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {
    
    Optional<Stream> findByStreamId(String streamId);
    
    List<Stream> findByChannelArn(String channelArn);
    
    List<Stream> findByState(Stream.StreamState state);
    
    @Query("SELECT s FROM Stream s WHERE s.channelArn = ?1 ORDER BY s.startTime DESC")
    List<Stream> findByChannelArnOrderByStartTimeDesc(String channelArn);
    
    @Query("SELECT s FROM Stream s WHERE s.state = 'LIVE' ORDER BY s.viewerCount DESC")
    List<Stream> findLiveStreamsOrderByViewers();
    
    @Query("SELECT s FROM Stream s WHERE s.startTime BETWEEN ?1 AND ?2")
    List<Stream> findStreamsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(s) FROM Stream s WHERE s.state = 'LIVE'")
    Long countLiveStreams();
    
    @Query("SELECT AVG(s.duration) FROM Stream s WHERE s.duration IS NOT NULL")
    Double getAverageStreamDuration();
}