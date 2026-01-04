package com.example.ivs.controller;

import com.example.ivs.dto.ChannelRequest;
import com.example.ivs.dto.ChannelResponse;
import com.example.ivs.service.IvsChannelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/channels")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ChannelController {

    @Autowired
    private IvsChannelService channelService;

    @PostMapping
    public ResponseEntity<ChannelResponse> createChannel(@Valid @RequestBody ChannelRequest request) {
        try {
            ChannelResponse response = channelService.createChannel(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ChannelResponse>> getAllChannels() {
        List<ChannelResponse> channels = channelService.getAllChannels();
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChannelResponse> getChannelById(@PathVariable Long id) {
        Optional<ChannelResponse> channel = channelService.getChannelById(id);
        return channel.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/arn/{channelArn}")
    public ResponseEntity<ChannelResponse> getChannelByArn(@PathVariable String channelArn) {
        Optional<ChannelResponse> channel = channelService.getChannelByArn(channelArn);
        return channel.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/live")
    public ResponseEntity<List<ChannelResponse>> getLiveChannels() {
        List<ChannelResponse> liveChannels = channelService.getLiveChannels();
        return ResponseEntity.ok(liveChannels);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChannelResponse>> searchChannels(@RequestParam String keyword) {
        List<ChannelResponse> channels = channelService.searchChannels(keyword);
        return ResponseEntity.ok(channels);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChannelResponse> updateChannel(@PathVariable Long id, 
                                                        @Valid @RequestBody ChannelRequest request) {
        try {
            ChannelResponse response = channelService.updateChannel(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable Long id) {
        try {
            channelService.deleteChannel(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/sync")
    public ResponseEntity<Void> syncChannelStatus(@PathVariable Long id) {
        try {
            Optional<ChannelResponse> channel = channelService.getChannelById(id);
            if (channel.isPresent()) {
                channelService.syncChannelStatus(channel.get().getChannelArn());
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/sync-all")
    public ResponseEntity<Void> syncAllChannelStatuses() {
        try {
            channelService.syncAllChannelStatuses();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}