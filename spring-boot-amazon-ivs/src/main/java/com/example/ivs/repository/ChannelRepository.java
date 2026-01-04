package com.example.ivs.repository;

import com.example.ivs.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    
    Optional<Channel> findByChannelArn(String channelArn);
    
    Optional<Channel> findByName(String name);
    
    List<Channel> findByStatus(Channel.ChannelStatus status);
    
    List<Channel> findByType(Channel.ChannelType type);
    
    @Query("SELECT c FROM Channel c WHERE c.status = 'LIVE' ORDER BY c.currentViewers DESC")
    List<Channel> findLiveChannelsOrderByViewers();
    
    @Query("SELECT c FROM Channel c WHERE c.name LIKE %?1% OR c.description LIKE %?1%")
    List<Channel> searchChannels(String keyword);
    
    @Query("SELECT COUNT(c) FROM Channel c WHERE c.status = 'LIVE'")
    Long countLiveChannels();
    
    @Query("SELECT SUM(c.currentViewers) FROM Channel c WHERE c.status = 'LIVE'")
    Long getTotalCurrentViewers();
}