# Spring Boot Amazon IVS Integration

A comprehensive Spring Boot application that integrates with Amazon Interactive Video Service (IVS) for live streaming capabilities, paired with a React frontend for complete streaming platform management.

## Features

### Backend (Spring Boot)
- **Channel Management**: Create, update, delete, and manage IVS channels
- **Stream Monitoring**: Real-time stream status tracking and viewer analytics
- **AWS IVS Integration**: Full integration with Amazon IVS APIs
- **RESTful APIs**: Complete REST API for frontend consumption
- **Database Integration**: H2 database for development, easily configurable for production
- **Real-time Sync**: Automatic synchronization with AWS IVS service
- **CORS Support**: Configured for React frontend integration

### Frontend (React)
- **Dashboard**: Overview of channels, streams, and viewer statistics
- **Channel Management**: Create and manage streaming channels
- **Live Stream Viewer**: Built-in video player using Amazon IVS Player SDK
- **Real-time Updates**: Auto-refreshing live stream data
- **Responsive Design**: Bootstrap-based responsive UI
- **Stream Analytics**: Viewer count and stream health monitoring

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React App     │    │  Spring Boot    │    │   Amazon IVS    │
│   (Frontend)    │◄──►│   (Backend)     │◄──►│   (AWS Cloud)   │
│                 │    │                 │    │                 │
│ - Dashboard     │    │ - REST APIs     │    │ - Live Streams  │
│ - Channel Mgmt  │    │ - IVS Client    │    │ - Channel Mgmt  │
│ - Video Player  │    │ - Database      │    │ - Stream Keys   │
│ - Analytics     │    │ - Sync Service  │    │ - Playback URLs │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Prerequisites

- Java 17+
- Maven 3.6+
- Node.js 16+
- npm or yarn
- AWS Account with IVS access
- AWS CLI configured (optional)

## AWS Setup

### 1. Create AWS Account and Configure IVS
1. Sign up for AWS account
2. Enable Amazon IVS service
3. Create IAM user with IVS permissions
4. Generate access keys

### 2. Required IAM Permissions
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ivs:*"
            ],
            "Resource": "*"
        }
    ]
}
```

## Backend Setup (Spring Boot)

### 1. Configure AWS Credentials
Set environment variables:
```bash
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export AWS_REGION=us-east-1
```

Or update `application.yml`:
```yaml
aws:
  ivs:
    access-key: your-access-key
    secret-key: your-secret-key
    region: us-east-1
```

### 2. Run the Backend
```bash
cd spring-boot-amazon-ivs
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Verify Backend
```bash
curl http://localhost:8080/api/channels
```

## Frontend Setup (React)

### 1. Install Dependencies
```bash
cd ivs-frontend-react
npm install
```

### 2. Configure API URL (Optional)
Create `.env` file:
```
REACT_APP_API_URL=http://localhost:8080/api
```

### 3. Run the Frontend
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## API Documentation

### Channel Endpoints

#### Create Channel
```http
POST /api/channels
Content-Type: application/json

{
    "name": "My Live Channel",
    "description": "Channel description",
    "type": "STANDARD",
    "latencyMode": "LOW",
    "authorized": false
}
```

#### Get All Channels
```http
GET /api/channels
```

#### Get Channel by ID
```http
GET /api/channels/{id}
```

#### Get Live Channels
```http
GET /api/channels/live
```

#### Update Channel
```http
PUT /api/channels/{id}
Content-Type: application/json

{
    "name": "Updated Channel Name",
    "description": "Updated description"
}
```

#### Delete Channel
```http
DELETE /api/channels/{id}
```

#### Sync Channel Status
```http
POST /api/channels/{id}/sync
```

### Stream Endpoints

#### Get Stream by Channel ARN
```http
GET /api/streams/channel/{channelArn}
```

#### Get Live Streams
```http
GET /api/streams/live
```

#### Get Stream History
```http
GET /api/streams/channel/{channelArn}/history?limit=10
```

#### Stop Stream
```http
POST /api/streams/channel/{channelArn}/stop
```

#### Update Stream Metadata
```http
PUT /api/streams/{id}/metadata
Content-Type: application/json

{
    "title": "Stream Title",
    "description": "Stream Description"
}
```

## Usage Guide

### 1. Create Your First Channel
1. Open the React app at `http://localhost:3000`
2. Navigate to "Create Channel"
3. Fill in channel details
4. Click "Create Channel"

### 2. Start Streaming
1. Use the provided **Ingest Endpoint** and **Stream Key**
2. Configure your streaming software (OBS, etc.):
   - **Server**: Ingest Endpoint
   - **Stream Key**: Stream Key from channel details
3. Start streaming

### 3. View Live Stream
1. Once streaming starts, the channel status will show "LIVE"
2. Click "Watch" to view the stream
3. The video player will automatically load the stream

### 4. Monitor Analytics
- View real-time viewer count
- Monitor stream health
- Track stream duration
- View stream history

## Streaming Software Configuration

### OBS Studio Setup
1. Open OBS Studio
2. Go to Settings → Stream
3. Set Service to "Custom"
4. Set Server to your channel's Ingest Endpoint
5. Set Stream Key to your channel's Stream Key
6. Click OK and start streaming

### FFmpeg Example
```bash
ffmpeg -re -i input.mp4 -c:v libx264 -c:a aac -f flv rtmp://your-ingest-endpoint/live/your-stream-key
```

## Development

### Backend Development
- **Database**: H2 in-memory database (development)
- **Profiles**: Configure different profiles for dev/prod
- **Logging**: Detailed logging for debugging
- **Testing**: Unit tests included

### Frontend Development
- **Hot Reload**: Automatic refresh during development
- **API Integration**: Axios for HTTP requests
- **State Management**: React hooks
- **Responsive Design**: Bootstrap components

## Production Deployment

### Backend (Spring Boot)
1. **Database**: Configure PostgreSQL/MySQL
2. **Environment Variables**: Set production AWS credentials
3. **Security**: Enable HTTPS and authentication
4. **Monitoring**: Add application monitoring

### Frontend (React)
1. **Build**: `npm run build`
2. **Deploy**: Deploy to CDN/static hosting
3. **Environment**: Configure production API URL

### Docker Deployment
```dockerfile
# Backend Dockerfile
FROM openjdk:17-jdk-slim
COPY target/spring-boot-amazon-ivs-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```dockerfile
# Frontend Dockerfile
FROM node:16-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

## Troubleshooting

### Common Issues

#### 1. AWS Credentials Error
```
Error: Unable to load AWS credentials
```
**Solution**: Verify AWS credentials are properly configured

#### 2. CORS Error
```
Access to XMLHttpRequest blocked by CORS policy
```
**Solution**: Ensure backend CORS configuration includes frontend URL

#### 3. Stream Not Loading
```
Failed to load stream
```
**Solution**: 
- Verify channel is live
- Check playback URL
- Ensure IVS Player SDK is loaded

#### 4. Channel Creation Failed
```
Failed to create channel
```
**Solution**:
- Check AWS permissions
- Verify region configuration
- Check AWS service limits

### Debug Mode
Enable debug logging:
```yaml
logging:
  level:
    com.example.ivs: DEBUG
    software.amazon.awssdk: DEBUG
```

## Performance Optimization

### Backend
- Connection pooling for database
- Async processing for AWS calls
- Caching for frequently accessed data
- Rate limiting for API endpoints

### Frontend
- Lazy loading for components
- Memoization for expensive operations
- Optimized re-renders
- CDN for static assets

## Security Considerations

### Backend Security
- Input validation
- SQL injection prevention
- Rate limiting
- HTTPS enforcement
- Authentication/Authorization

### Frontend Security
- XSS prevention
- Secure API communication
- Environment variable protection
- Content Security Policy

## Monitoring and Analytics

### Metrics to Track
- Channel creation rate
- Stream duration
- Viewer engagement
- Error rates
- API response times

### Logging
- Structured logging
- Error tracking
- Performance monitoring
- User activity logs

## Contributing

1. Fork the repository
2. Create feature branch
3. Make changes
4. Add tests
5. Submit pull request

## License

This project is licensed under the MIT License.

## Support

For issues and questions:
1. Check troubleshooting section
2. Review AWS IVS documentation
3. Create GitHub issue
4. Contact support team