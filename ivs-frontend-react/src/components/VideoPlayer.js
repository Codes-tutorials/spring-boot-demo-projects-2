import React, { useEffect, useRef, useState } from 'react';
import { Alert, Spinner } from 'react-bootstrap';

const VideoPlayer = ({ playbackUrl, autoplay = true, muted = false }) => {
  const videoRef = useRef(null);
  const playerRef = useRef(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [playerReady, setPlayerReady] = useState(false);

  useEffect(() => {
    let isMounted = true;

    const initializePlayer = async () => {
      try {
        // Check if IVS player is available
        if (!window.IVSPlayer) {
          // Load IVS Player script dynamically
          await loadIVSPlayerScript();
        }

        if (!isMounted) return;

        const { IVSPlayer } = window;
        
        if (IVSPlayer.isPlayerSupported) {
          const player = IVSPlayer.create();
          playerRef.current = player;

          player.attachHTMLVideoElement(videoRef.current);

          // Set up event listeners
          player.addEventListener(IVSPlayer.PlayerState.PLAYING, () => {
            if (isMounted) {
              setIsLoading(false);
              setError(null);
            }
          });

          player.addEventListener(IVSPlayer.PlayerState.BUFFERING, () => {
            if (isMounted) {
              setIsLoading(true);
            }
          });

          player.addEventListener(IVSPlayer.PlayerEventType.ERROR, (err) => {
            if (isMounted) {
              setError('Failed to load stream: ' + err.message);
              setIsLoading(false);
            }
          });

          player.addEventListener(IVSPlayer.PlayerState.READY, () => {
            if (isMounted) {
              setPlayerReady(true);
            }
          });

          // Load the stream
          if (playbackUrl) {
            player.load(playbackUrl);
            if (autoplay) {
              player.play();
            }
          }

        } else {
          if (isMounted) {
            setError('IVS Player is not supported in this browser');
            setIsLoading(false);
          }
        }
      } catch (err) {
        if (isMounted) {
          setError('Failed to initialize player: ' + err.message);
          setIsLoading(false);
        }
      }
    };

    initializePlayer();

    return () => {
      isMounted = false;
      if (playerRef.current) {
        playerRef.current.delete();
        playerRef.current = null;
      }
    };
  }, [playbackUrl, autoplay]);

  const loadIVSPlayerScript = () => {
    return new Promise((resolve, reject) => {
      if (window.IVSPlayer) {
        resolve();
        return;
      }

      const script = document.createElement('script');
      script.src = 'https://player.live-video.net/1.21.1/amazon-ivs-player.min.js';
      script.onload = resolve;
      script.onerror = reject;
      document.head.appendChild(script);
    });
  };

  if (error) {
    return (
      <Alert variant="danger">
        <Alert.Heading>Playback Error</Alert.Heading>
        <p>{error}</p>
      </Alert>
    );
  }

  return (
    <div className="video-player-container">
      {isLoading && (
        <div className="loading-spinner">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading stream...</span>
          </Spinner>
        </div>
      )}
      
      <video
        ref={videoRef}
        className="video-player"
        controls
        muted={muted}
        playsInline
        style={{ display: isLoading ? 'none' : 'block' }}
      />
      
      {!playbackUrl && (
        <Alert variant="info">
          No playback URL provided
        </Alert>
      )}
    </div>
  );
};

export default VideoPlayer;