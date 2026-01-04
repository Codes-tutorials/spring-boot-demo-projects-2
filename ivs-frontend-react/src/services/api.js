import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    // Add auth token if available
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Channel API
export const channelAPI = {
  // Get all channels
  getAll: () => api.get('/channels'),
  
  // Get channel by ID
  getById: (id) => api.get(`/channels/${id}`),
  
  // Get channel by ARN
  getByArn: (channelArn) => api.get(`/channels/arn/${encodeURIComponent(channelArn)}`),
  
  // Get live channels
  getLive: () => api.get('/channels/live'),
  
  // Search channels
  search: (keyword) => api.get(`/channels/search?keyword=${encodeURIComponent(keyword)}`),
  
  // Create channel
  create: (channelData) => api.post('/channels', channelData),
  
  // Update channel
  update: (id, channelData) => api.put(`/channels/${id}`, channelData),
  
  // Delete channel
  delete: (id) => api.delete(`/channels/${id}`),
  
  // Sync channel status
  sync: (id) => api.post(`/channels/${id}/sync`),
  
  // Sync all channels
  syncAll: () => api.post('/channels/sync-all'),
};

// Stream API
export const streamAPI = {
  // Get stream by channel ARN
  getByChannelArn: (channelArn) => api.get(`/streams/channel/${encodeURIComponent(channelArn)}`),
  
  // Get all streams by channel ARN
  getAllByChannelArn: (channelArn) => api.get(`/streams/channel/${encodeURIComponent(channelArn)}/all`),
  
  // Get stream history
  getHistory: (channelArn, limit = 10) => api.get(`/streams/channel/${encodeURIComponent(channelArn)}/history?limit=${limit}`),
  
  // Get live streams
  getLive: () => api.get('/streams/live'),
  
  // Get stream by ID
  getById: (id) => api.get(`/streams/${id}`),
  
  // Update stream metadata
  updateMetadata: (id, metadata) => api.put(`/streams/${id}/metadata`, metadata),
  
  // Stop stream
  stop: (channelArn) => api.post(`/streams/channel/${encodeURIComponent(channelArn)}/stop`),
  
  // Sync all streams
  syncAll: () => api.post('/streams/sync-all'),
};

// Dashboard API
export const dashboardAPI = {
  // Get dashboard stats
  getStats: async () => {
    try {
      const [channelsResponse, liveChannelsResponse, liveStreamsResponse] = await Promise.all([
        channelAPI.getAll(),
        channelAPI.getLive(),
        streamAPI.getLive(),
      ]);
      
      const totalViewers = liveChannelsResponse.data.reduce((sum, channel) => sum + (channel.currentViewers || 0), 0);
      
      return {
        totalChannels: channelsResponse.data.length,
        liveChannels: liveChannelsResponse.data.length,
        liveStreams: liveStreamsResponse.data.length,
        totalViewers: totalViewers,
      };
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
      throw error;
    }
  },
};

export default api;