import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Container, Row, Col } from 'react-bootstrap';
import Navigation from './components/Navigation';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';
import Channels from './pages/Channels';
import CreateChannel from './pages/CreateChannel';
import ChannelDetails from './pages/ChannelDetails';
import LiveStreams from './pages/LiveStreams';
import StreamPlayer from './pages/StreamPlayer';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Navigation />
        <Container fluid>
          <Row>
            <Col md={2} className="p-0">
              <Sidebar />
            </Col>
            <Col md={10} className="main-content">
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/channels" element={<Channels />} />
                <Route path="/channels/create" element={<CreateChannel />} />
                <Route path="/channels/:id" element={<ChannelDetails />} />
                <Route path="/live-streams" element={<LiveStreams />} />
                <Route path="/player/:channelArn" element={<StreamPlayer />} />
              </Routes>
            </Col>
          </Row>
        </Container>
      </div>
    </Router>
  );
}

export default App;