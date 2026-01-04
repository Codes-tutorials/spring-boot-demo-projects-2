import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Alert, Spinner, Badge, Form, InputGroup } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import { FaArrowLeft, FaPlay, FaCopy, FaEdit, FaTrash, FaSync, FaEye } from 'react-icons/fa';
import { channelAPI, streamAPI } from '../services/api';
import moment from 'moment';

const ChannelDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [channel, setChannel] = useState(null);
  const [streams, setStreams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [copySuccess, setCopySuccess] = useState('');

  useEffect(() => {
    fetchChannelDetails();
  }, [id]);

  const fetchChannelDetails = async () => {
    try {
      setLoading(true);
      setError(null);

      const [channelResponse, streamsResponse] = await Promise.all([
        channelAPI.getById(id),
        channelAPI.getById(id).then(res => 
          streamAPI.getHistory(res.data.channelArn, 10)
        ).catch(() => ({ data: [] }))
      ]);

      setChannel(channelResponse.data);
      setStreams(streamsResponse.data || []);
    } catch (err) {
      console.error('Error fetching channel details:', err);
      setError('Failed to load channel details. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    navigate('/channels');
  };

  const handleWatch = () => {
    navigate(`/player/${encodeURIComponent(channel.channelArn)}`);
  };

  const handleEdit = () => {
    // For now, just show an alert. In a real app, you'd navigate to an edit form
    alert('Edit functionality would be implemented here');
  };

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this channel? This action cannot be undone.')) {
      try {
        await channelAPI.delete(id);
        navigate('/channels');
      } catch (err) {
        console.error('Error deleting channel:', err);
        setError('Failed to delete channel. Please try again.');
      }
    }
  };

  const handleSync = async () => {
    try {
      await channelAPI.sync(id);
      fetchChannelDetails(); // Refresh data
    } catch (err) {
      console.error('Error syncing channel:', err);
      setError('Failed to sync channel. Please try again.');
    }
  };

  const copyToClipboard = async (text, type) => {
    try {
      await navigator.clipboard.writeText(text);
      setCopySuccess(`${type} copied to clipboard!`);
      setTimeout(() => setCopySuccess(''), 3000);
    } catch (err) {
      console.error('Failed to copy:', err);
    }
  };

  const getStatusBadge = () => {
    if (!channel) return null;
    
    switch (channel.status) {
      case 'LIVE':
        return <Badge bg="danger" className="live-indicator">LIVE</Badge>;
      case 'OFFLINE':
        return <Badge bg="secondary" className="offline-indicator">OFFLINE</Badge>;
      case 'CREATING':
        return <Badge bg="warning">CREATING</Badge>;
      case 'ERROR':
        return <Badge bg="danger">ERROR</Badge>;
      default:
        return <Badge bg="secondary">UNKNOWN</Badge>;
    }
  };

  const formatDate = (dateString) => {
    return dateString ? moment(dateString).format('MMM DD, YYYY HH:mm:ss') : 'Never';
  };

  if (loading) {
    return (
      <Container>
        <div className="loading-spinner">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading channel details...</span>
          </Spinner>
        </div>
      </Container>
    );
  }

  if (!channel) {
    return (
      <Container>
        <Alert variant="danger">
          Channel not found.
        </Alert>
      </Container>
    );
  }

  return (
    <Container fluid>
      <div className="d-flex align-items-center mb-4">
        <Button variant="outline-secondary" onClick={handleBack} className="me-3">
          <FaArrowLeft />
        </Button>
        <h1 className="me-3">{channel.name}</h1>
        {getStatusBadge()}
      </div>

      {error && (
        <Alert variant="danger" dismissible onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {copySuccess && (
        <Alert variant="success" dismissible onClose={() => setCopySuccess('')}>
          {copySuccess}
        </Alert>
      )}

      <Row>
        {/* Channel Information */}
        <Col md={8}>
          <Card className="channel-details mb-4">
            <Card.Header className="d-flex justify-content-between align-items-center">
              <h5 className="mb-0">Channel Information</h5>
              <div className="d-flex gap-2">
                {channel.status === 'LIVE' && (
                  <Button variant="success" size="sm" onClick={handleWatch}>
                    <FaPlay className="me-1" />
                    Watch Live
                  </Button>
                )}
                <Button variant="outline-primary" size="sm" onClick={handleEdit}>
                  <FaEdit className="me-1" />
                  Edit
                </Button>
                <Button variant="outline-info" size="sm" onClick={handleSync}>
                  <FaSync className="me-1" />
                  Sync
                </Button>
                <Button variant="outline-danger" size="sm" onClick={handleDelete}>
                  <FaTrash className="me-1" />
                  Delete
                </Button>
              </div>
            </Card.Header>
            <Card.Body>
              <Row>
                <Col md={6}>
                  <div className="mb-3">
                    <strong>Channel ARN:</strong>
                    <div className="stream-key-container mt-1">
                      <small>{channel.channelArn}</small>
                      <Button
                        variant="outline-secondary"
                        size="sm"
                        className="copy-button"
                        onClick={() => copyToClipboard(channel.channelArn, 'Channel ARN')}
                      >
                        <FaCopy />
                      </Button>
                    </div>
                  </div>
                  
                  <div className="mb-3">
                    <strong>Type:</strong> {channel.type}
                  </div>
                  
                  <div className="mb-3">
                    <strong>Latency Mode:</strong> {channel.latencyMode}
                  </div>
                  
                  <div className="mb-3">
                    <strong>Authorized:</strong> {channel.authorized ? 'Yes' : 'No'}
                  </div>
                </Col>
                
                <Col md={6}>
                  <div className="mb-3">
                    <strong>Created:</strong> {formatDate(channel.createdAt)}
                  </div>
                  
                  <div className="mb-3">
                    <strong>Last Updated:</strong> {formatDate(channel.updatedAt)}
                  </div>
                  
                  <div className="mb-3">
                    <strong>Last Stream:</strong> {formatDate(channel.lastStreamStartTime)}
                  </div>
                  
                  {channel.status === 'LIVE' && (
                    <div className="mb-3 viewer-count">
                      <FaEye className="me-1" />
                      <strong>Current Viewers:</strong> {channel.currentViewers || 0}
                    </div>
                  )}
                </Col>
              </Row>
              
              {channel.description && (
                <div className="mb-3">
                  <strong>Description:</strong>
                  <p className="mt-1">{channel.description}</p>
                </div>
              )}
            </Card.Body>
          </Card>

          {/* Streaming Configuration */}
          <Card className="mb-4">
            <Card.Header>
              <h5 className="mb-0">Streaming Configuration</h5>
            </Card.Header>
            <Card.Body>
              <div className="mb-3">
                <strong>Ingest Endpoint:</strong>
                <InputGroup className="mt-1">
                  <Form.Control
                    type="text"
                    value={channel.ingestEndpoint}
                    readOnly
                  />
                  <Button
                    variant="outline-secondary"
                    onClick={() => copyToClipboard(channel.ingestEndpoint, 'Ingest Endpoint')}
                  >
                    <FaCopy />
                  </Button>
                </InputGroup>
              </div>
              
              <div className="mb-3">
                <strong>Stream Key:</strong>
                <InputGroup className="mt-1">
                  <Form.Control
                    type="password"
                    value={channel.streamKey}
                    readOnly
                  />
                  <Button
                    variant="outline-secondary"
                    onClick={() => copyToClipboard(channel.streamKey, 'Stream Key')}
                  >
                    <FaCopy />
                  </Button>
                </InputGroup>
                <Form.Text className="text-muted">
                  Keep your stream key secure. Anyone with this key can stream to your channel.
                </Form.Text>
              </div>
              
              <div className="mb-3">
                <strong>Playback URL:</strong>
                <InputGroup className="mt-1">
                  <Form.Control
                    type="text"
                    value={channel.playbackUrl}
                    readOnly
                  />
                  <Button
                    variant="outline-secondary"
                    onClick={() => copyToClipboard(channel.playbackUrl, 'Playback URL')}
                  >
                    <FaCopy />
                  </Button>
                </InputGroup>
              </div>
            </Card.Body>
          </Card>
        </Col>

        {/* Stream History */}
        <Col md={4}>
          <Card>
            <Card.Header>
              <h5 className="mb-0">Recent Streams</h5>
            </Card.Header>
            <Card.Body>
              {streams.length === 0 ? (
                <p className="text-muted">No stream history available.</p>
              ) : (
                streams.map((stream) => (
                  <div key={stream.id} className="mb-3 p-3 border rounded">
                    <div className="d-flex justify-content-between align-items-center mb-2">
                      <strong>{stream.title || 'Untitled Stream'}</strong>
                      <Badge bg={stream.state === 'LIVE' ? 'danger' : 'secondary'}>
                        {stream.state}
                      </Badge>
                    </div>
                    
                    {stream.description && (
                      <p className="text-muted small mb-2">{stream.description}</p>
                    )}
                    
                    <div className="small text-muted">
                      <div>Started: {formatDate(stream.startTime)}</div>
                      {stream.endTime && (
                        <div>Ended: {formatDate(stream.endTime)}</div>
                      )}
                      {stream.duration && (
                        <div>Duration: {Math.floor(stream.duration / 60)}m {stream.duration % 60}s</div>
                      )}
                      {stream.viewerCount > 0 && (
                        <div className="viewer-count">
                          <FaEye className="me-1" />
                          Peak: {stream.viewerCount} viewers
                        </div>
                      )}
                    </div>
                  </div>
                ))
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default ChannelDetails;