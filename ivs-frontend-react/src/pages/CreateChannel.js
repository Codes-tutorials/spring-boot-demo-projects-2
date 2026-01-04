import React, { useState } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { FaSave, FaArrowLeft } from 'react-icons/fa';
import { channelAPI } from '../services/api';

const CreateChannel = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    type: 'STANDARD',
    latencyMode: 'LOW',
    authorized: false,
    recordingConfigurationArn: '',
    tags: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      setError('Channel name is required');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setSuccess(null);

      const response = await channelAPI.create(formData);
      
      setSuccess('Channel created successfully!');
      
      // Redirect to channel details after a short delay
      setTimeout(() => {
        navigate(`/channels/${response.data.id}`);
      }, 2000);

    } catch (err) {
      console.error('Error creating channel:', err);
      setError(err.response?.data?.message || 'Failed to create channel. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/channels');
  };

  return (
    <Container>
      <div className="d-flex align-items-center mb-4">
        <Button variant="outline-secondary" onClick={handleCancel} className="me-3">
          <FaArrowLeft />
        </Button>
        <h1>Create New Channel</h1>
      </div>

      <Row className="justify-content-center">
        <Col md={8}>
          <Card className="channel-form">
            <Card.Body>
              {error && (
                <Alert variant="danger" dismissible onClose={() => setError(null)}>
                  {error}
                </Alert>
              )}

              {success && (
                <Alert variant="success">
                  {success}
                </Alert>
              )}

              <Form onSubmit={handleSubmit}>
                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Channel Name *</Form.Label>
                      <Form.Control
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleInputChange}
                        placeholder="Enter channel name"
                        required
                        maxLength={128}
                      />
                      <Form.Text className="text-muted">
                        Maximum 128 characters
                      </Form.Text>
                    </Form.Group>
                  </Col>
                  
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Channel Type</Form.Label>
                      <Form.Select
                        name="type"
                        value={formData.type}
                        onChange={handleInputChange}
                      >
                        <option value="BASIC">Basic</option>
                        <option value="STANDARD">Standard</option>
                      </Form.Select>
                      <Form.Text className="text-muted">
                        Standard channels support higher quality and more features
                      </Form.Text>
                    </Form.Group>
                  </Col>
                </Row>

                <Form.Group className="mb-3">
                  <Form.Label>Description</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    placeholder="Enter channel description (optional)"
                    maxLength={1000}
                  />
                  <Form.Text className="text-muted">
                    Maximum 1000 characters
                  </Form.Text>
                </Form.Group>

                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Latency Mode</Form.Label>
                      <Form.Select
                        name="latencyMode"
                        value={formData.latencyMode}
                        onChange={handleInputChange}
                      >
                        <option value="NORMAL">Normal</option>
                        <option value="LOW">Low</option>
                      </Form.Select>
                      <Form.Text className="text-muted">
                        Low latency reduces delay but may affect quality
                      </Form.Text>
                    </Form.Group>
                  </Col>
                  
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Check
                        type="checkbox"
                        name="authorized"
                        checked={formData.authorized}
                        onChange={handleInputChange}
                        label="Authorized Channel"
                      />
                      <Form.Text className="text-muted">
                        Requires authentication to stream
                      </Form.Text>
                    </Form.Group>
                  </Col>
                </Row>

                <Form.Group className="mb-3">
                  <Form.Label>Recording Configuration ARN (Optional)</Form.Label>
                  <Form.Control
                    type="text"
                    name="recordingConfigurationArn"
                    value={formData.recordingConfigurationArn}
                    onChange={handleInputChange}
                    placeholder="arn:aws:ivs:region:account:recording-configuration/..."
                  />
                  <Form.Text className="text-muted">
                    ARN of the recording configuration to use for this channel
                  </Form.Text>
                </Form.Group>

                <Form.Group className="mb-4">
                  <Form.Label>Tags (Optional)</Form.Label>
                  <Form.Control
                    type="text"
                    name="tags"
                    value={formData.tags}
                    onChange={handleInputChange}
                    placeholder="tag1,tag2,tag3"
                  />
                  <Form.Text className="text-muted">
                    Comma-separated tags for organizing channels
                  </Form.Text>
                </Form.Group>

                <div className="d-flex justify-content-end gap-2">
                  <Button variant="outline-secondary" onClick={handleCancel} disabled={loading}>
                    Cancel
                  </Button>
                  <Button type="submit" variant="primary" disabled={loading}>
                    {loading ? (
                      <>
                        <Spinner animation="border" size="sm" className="me-2" />
                        Creating...
                      </>
                    ) : (
                      <>
                        <FaSave className="me-2" />
                        Create Channel
                      </>
                    )}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default CreateChannel;