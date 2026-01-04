import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Alert, Spinner, Badge } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import { FaArrowLeft, FaEye, FaSync, FaExternalLinkAlt } from 'react-icons/fa';
import { channelAPI, streamAPI } from '../services/api';
import VideoPlayer from '../components/VideoPlayer';
import moment from 'moment';

const StreamPlayer = () => {
  const { channelArn } = useParams();
  const navigate = useNavigate();
  const [channel, setChannel] = useState(null);
  const [stream, setStream] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchStreamData();
    
    // Set up auto-refresh every 10 seconds for viewer count
    const interval = setInterval(fetchStreamData, 10000);
    
    return () => clearInterval(interval);
  }, [channelArn]);

  const fetchStreamData = async () => {
    try {
      setError(null);
      
      const decodedChannelArn = decodeURIComponent(channelArn);
      
      const [channelResponse, streamResponse] = await Promise.all([
        channelAPI.getByArn(decodedChannelArn),
        streamAPI.getByChannelArn(decodedChannelArn).catch(() => ({ data: null }))
      ]);
      
      setChannel(channelResponse.data);
      setStream(streamResponse.data);
      
    } catch (err) {
      console.error('Error fetching stream data:', err);
      setError('Failed to load stream information. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    navigate(-1);
  };

  const handleViewChannel = () => {
    if (channel) {
      navigate(`/channels/${channel.id}`);
    }
  };

  const handleRefresh = () => {
    setLoading(true);
    fetchStreamData();
  };

  const formatDuration = (startTime) => {
    if (!startTime) return 'Unknown';
    
    const start = moment(startTime);
    const now = moment();
    const duration = moment.duration(now.diff(start));
    
    const hours = Math.floor(duration.asHours());
    const minutes = Math.floor(duration.asMinutes()) % 60;
    
    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    } else {
      return `${minutes}m`;
    }
  };

  const getStatusBadge = () => {
    if (!channel) return null;
    
    switch (channel.status) {
      case 'LIVE':
        return <Badge bg="danger" className="live-indicator">LIVE</Badge>;
      case 'OFFLINE':
        return <Badge bg="secondary" className="offline-indicator">OFFLINE</Badge>;
      default:
        return <Badge bg="secondary">{channel.status}</Badge>;
    }
  };

  if (loading) {
    return (
      <Container>
        <div className="loading-spinner">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading stream...</span>
          </Spinner>
        </div>
      </Container>
    );
  }

  if (error || !channel) {
    return (
      <Container>
        <div className="d-flex align-items-center mb-4">
          <Button variant="outline-secondary" onClick={handleBack} className="me-3">
            <FaArrowLeft />
          </Button>
          <h1>Stream Player</h1>
        </div>
        
        <Alert variant="danger">
          {error || 'Channel not found.'}
        </Alert>
      </Container>
    );
  }

  return (
    <Container fluid>
      <div className="d-flex align-items-center justify-content-between mb-4">
        <div className="d-flex align-items-center">
          <Button variant="outline-secondary" onClick={handleBack} className="me-3">
            <FaArrowLeft />
          </Button>
          <h1 className="me-3">{channel.name}</h1>
          {getStatusBadge()}
        </div>
        
        <div className="d-flex gap-2">
          <Button variant="outline-info" size="sm" onClick={handleRefresh}>
            <FaSync className="me-1" />
            Refresh
          </Button>
          <Button variant="outline-primary" size="sm" onClick={handleViewChannel}>
            <FaExternalLinkAlt className="me-1" />
            Channel Details
          </Button>
        </div>
      </div>

      <Row>
        {/* Video Player */}
        <Col lg={8}>
          <Card className="mb-4">
            <Card.Body className="p-0">
              {channel.status === 'LIVE' ? (
                <VideoPlayer 
                  playbackUrl={channel.playbackUrl}
                  autoplay={true}
                  muted={false}
                />
              ) : (
                <div className="text-center py-5">
                  <h4 className="text-muted">Stream Offline</h4>
                  <p className="text-muted">
                    This channel is currently not streaming. Check back later or explore other live streams.
                  </p>
                  <Button variant="primary" onClick={() => navigate('/live-streams')}>
                    Browse Live Streams
                  </Button>
                </div>
              )}
            </Card.Body>
          </Card>
          
          {/* Stream Information */}
          {channel.status === 'LIVE' && (
            <Card>
              <Card.Header>
                <h5 className="mb-0">Stream Information</h5>
              </Card.Header>
              <Card.Body>
                <Row>
                  <Col md={6}>
                    <div className="mb-3">
                      <strong>Channel:</strong> {channel.name}
                    </div>
                    
                    {channel.description && (
                      <div className="mb-3">
                        <strong>Description:</strong>
                        <p className="mt-1">{channel.description}</p>
                      </div>
                    )}
                    
                    <div className="mb-3">
                      <strong>Type:</strong> {channel.type}
                    </div>
                    
                    <div className="mb-3">
                      <strong>Latency Mode:</strong> {channel.latencyMode}
                    </div>
                  </Col>
                  
                  <Col md={6}>
                    {stream && (
                      <>
                        {stream.title && (
                          <div className="mb-3">
                            <strong>Stream Title:</strong> {stream.title}
                          </div>
                        )}
                        
                        <div className="mb-3">
                          <strong>Started:</strong> {moment(stream.startTime).format('MMM DD, YYYY HH:mm')}
                        </div>
                        
                        <div className="mb-3">
                          <strong>Duration:</strong> {formatDuration(stream.startTime)}
                        </div>
                        
                        <div className="mb-3">
                          <strong>Health:</strong>
                          <Badge 
                            bg={stream.health === 'HEALTHY' ? 'success' : 'warning'} 
                            className="ms-2"
                          >
                            {stream.health}
                          </Badge>
                        </div>
                      </>
                    )}
                  </Col>
                </Row>
              </Card.Body>
            </Card>
          )}
        </Col>

        {/* Sidebar */}
        <Col lg={4}>
          {/* Viewer Count */}
          {channel.status === 'LIVE' && (
            <Card className="mb-4">
              <Card.Body className="text-center">
                <div className="stats-number viewer-count">
                  <FaEye className="me-2" />
                  {channel.currentViewers || 0}
                </div>
                <div className="text-muted">Current Viewers</div>
              </Card.Body>
            </Card>
          )}
          
          {/* Channel Stats */}
          <Card className="mb-4">
            <Card.Header>
              <h6 className="mb-0">Channel Statistics</h6>
            </Card.Header>
            <Card.Body>
              <div className="mb-2">
                <strong>Total Viewers:</strong> {channel.totalViewers || 0}
              </div>
              
              <div className="mb-2">
                <strong>Created:</strong> {moment(channel.createdAt).format('MMM DD, YYYY')}
              </div>
              
              {channel.lastStreamStartTime && (
                <div className="mb-2">
                  <strong>Last Stream:</strong> {moment(channel.lastStreamStartTime).format('MMM DD, YYYY')}
                </div>
              )}
            </Card.Body>
          </Card>
          
          {/* Playback Information */}
          <Card>
            <Card.Header>
              <h6 className="mb-0">Playback Information</h6>
            </Card.Header>
            <Card.Body>
              <div className="mb-2">
                <strong>Playback URL:</strong>
                <div className="stream-key-container mt-1">
                  <small className="text-break">{channel.playbackUrl}</small>
                </div>
              </div>
              
              <div className="mb-2">
                <strong>Channel ARN:</strong>
                <div className="stream-key-container mt-1">
                  <small className="text-break">{channel.channelArn}</small>
                </div>
              </div>
              
              <div className="text-muted small mt-3">
                <p>
                  You can use the playback URL with any HLS-compatible video player 
                  or integrate it into your own applications.
                </p>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default StreamPlayer;