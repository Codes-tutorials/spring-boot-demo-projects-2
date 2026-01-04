import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Alert, Spinner, Button } from 'react-bootstrap';
import { FaVideo, FaBroadcastTower, FaEye, FaSync } from 'react-icons/fa';
import { dashboardAPI, channelAPI, streamAPI } from '../services/api';
import ChannelCard from '../components/ChannelCard';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalChannels: 0,
    liveChannels: 0,
    liveStreams: 0,
    totalViewers: 0,
  });
  const [recentChannels, setRecentChannels] = useState([]);
  const [liveStreams, setLiveStreams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      // Fetch dashboard stats
      const statsData = await dashboardAPI.getStats();
      setStats(statsData);

      // Fetch recent channels
      const channelsResponse = await channelAPI.getAll();
      setRecentChannels(channelsResponse.data.slice(0, 5)); // Show only 5 recent channels

      // Fetch live streams
      const liveStreamsResponse = await streamAPI.getLive();
      setLiveStreams(liveStreamsResponse.data.slice(0, 3)); // Show only 3 live streams

    } catch (err) {
      console.error('Error fetching dashboard data:', err);
      setError('Failed to load dashboard data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSyncAll = async () => {
    try {
      await channelAPI.syncAll();
      await streamAPI.syncAll();
      fetchDashboardData(); // Refresh data after sync
    } catch (err) {
      console.error('Error syncing data:', err);
      setError('Failed to sync data. Please try again.');
    }
  };

  const handleChannelEdit = (channel) => {
    // Navigate to edit channel page
    window.location.href = `/channels/${channel.id}`;
  };

  const handleChannelDelete = async (channelId) => {
    if (window.confirm('Are you sure you want to delete this channel?')) {
      try {
        await channelAPI.delete(channelId);
        fetchDashboardData(); // Refresh data after deletion
      } catch (err) {
        console.error('Error deleting channel:', err);
        setError('Failed to delete channel. Please try again.');
      }
    }
  };

  const handleChannelSync = async (channelId) => {
    try {
      await channelAPI.sync(channelId);
      fetchDashboardData(); // Refresh data after sync
    } catch (err) {
      console.error('Error syncing channel:', err);
      setError('Failed to sync channel. Please try again.');
    }
  };

  if (loading) {
    return (
      <Container>
        <div className="loading-spinner">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading dashboard...</span>
          </Spinner>
        </div>
      </Container>
    );
  }

  return (
    <Container fluid>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Dashboard</h1>
        <Button variant="outline-primary" onClick={handleSyncAll}>
          <FaSync className="me-2" />
          Sync All
        </Button>
      </div>

      {error && (
        <Alert variant="danger" dismissible onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Stats Cards */}
      <Row className="mb-4">
        <Col md={3}>
          <Card className="stats-card text-center">
            <Card.Body>
              <FaVideo size={40} className="mb-2" />
              <div className="stats-number">{stats.totalChannels}</div>
              <div>Total Channels</div>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="stats-card text-center">
            <Card.Body>
              <FaBroadcastTower size={40} className="mb-2" />
              <div className="stats-number">{stats.liveChannels}</div>
              <div>Live Channels</div>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="stats-card text-center">
            <Card.Body>
              <FaBroadcastTower size={40} className="mb-2" />
              <div className="stats-number">{stats.liveStreams}</div>
              <div>Active Streams</div>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="stats-card text-center">
            <Card.Body>
              <FaEye size={40} className="mb-2" />
              <div className="stats-number">{stats.totalViewers}</div>
              <div>Total Viewers</div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        {/* Recent Channels */}
        <Col md={8}>
          <Card>
            <Card.Header>
              <h5 className="mb-0">Recent Channels</h5>
            </Card.Header>
            <Card.Body>
              {recentChannels.length === 0 ? (
                <p className="text-muted">No channels found. Create your first channel to get started.</p>
              ) : (
                recentChannels.map((channel) => (
                  <ChannelCard
                    key={channel.id}
                    channel={channel}
                    onEdit={handleChannelEdit}
                    onDelete={handleChannelDelete}
                    onSync={handleChannelSync}
                  />
                ))
              )}
            </Card.Body>
          </Card>
        </Col>

        {/* Live Streams */}
        <Col md={4}>
          <Card>
            <Card.Header>
              <h5 className="mb-0">Live Streams</h5>
            </Card.Header>
            <Card.Body>
              {liveStreams.length === 0 ? (
                <p className="text-muted">No live streams currently active.</p>
              ) : (
                liveStreams.map((stream) => (
                  <div key={stream.id} className="mb-3 p-3 border rounded">
                    <div className="d-flex justify-content-between align-items-center">
                      <div>
                        <strong>{stream.title || 'Untitled Stream'}</strong>
                        <div className="text-muted small">
                          Channel: {stream.channelArn.split('/').pop()}
                        </div>
                        <div className="viewer-count">
                          <FaEye className="me-1" />
                          {stream.viewerCount || 0} viewers
                        </div>
                      </div>
                      <Button
                        variant="success"
                        size="sm"
                        onClick={() => window.location.href = `/player/${encodeURIComponent(stream.channelArn)}`}
                      >
                        Watch
                      </Button>
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

export default Dashboard;