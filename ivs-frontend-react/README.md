# Amazon IVS Frontend - React Application

A modern React application for managing Amazon Interactive Video Service (IVS) live streaming channels and viewing streams.

## Features

- **Dashboard**: Overview of channels, streams, and analytics
- **Channel Management**: Create, edit, and delete streaming channels
- **Live Stream Viewer**: Built-in video player using Amazon IVS Player SDK
- **Real-time Updates**: Auto-refreshing stream data and viewer counts
- **Responsive Design**: Mobile-friendly Bootstrap-based UI
- **Stream Analytics**: Monitor viewer engagement and stream health

## Technology Stack

- **React 18**: Modern React with hooks
- **React Router**: Client-side routing
- **Bootstrap 5**: Responsive UI components
- **React Bootstrap**: Bootstrap components for React
- **Axios**: HTTP client for API calls
- **Amazon IVS Player**: Official IVS video player SDK
- **React Icons**: Icon library
- **Moment.js**: Date/time formatting

## Prerequisites

- Node.js 16 or higher
- npm or yarn
- Running Spring Boot backend (see backend README)

## Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ivs-frontend-react
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment (optional)**
   Create `.env` file:
   ```
   REACT_APP_API_URL=http://localhost:8080/api
   ```

4. **Start the development server**
   ```bash
   npm start
   ```

   The app will open at `http://localhost:3000`

## Project Structure

```
src/
├── components/          # Reusable React components
│   ├── Navigation.js    # Top navigation bar
│   ├── Sidebar.js       # Side navigation menu
│   ├── ChannelCard.js   # Channel display component
│   └── VideoPlayer.js   # IVS video player wrapper
├── pages/               # Page components
│   ├── Dashboard.js     # Main dashboard
│   ├── Channels.js      # Channel list page
│   ├── CreateChannel.js # Channel creation form
│   ├── ChannelDetails.js# Channel details page
│   ├── LiveStreams.js   # Live streams page
│   └── StreamPlayer.js  # Stream viewing page
├── services/            # API service layer
│   └── api.js          # API client and endpoints
├── App.js              # Main app component
├── index.js            # App entry point
└── index.css           # Global styles
```

## Available Scripts

### `npm start`
Runs the app in development mode. Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

### `npm test`
Launches the test runner in interactive watch mode.

### `npm run build`
Builds the app for production to the `build` folder.

### `npm run eject`
**Note: this is a one-way operation. Once you `eject`, you can't go back!**

## API Integration

The frontend communicates with the Spring Boot backend through RESTful APIs:

### Channel Operations
- `GET /api/channels` - List all channels
- `POST /api/channels` - Create new channel
- `GET /api/channels/{id}` - Get channel details
- `PUT /api/channels/{id}` - Update channel
- `DELETE /api/channels/{id}` - Delete channel

### Stream Operations
- `GET /api/streams/live` - Get live streams
- `GET /api/streams/channel/{arn}` - Get stream by channel
- `POST /api/streams/channel/{arn}/stop` - Stop stream

## Components Guide

### VideoPlayer Component
Wraps the Amazon IVS Player SDK for seamless video playback:

```jsx
import VideoPlayer from './components/VideoPlayer';

<VideoPlayer 
  playbackUrl="https://your-playback-url.m3u8"
  autoplay={true}
  muted={false}
/>
```

### ChannelCard Component
Displays channel information with action buttons:

```jsx
import ChannelCard from './components/ChannelCard';

<ChannelCard 
  channel={channelData}
  onEdit={handleEdit}
  onDelete={handleDelete}
  onSync={handleSync}
/>
```

## Features in Detail

### Dashboard
- Real-time statistics (channels, streams, viewers)
- Recent channels overview
- Live streams preview
- Quick navigation to key features

### Channel Management
- Create channels with custom settings
- View detailed channel information
- Copy streaming credentials
- Monitor channel status and analytics

### Live Stream Viewer
- High-quality video playback using IVS Player
- Real-time viewer count
- Stream health monitoring
- Responsive video player

### Real-time Updates
- Auto-refresh every 30 seconds for live data
- Manual refresh options
- Real-time viewer count updates
- Stream status synchronization

## Styling and Theming

The application uses Bootstrap 5 with custom CSS:

### Custom CSS Classes
- `.live-indicator` - Animated live badge
- `.viewer-count` - Styled viewer count display
- `.stream-card` - Channel/stream card styling
- `.video-player-container` - Video player wrapper

### Color Scheme
- Primary: Bootstrap blue (#0d6efd)
- Success: Green for live indicators (#198754)
- Danger: Red for live badges (#dc3545)
- Secondary: Gray for offline status (#6c757d)

## Development Guidelines

### Code Style
- Use functional components with hooks
- Follow React best practices
- Use meaningful component and variable names
- Add PropTypes for component props (recommended)

### State Management
- Use React hooks (useState, useEffect)
- Lift state up when needed
- Consider Context API for global state

### Error Handling
- Implement try-catch blocks for API calls
- Show user-friendly error messages
- Provide fallback UI for failed states

### Performance
- Use React.memo for expensive components
- Implement proper dependency arrays in useEffect
- Avoid unnecessary re-renders

## Deployment

### Development Build
```bash
npm run build
```

### Production Deployment Options

#### 1. Static Hosting (Netlify, Vercel)
```bash
npm run build
# Deploy the 'build' folder
```

#### 2. Docker Deployment
```dockerfile
FROM node:16-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

#### 3. AWS S3 + CloudFront
```bash
npm run build
aws s3 sync build/ s3://your-bucket-name
```

## Environment Configuration

### Development (.env.development)
```
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENV=development
```

### Production (.env.production)
```
REACT_APP_API_URL=https://your-api-domain.com/api
REACT_APP_ENV=production
```

## Troubleshooting

### Common Issues

#### 1. API Connection Error
```
Network Error: Request failed
```
**Solution**: Verify backend is running on correct port

#### 2. CORS Error
```
Access blocked by CORS policy
```
**Solution**: Ensure backend CORS configuration includes frontend URL

#### 3. Video Player Not Loading
```
IVS Player not supported
```
**Solution**: Check browser compatibility and IVS Player SDK loading

#### 4. Build Errors
```
Module not found
```
**Solution**: Run `npm install` to ensure all dependencies are installed

### Debug Mode
Enable React debug mode:
```bash
REACT_APP_DEBUG=true npm start
```

## Browser Support

- Chrome 70+
- Firefox 65+
- Safari 12+
- Edge 79+

Note: Amazon IVS Player has specific browser requirements for optimal performance.

## Performance Optimization

### Bundle Size Optimization
- Use React.lazy for code splitting
- Implement tree shaking
- Optimize images and assets
- Use CDN for static resources

### Runtime Performance
- Implement virtual scrolling for large lists
- Use React.memo for expensive components
- Optimize re-renders with useMemo and useCallback
- Implement proper loading states

## Testing

### Unit Testing
```bash
npm test
```

### Integration Testing
- Test API integration
- Test user workflows
- Test responsive design

### E2E Testing (Recommended)
Consider adding Cypress or Playwright for end-to-end testing.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review React and IVS documentation
3. Create a GitHub issue