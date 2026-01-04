import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Button, Form, Alert, Spinner, InputGroup } from 'react-bootstrap';
import { FaPlus, FaSearch, FaSync } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import { channelAPI } from '../services/api';
import ChannelCard from '../components/ChannelCard';

const Channels = () => {
  const navigate = useNavigate();
  const [channels, setChannels] = useState([]);
  const [filteredChannels, setFilteredChannels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    fetchChannels();
  }, []);

  useEffect(() => {
    filterChannels();
  }, [channels, searchTerm, statusFilter]);

  const fetchChannels = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await channelAPI.getAll();
      setChannels(response.data);
    } catch (err) {
      console.error('Error fetching channels:', err);
      setError('Failed to load channels. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const filterChannels = () => {
    let filtered = channels;

    // Filter by search term
    if (searchTerm) {
      filtered = filtered.filter(channel =>
        channel.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (channel.description && channel.description.toLowerCase().includes(searchTerm.toLowerCase()))
      );
    }

    // Filter by status
    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(channel => channel.status === statusFilter);
    }

    setFilteredChannels(filtered);
  };

  const handleCreateChannel = () => {
    navigate('/channels/create');
  };

  const handleChannelEdit = (channel) => {
    navigate(`/channels/${channel.id}`);
  };

  const handleChannelDelete = async (channelId) => {
    if (window.confirm('Are you sure you want to delete this channel? This action cannot be undone.')) {
      try {
        await channelAPI.delete(channelId);
        setChannels(channels.filter(channel => channel.id !== channelId));
      } catch (err) {
        console.error('Error deleting channel:', err);
        setError('Failed to delete channel. Please try again.');
      }
    }
  };

  const handleChannelSync = async (channelId) => {
    try {
      await channelAPI.sync(channelId);
      // Refresh the specific channel data
      const response = await channelAPI.getById(channelId);
      setChannels(channels.map(channel => 
        channel.id === channelId ? response.data : channel
      ));
    } catch (err) {
      console.error('Error syncing channel:', err);
      setError('Failed to sync channel. Please try again.');
    }
  };

  const handleSyncAll = async () => {
    try {
      await channelAPI.syncAll();
      fetchChannels(); // Refresh all channels
    } catch (err) {
      console.error('Error syncing all channels:', err);
      setError('Failed to sync channels. Please try again.');
    }
  };

  const handleSearch = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleStatusFilter = (e) => {
    setStatusFilter(e.target.value);
  };

  if (loading) {
    return (
      <Container>
        <div className="loading-spinner">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading channels...</span>
          </Spinner>
        </div>
      </Container>
    );
  }

  return (
    <Container fluid>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Channels ({filteredChannels.length})</h1>
        <div className="d-flex gap-2">
          <Button variant="outline-primary" onClick={handleSyncAll}>
            <FaSync className="me-2" />
            Sync All
          </Button>
          <Button variant="primary" onClick={handleCreateChannel}>
            <FaPlus className="me-2" />
            Create Channel
          </Button>
        </div>
      </div>

      {error && (
        <Alert variant="danger" dismissible onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Filters */}
      <Row className="mb-4">
        <Col md={6}>
          <InputGroup>
            <InputGroup.Text>
              <FaSearch />
            </InputGroup.Text>
            <Form.Control
              type="text"
              placeholder="Search channels by name or description..."
              value={searchTerm}
              onChange={handleSearch}
            />
          </InputGroup>
        </Col>
        <Col md={3}>
          <Form.Select value={statusFilter} onChange={handleStatusFilter}>
            <option value="ALL">All Statuses</option>
            <option value="LIVE">Live</option>
            <option value="OFFLINE">Offline</option>
            <option value="CREATING">Creating</option>
            <option value="ERROR">Error</option>
          </Form.Select>
        </Col>
      </Row>

      {/* Channels List */}
      {filteredChannels.length === 0 ? (
        <div className="text-center py-5">
          <h4 className="text-muted">No channels found</h4>
          <p className="text-muted">
            {channels.length === 0 
              ? "Create your first channel to get started with live streaming."
              : "Try adjusting your search criteria or filters."
            }
          </p>
          {channels.length === 0 && (
            <Button variant="primary" onClick={handleCreateChannel}>
              <FaPlus className="me-2" />
              Create Your First Channel
            </Button>
          )}
        </div>
      ) : (
        <Row>
          <Col>
            {filteredChannels.map((channel) => (
              <ChannelCard
                key={channel.id}
                channel={channel}
                onEdit={handleChannelEdit}
                onDelete={handleChannelDelete}
                onSync={handleChannelSync}
              />
            ))}
          </Col>
        </Row>
      )}
    </Container>
  );
};

export default Channels;