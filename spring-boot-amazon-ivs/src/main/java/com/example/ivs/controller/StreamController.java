package com.example.ivs.controller;

import com.example.ivs.entity.Stream;
import com.example.ivs.service.IvsStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/streams")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class StreamController {

    @Autowired
    private IvsStreamService streamService;

    @GetMapping("/channel/{channelArn}")
    public ResponseEntity<Stream> getStreamByChannelArn(@PathVariable String channelArn) {
        Optional<Stream> stream = streamService.getStreamByChannelArn(channelArn);
        return stream.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/channel/{channelArn}/all")
    public ResponseEntity<List<Stream>> getStreamsByChannelArn(@PathVariable String channelArn) {
        List<Stream> streams = streamService.getStreamsByChannelArn(channelArn);
        return ResponseEntity.ok(streams);
    }

    @GetMapping("/channel/{channelArn}/history")
    public ResponseEntity<List<Stream>> getStreamHistory(@PathVariable String channelArn,
                                                        @RequestParam(defaultValue = "10") int limit) {
        List<Stream> streams = streamService.getStreamHistory(channelArn, limit);
        return ResponseEntity.ok(streams);
    }

    @GetMapping("/live")
    public ResponseEntity<List<Stream>> getLiveStreams() {
        List<Stream> liveStreams = streamService.getLiveStreams();
        return ResponseEntity.ok(liveStreams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stream> getStreamById(@PathVariable Long id) {
        Optional<Stream> stream = streamService.getStreamById(id);
        return stream.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/metadata")
    public ResponseEntity<Stream> updateStreamMetadata(@PathVariable Long id,
                                                      @RequestBody Map<String, String> metadata) {
        try {
            String title = metadata.get("title");
            String description = metadata.get("description");
            
            Stream updatedStream = streamService.updateStreamMetadata(id, title, description);
            return ResponseEntity.ok(updatedStream);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/channel/{channelArn}/stop")
    public ResponseEntity<Void> stopStream(@PathVariable String channelArn) {
        try {
            streamService.stopStream(channelArn);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/sync-all")
    public ResponseEntity<Void> syncAllStreamStatuses() {
        try {
            streamService.syncStreamStatuses();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}