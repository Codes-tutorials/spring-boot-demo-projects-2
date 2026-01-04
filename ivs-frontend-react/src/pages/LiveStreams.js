import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Alert, Spinner, Badge } from 'react-bootstrap';
import { FaPlay, FaEye, FaSync } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import { streamAPI, channelAPI } from '../services/api';
import moment from 'moment';

const LiveStreams = () => {
  const navigate = useNavigate();
  const [liveStreams, setLiveStreams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchLiveStreams();
    
    // Set up auto-refresh every 30 seconds
    const interval = setInterval(fetchLiveStreams, 30000);
    
    return () => clearInterval(interval);
  }, []);

  const fetchLiveStreams = async () => {
    try {
      setError(null);
      const response = await streamAPI.getLive();
      setLiveStreams(response.data);
    } catch (err) {
      console.error('Error fetching live streams:', err);
      setError('Failed to load live streams. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleWatch = (channelArn) => {
    navigate(`/player/${encodeURIComponent(channelArn)}`);
  };

  const handleViewChannel = (channelArn) => {
    // Find channel by ARN and navigate to its details
    channelAPI.getByArn(channelArn)
      .then(response => {
        navigate(`/channels/${response.data.id}`);
      })
      .catch(err => {
        console.error('Error finding channel:', err);
        setError('Failed to find channel details.');
      });
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

  const formatStartTime = (startTime) => {
    return startTime ? moment(startTime).format('MMM DD, HH:mm') : 'Unknown';
  };

  if (loading) {
    return (
      <Container>
        <div className="loading-spinner">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading live streams...</span>
          </Spinner>
        </div>
      </Container>
    );
  }

  return (
    <Container fluid>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Live Streams ({liveStreams.length})</h1>
        <Button variant="outline-primary" onClick={fetchLiveStreams}>
          <FaSync className="me-2" />
          Refresh
        </Button>
      </div>

      {error && (
        <Alert variant="danger" dismissible onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {liveStreams.length === 0 ? (
        <div className="text-center py-5">
          <h4 className="text-muted">No Live Streams</h4>
          <p className="text-muted">
            There are currently no active live streams. Create a channel and start streaming to see them here.
          </p>
          <Button variant="primary" onClick={() => navigate('/channels/create')}>
            Create Channel
          </Button>
        </div>
      ) : (
        <Row>
          {liveStreams.map((stream) => (
            <Col md={6} lg={4} key={stream.id} className="mb-4">
              <Card className="stream-card h-100">
                <Card.Body className="d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <Card.Title className="mb-0">
                      {stream.title || 'Untitled Stream'}
                    </Card.Title>
                    <Badge bg="danger" className="live-indicator">
                      LIVE
                    </Badge>
                  </div>
                  
                  {stream.description && (
                    <Card.Text className="text-muted mb-3">
                      {stream.description}
                    </Card.Text>
                  )}
                  
                  <div className="mb-3 flex-grow-1">
                    <div className="mb-2">
                      <small className="text-muted">
                        <strong>Channel:</strong> {stream.channelArn.split('/').pop()}
                      </small>
                    </div>
                    
                    <div className="mb-2">
                      <small className="text-muted">
                        <strong>Started:</strong> {formatStartTime(stream.startTime)}
                      </small>
                    </div>
                    
                    <div className="mb-2">
                      <small className="text-muted">
                        <strong>Duration:</strong> {formatDuration(stream.startTime)}
                      </small>
                    </div>
                    
                    <div className="mb-2">
                      <small className="text-muted">
                        <strong>Health:</strong> 
                        <Badge 
                          bg={stream.health === 'HEALTHY' ? 'success' : 'warning'} 
                          className="ms-1"
                        >
                          {stream.health}
                        </Badge>
                      </small>
                    </div>
                    
                    <div className="viewer-count">
                      <FaEye className="me-1" />
                      <strong>{stream.viewerCount || 0} viewers</strong>
                    </div>
                  </div>
                  
                  <div className="d-flex gap-2 mt-auto">
                    <Button 
                      variant="success" 
                      size="sm" 
                      onClick={() => handleWatch(stream.channelArn)}
                      className="flex-grow-1"
                    >
                      <FaPlay className="me-1" />
                      Watch
                    </Button>
                    <Button 
                      variant="outline-primary" 
                      size="sm" 
                      onClick={() => handleViewChannel(stream.channelArn)}
                    >
                      Channel
                    </Button>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}
      
      {liveStreams.length > 0 && (
        <div className="text-center mt-4">
          <small className="text-muted">
            Live streams are automatically refreshed every 30 seconds
          </small>
        </div>
      )}
    </Container>
  );
};

export default LiveStreams;