import React from 'react';
import { Card, Badge, Button, Row, Col } from 'react-bootstrap';
import { FaEye, FaPlay, FaEdit, FaTrash, FaSync } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import moment from 'moment';

const ChannelCard = ({ channel, onEdit, onDelete, onSync }) => {
  const navigate = useNavigate();

  const handleWatch = () => {
    navigate(`/player/${encodeURIComponent(channel.channelArn)}`);
  };

  const handleViewDetails = () => {
    navigate(`/channels/${channel.id}`);
  };

  const getStatusBadge = () => {
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
    return dateString ? moment(dateString).format('MMM DD, YYYY HH:mm') : 'Never';
  };

  return (
    <Card className="stream-card mb-3">
      <Card.Body>
        <Row>
          <Col md={8}>
            <Card.Title className="d-flex align-items-center">
              {channel.name}
              <span className="ms-2">{getStatusBadge()}</span>
            </Card.Title>
            
            <Card.Text className="text-muted mb-2">
              {channel.description || 'No description available'}
            </Card.Text>
            
            <div className="mb-2">
              <small className="text-muted">
                <strong>Type:</strong> {channel.type} | 
                <strong> Latency:</strong> {channel.latencyMode} |
                <strong> Created:</strong> {formatDate(channel.createdAt)}
              </small>
            </div>
            
            {channel.status === 'LIVE' && (
              <div className="viewer-count">
                <FaEye className="me-1" />
                {channel.currentViewers || 0} viewers
              </div>
            )}
            
            {channel.lastStreamStartTime && (
              <div>
                <small className="text-muted">
                  Last stream: {formatDate(channel.lastStreamStartTime)}
                </small>
              </div>
            )}
          </Col>
          
          <Col md={4} className="text-end">
            <div className="d-flex flex-column gap-2">
              {channel.status === 'LIVE' && (
                <Button 
                  variant="success" 
                  size="sm" 
                  onClick={handleWatch}
                  className="d-flex align-items-center justify-content-center"
                >
                  <FaPlay className="me-1" />
                  Watch
                </Button>
              )}
              
              <Button 
                variant="outline-primary" 
                size="sm" 
                onClick={handleViewDetails}
              >
                View Details
              </Button>
              
              <div className="d-flex gap-1">
                <Button 
                  variant="outline-secondary" 
                  size="sm" 
                  onClick={() => onEdit(channel)}
                  title="Edit Channel"
                >
                  <FaEdit />
                </Button>
                
                <Button 
                  variant="outline-info" 
                  size="sm" 
                  onClick={() => onSync(channel.id)}
                  title="Sync Status"
                >
                  <FaSync />
                </Button>
                
                <Button 
                  variant="outline-danger" 
                  size="sm" 
                  onClick={() => onDelete(channel.id)}
                  title="Delete Channel"
                >
                  <FaTrash />
                </Button>
              </div>
            </div>
          </Col>
        </Row>
      </Card.Body>
    </Card>
  );
};

export default ChannelCard;