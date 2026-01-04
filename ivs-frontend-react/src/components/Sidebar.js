import React from 'react';
import { Nav } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { useLocation } from 'react-router-dom';
import { 
  FaTachometerAlt, 
  FaVideo, 
  FaPlus, 
  FaBroadcastTower,
  FaChartBar 
} from 'react-icons/fa';

const Sidebar = () => {
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <div className="sidebar">
      <Nav className="flex-column">
        <LinkContainer to="/">
          <Nav.Link className={isActive('/') ? 'active' : ''}>
            <FaTachometerAlt className="me-2" />
            Dashboard
          </Nav.Link>
        </LinkContainer>
        
        <LinkContainer to="/channels">
          <Nav.Link className={isActive('/channels') ? 'active' : ''}>
            <FaVideo className="me-2" />
            Channels
          </Nav.Link>
        </LinkContainer>
        
        <LinkContainer to="/channels/create">
          <Nav.Link className={isActive('/channels/create') ? 'active' : ''}>
            <FaPlus className="me-2" />
            Create Channel
          </Nav.Link>
        </LinkContainer>
        
        <LinkContainer to="/live-streams">
          <Nav.Link className={isActive('/live-streams') ? 'active' : ''}>
            <FaBroadcastTower className="me-2" />
            Live Streams
          </Nav.Link>
        </LinkContainer>
      </Nav>
    </div>
  );
};

export default Sidebar;